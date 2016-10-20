/*
 * Copyright 2014-2016 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.VersionCodes;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.module.project.converter.DummyConverter;
import com.kotcrab.vis.editor.module.project.converter.ProjectConverter;
import com.kotcrab.vis.editor.util.CopyFileVisitor;
import com.kotcrab.vis.editor.util.async.Async;
import com.kotcrab.vis.editor.util.async.AsyncTaskAdapter;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.editor.util.vis.WikiPages;
import com.kotcrab.vis.ui.util.async.AsyncTask;
import com.kotcrab.vis.ui.util.async.AsyncTaskProgressDialog;
import com.kotcrab.vis.ui.util.async.SteppedAsyncTask;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Module allowing to perform IO operation with projects
 * @author Kotcrab
 */
public class ProjectIOModule extends EditorModule {
	//confirm dialog buttons
	private static final int OK = 0;
	private static final int CONVERTING_HELP = 1;

	private static final String TAG = "ProjectIOModule";

	public static final String PROJECT_FILE = "project.json";

	private StatusBarModule statusBar;
	private GsonModule gsonModule;

	private Stage stage;

	private Gson gson;

	private Array<ProjectConverter> projectConverters = new Array<>();

	@Override
	public void init () {
		gson = gsonModule.getCommonGson();

		//TODO: [plugins] plugin entry point

		projectConverters.add(new DummyConverter(VersionCodes.EDITOR_030, VersionCodes.EDITOR_040));
		projectConverters.add(new DummyConverter(VersionCodes.EDITOR_031, VersionCodes.EDITOR_040));
		projectConverters.add(new DummyConverter(VersionCodes.EDITOR_032, VersionCodes.EDITOR_040));
		projectConverters.add(new DummyConverter(VersionCodes.EDITOR_033, VersionCodes.EDITOR_040));
		projectConverters.add(new DummyConverter(VersionCodes.EDITOR_034, VersionCodes.EDITOR_040));

		for (ProjectConverter converter : projectConverters)
			container.injectModules(converter);
	}

	public void loadHandleError (Stage stage, FileHandle projectRoot) {
		try {
			load(projectRoot);
		} catch (EditorException e) {
			Dialogs.showErrorDialog(stage, e.getMessage(), e);
			Log.exception(e);
		}
	}

	public void load (FileHandle projectRoot) throws EditorException {
		if (projectRoot.exists() == false) throw new EditorException("Selected folder does not exist!");
		if (projectRoot.name().equals(PROJECT_FILE)) {
			loadProject(projectRoot);
			return;
		}

		if (projectRoot.name().equals("vis") && projectRoot.isDirectory()) {
			loadProject(projectRoot.child(PROJECT_FILE));
			return;
		}

		if (projectRoot.child(PROJECT_FILE).exists()) {
			loadProject(projectRoot.child(PROJECT_FILE));
			return;
		}

		FileHandle visFolder = projectRoot.child("vis");
		if (visFolder.child(PROJECT_FILE).exists()) {
			loadProject(visFolder.child(PROJECT_FILE));
			return;
		}

		throw new EditorException("Selected folder is not a Vis project!");
	}

