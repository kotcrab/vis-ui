/*
 * Copyright 2014-2015 See AUTHORS file.
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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.serializer.ArraySerializer;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.CopyFileVisitor;
import com.kotcrab.vis.editor.util.SteppedAsyncTask;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.util.dialog.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import org.apache.commons.io.IOUtils;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Module allowing to perform IO operation with projects
 * @author Kotcrab
 */
public class ProjectIOModule extends EditorModule {
	@InjectModule private StatusBarModule statusBar;

	public static final String PROJECT_FILE = "project.data";

	private Kryo kryo;

	@Override
	public void init () {
		kryo = new Kryo();
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setDefaultSerializer(TaggedFieldSerializer.class);
		kryo.register(Array.class, new ArraySerializer(), 10);
		kryo.register(ProjectLibGDX.class, 11);
		kryo.register(ProjectGeneric.class, 12);
	}

	public void loadHandleError (Stage stage, FileHandle projectRoot) {
		try {
			load(projectRoot);
		} catch (EditorException e) {
			DialogUtils.showErrorDialog(stage, e.getMessage(), e);
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
			if (descriptor.versionCode > App.VERSION_CODE) {
				DialogUtils.showOptionDialog(Editor.instance.getStage(), "Warning",
						"This project was opened in newer version of VisEditor.\nSome functions may not work properly. Do you want to continue?",
						OptionDialogType.YES_NO, new OptionDialogAdapter() {
							@Override
							public void yes () {
								doLoadProject(dataFile);
							}
						});
			} else if (descriptor.versionCode < App.VERSION_CODE) {
				backupProjectAndLoad(dataFile, descriptor.versionCode);
			} else
				doLoadProject(dataFile);
		} else //if there is no version file that means that project was just created
			doLoadProject(dataFile);

	}

	private void backupProjectAndLoad (FileHandle dataFile, int oldVersionCode) {
		Project project = loadProjectDataFile(dataFile);
		FileHandle backupRoot = project.getVisDirectory();
		FileHandle backupOut = backupRoot.child("modules").child(".conversionBackup");
		backupOut.mkdirs();

		FileHandle backupArchive = backupOut.child("before-conversion-from-" + oldVersionCode + "-to-" + App.VERSION_CODE + ".zip");

		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Creating backup...",
				new CreateProjectBackupAsyncTask(dataFile, backupRoot, backupArchive)).fadeIn());
	}

	private Project loadProjectDataFile (FileHandle dataFile) {
		try {
			Input input = new Input(new FileInputStream(dataFile.file()));
			Project project = (Project) kryo.readClassAndObject(input);
			input.close();

			project.updateRoot(dataFile);

			return project;
		} catch (FileNotFoundException e) {
			Log.exception(e);
			throw new IllegalStateException(e);
		}
	}

	private void doLoadProject (FileHandle dataFile) {
		Project project = loadProjectDataFile(dataFile);
		Editor.instance.projectLoaded(project);
	}

	public void createLibGDXProject (final ProjectLibGDX project) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void execute () {
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

				executeOnOpenGL(() -> loadNewGeneratedProject(projectFile));
			}
		};

		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Creating project...", task).fadeIn());
	}

	public void createGenericProject (ProjectGeneric project) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void execute () {
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

				executeOnOpenGL(() -> loadNewGeneratedProject(projectFile));
			}
		};

		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Creating project...", task).fadeIn());
	}

	private void saveProjectFile (Project project, FileHandle projectFile) {
		try {
			Output output = new Output(new FileOutputStream(projectFile.file()));
			kryo.writeClassAndObject(output, project);
			output.close();
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}
	}

	private void loadNewGeneratedProject (FileHandle projectFile) {
		try {
			load(projectFile);
		} catch (EditorException e) {
			DialogUtils.showErrorDialog(Editor.instance.getStage(), "Error occurred while loading project", e);
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
	}

	private class CreateProjectBackupAsyncTask extends SteppedAsyncTask {
		private FileHandle dataFile;
		private final FileHandle backupRoot;
		private final FileHandle backupArchive;

		public CreateProjectBackupAsyncTask (FileHandle dataFile, FileHandle backupRoot, FileHandle backupArchive) {
			super("ProjectBackupZipCreator");
			this.dataFile = dataFile;
			this.backupRoot = backupRoot;
			this.backupArchive = backupArchive;
		}

		@Override
		public void execute () {
			try {
				setMessage("Creating project backup...");
				setTotalSteps(countZipFiles(backupRoot, 0));
				try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(backupArchive.file()))) {
					processZipFolder(backupRoot, zipOutputStream, backupRoot.path().length() + 1);
				}

				doLoadProject(dataFile);
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
