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
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectGeneric;
import com.kotcrab.vis.editor.module.project.ProjectLibGDX;
import com.kotcrab.vis.editor.serializer.ArraySerializer;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.CopyFileVisitor;
import com.kotcrab.vis.editor.util.EditorException;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Module allowing to perform IO operation with projects
 * @author Kotcrab
 */
public class ProjectIOModule extends EditorModule {
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

	public boolean loadHandleError (Stage stage, FileHandle projectRoot) {
		try {
			return load(projectRoot);
		} catch (EditorException e) {
			DialogUtils.showErrorDialog(stage, e.getMessage(), e);
		}

		return false;
	}

	public boolean load (FileHandle projectRoot) throws EditorException {
		if (projectRoot.exists() == false) throw new EditorException("Selected folder does not exist!");
		if (projectRoot.name().equals(PROJECT_FILE)) return loadProject(projectRoot);
		if (projectRoot.name().equals("vis") && projectRoot.isDirectory())
			return loadProject(projectRoot.child(PROJECT_FILE));
		if (projectRoot.child(PROJECT_FILE).exists()) return loadProject(projectRoot.child(PROJECT_FILE));

		FileHandle visFolder = projectRoot.child("vis");
		if (visFolder.child(PROJECT_FILE).exists()) return loadProject(visFolder.child(PROJECT_FILE));

		throw new EditorException("Selected folder is not a Vis project!");
	}

	private boolean loadProject (FileHandle dataFile) throws EditorException {
		if (dataFile.exists() == false) throw new EditorException("Project file does not exist!");

		try {
			Input input = new Input(new FileInputStream(dataFile.file()));
			Project project = (Project) kryo.readClassAndObject(input);
			input.close();

			project.updateRoot(dataFile);

			Editor.instance.projectLoaded(project);
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		return true;
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
				App.eventBus.post(new StatusBarEvent("Project created!"));

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
				App.eventBus.post(new StatusBarEvent("Project created!"));

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
	}
}