	private void loadProject (FileHandle dataFile) throws EditorException {
		if (dataFile.exists() == false) throw new EditorException("Project file does not exist!");

		FileHandle versionFile = dataFile.parent().child("modules").child("version.json");

		if (versionFile.exists()) {
			ProjectVersionDescriptor descriptor = ProjectVersionModule.getNewJson().fromJson(ProjectVersionDescriptor.class, versionFile);

			if (descriptor.versionCode < 20) {
				String[] buttons = {"How to convert project", "OK"};
				Integer[] returns = {CONVERTING_HELP, OK};
				Dialogs.showConfirmDialog(stage, "Warning", "This project uses old project format and must be converted before loading." +
						"\nSee help page for more details.", buttons, returns, result -> {
					if (result == CONVERTING_HELP) WikiPages.CONVERTING_FROM_VISEDITOR_02X.open();
				});

				return;
			}

			if (descriptor.versionCode > App.VERSION_CODE) {
				Dialogs.showOptionDialog(stage, "Warning",
						"This project was opened in newer version of VisEditor.\nSome functions may not work properly. Do you want to continue?",
						OptionDialogType.YES_NO, new OptionDialogAdapter() {
							@Override
							public void yes () {
								doLoadProject(dataFile);
							}
						});
			} else if (descriptor.versionCode < App.VERSION_CODE) {
				backupProject(dataFile, descriptor.versionCode, new AsyncTaskAdapter() {
					@Override
					public void finished () {
						convertAndLoad(dataFile, descriptor.versionCode);
					}
				});
			} else
				doLoadProject(dataFile);
		} else //if there is no version file that means that project was just created
			doLoadProject(dataFile);

	}

	private void backupProject (FileHandle dataFile, int oldVersionCode, AsyncTaskAdapter listener) {
		Project project = readProjectDataFile(dataFile);
		FileHandle backupRoot = project.getVisDirectory();
		FileHandle backupOut = backupRoot.child("modules").child(".conversionBackup");
		backupOut.mkdirs();

		FileHandle backupArchive = backupOut.child("before-conversion-from-" + oldVersionCode + "-to-" + App.VERSION_CODE + ".zip");

		AsyncTaskProgressDialog taskDialog = Async.startTask(stage, "Creating backup", new CreateProjectBackupAsyncTask(backupRoot, backupArchive));
		taskDialog.addListener(listener);
	}

	private void convertAndLoad (FileHandle dataFile, int versionCode) {
		if (versionCode == App.VERSION_CODE) {
			doLoadProject(dataFile);
			return;
		}

		for (ProjectConverter converter : projectConverters) {
			if (converter.getFromVersion() == versionCode) {
				Log.info(TAG, "Running converter from versionCode " + converter.getFromVersion() + " to versionCode " + converter.getToVersion());
				AsyncTask task = converter.getConversionTask(dataFile);
				if (task == null) {
					convertAndLoad(dataFile, converter.getToVersion());
					return;
				}

				AsyncTaskProgressDialog taskDialog = Async.startTask(stage, "Converting project", task);
				taskDialog.addListener(new AsyncTaskAdapter() {
					@Override
					public void finished () {
						convertAndLoad(dataFile, converter.getToVersion());
					}
				});
				return;
			}
		}
	}

	public Project readProjectDataFile (FileHandle dataFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile.file()));
			Project project = gson.fromJson(reader, Project.class);
			reader.close();

			project.updateRoot(dataFile);

			return project;
		} catch (IOException e) {
			Log.exception(e);
			throw new IllegalStateException(e);
		}
	}

	private void doLoadProject (FileHandle dataFile) {
		Project project = readProjectDataFile(dataFile);
		Editor.instance.projectLoaded(project);
	}

	public void createLibGDXProject (final ProjectLibGDX project) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void doInBackground () {
				setMessage("Creating directory structure...");

				FileHandle projectRoot = Gdx.files.absolute(project.getRoot());
				FileHandle standardAssetsDir = project.getAssetOutputDirectory();
				FileHandle visDir = projectRoot.child("vis");
				FileHandle visAssetsDir = visDir.child("assets");

				visDir.mkdirs();
				visAssetsDir.mkdirs();

				createStandardAssetsDirs(visAssetsDir);
				visDir.child("modules").mkdirs();

				setProgressPercent(33);
				setMessage("Moving assets...");

				try {
					Files.walkFileTree(standardAssetsDir.file().toPath(), new CopyFileVisitor(visAssetsDir.file().toPath()));
				} catch (IOException e) {
					failed(e.getMessage(), e);
					Log.exception(e);
				}

				setProgressPercent(66);
				setMessage("Saving project files...");

				FileHandle projectFile = visDir.child(PROJECT_FILE);
				saveProjectFile(project, projectFile);

				setProgressPercent(100);
				statusBar.setText("Project created!");

				executeOnGdx(() -> loadNewGeneratedProject(projectFile));
			}
		};

		Async.startTask(stage, "Creating project", task);
	}

	public void createGenericProject (ProjectGeneric project) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void doInBackground () {
				setMessage("Creating directory structure...");

				FileHandle visDir = project.getVisDirectory();
				FileHandle visAssetsDir = visDir.child("assets");

				visDir.mkdirs();
				visAssetsDir.mkdirs();

				createStandardAssetsDirs(visAssetsDir);
				visDir.child("modules").mkdirs();

				setProgressPercent(50);
				setMessage("Saving project files...");

				FileHandle projectFile = visDir.child(PROJECT_FILE);
				saveProjectFile(project, projectFile);

				setProgressPercent(100);
				statusBar.setText("Project created!");

				executeOnGdx(() -> loadNewGeneratedProject(projectFile));
			}
		};

		Async.startTask(stage, "Creating project", task);
	}

	private void saveProjectFile (Project project, FileHandle projectFile) {
		try {
			FileWriter writer = new FileWriter(projectFile.file());
			gson.toJson(project, writer);
			writer.close();
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	private void loadNewGeneratedProject (FileHandle projectFile) {
		try {
			load(projectFile);
		} catch (EditorException e) {
			Dialogs.showErrorDialog(stage, "Error occurred while loading project", e);
			Log.exception(e);
		}
	}

	private void createStandardAssetsDirs (FileHandle visAssetsDir) {
		visAssetsDir.child("scene").mkdirs();
		visAssetsDir.child("gfx").mkdirs();
		visAssetsDir.child("font").mkdirs();
		visAssetsDir.child("bmpfont").mkdirs();
		visAssetsDir.child("sound").mkdirs();
		visAssetsDir.child("music").mkdirs();
		visAssetsDir.child("particle").mkdirs();
		visAssetsDir.child("shader").mkdirs();
		visAssetsDir.child("spriter").mkdirs();
	}

	private class CreateProjectBackupAsyncTask extends SteppedAsyncTask {
		private final FileHandle backupRoot;
		private final FileHandle backupArchive;

		public CreateProjectBackupAsyncTask (FileHandle backupRoot, FileHandle backupArchive) {
			super("ProjectBackupZipCreator");
			this.backupRoot = backupRoot;
			this.backupArchive = backupArchive;
		}

		@Override
		public void doInBackground () {
			try {
				setMessage("Creating project backup...");
				setTotalSteps(countZipFiles(backupRoot, 0));
				try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(backupArchive.file()))) {
					processZipFolder(backupRoot, zipOutputStream, backupRoot.path().length() + 1);
				}
			} catch (IOException e) {
				failed("IO error occurred during backup creation", e);
			}
		}

		private void processZipFolder (final FileHandle folder, final ZipOutputStream zipOutputStream, final int prefixLength) throws IOException {
			for (FileHandle file : folder.list()) {
				if (file.name().equals(".conversionBackup")) continue;

				if (file.isDirectory()) {
					processZipFolder(file, zipOutputStream, prefixLength);
				} else {
					final ZipEntry zipEntry = new ZipEntry(file.path().substring(prefixLength));
					zipOutputStream.putNextEntry(zipEntry);
					try (FileInputStream inputStream = new FileInputStream(file.file())) {
						IOUtils.copy(inputStream, zipOutputStream);
					}
					zipOutputStream.closeEntry();
					nextStep();
				}
			}
		}

		private int countZipFiles (FileHandle folder, int count) {
			for (FileHandle file : folder.list()) {
				if (file.name().equals(".conversionBackup")) continue;

				if (file.isDirectory()) {
					count = countZipFiles(file, count);
				} else {
					count++;
				}
			}

			return count;
		}
	}
}
