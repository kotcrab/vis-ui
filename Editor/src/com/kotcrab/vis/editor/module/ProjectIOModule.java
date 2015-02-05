/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.project.Project;
import com.kotcrab.vis.editor.module.project.ProjectLibGDX;
import com.kotcrab.vis.editor.ui.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.CopyFileVisitor;
import com.kotcrab.vis.editor.util.EditorException;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.ui.util.DialogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class ProjectIOModule extends EditorModule {
	private static final String PROJECT_FILE = "project.data";
	private Kryo kryo;

	@Override
	public void init () {
		kryo = new Kryo();
	}

	public boolean load (FileHandle projectRoot) throws EditorException {
		if (projectRoot.exists() == false) throw new EditorException("Selected folder does not exist!");
		if (projectRoot.name().equals(PROJECT_FILE)) return loadProject(projectRoot);
		if (projectRoot.name().equals("vis") && projectRoot.isDirectory())
			return loadProject(projectRoot.child(PROJECT_FILE));

		FileHandle visFolder = projectRoot.child("vis");
		if (visFolder.exists()) return loadProject(visFolder.child(PROJECT_FILE));

		throw new EditorException("Selected folder is not a Vis project!");
	}

	private boolean loadProject (FileHandle file) throws EditorException {

//		if (file.exists() == false) throw new EditorException("Project file does not exist!");
//		Json json = new Json();
//
//		Project project = json.fromJson(Project.class, file);
//		project.root = file.parent().parent().path();
//
//		Editor.instance.projectLoaded(project);
//
//		return true;
//


		if (file.exists() == false) throw new EditorException("Project file does not exist!");

		try {
			Input input = new Input(new FileInputStream(file.file()));
			Project project = (Project) kryo.readClassAndObject(input);
			input.close();

			project.root = file.parent().parent().path();

			Editor.instance.projectLoaded(project);
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		return true;


	}

	public void create (final ProjectLibGDX project) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void execute () {
				setMessage("Creating directory structure...");

				FileHandle projectRoot = Gdx.files.absolute(project.root);
				FileHandle standardAssetsDir = projectRoot.child(project.assets);
				FileHandle visDir = projectRoot.child("vis");
				FileHandle visAssetsDir = visDir.child("assets");

				visDir.mkdirs();
				visAssetsDir.mkdirs();

				visAssetsDir.child("scene").mkdirs();
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

				try {
					Output output = new Output(new FileOutputStream(projectFile.file()));
					kryo.writeClassAndObject(output, project);
					output.close();
				} catch (FileNotFoundException e) {
					Log.exception(e);
				}

				setProgressPercent(100);
				App.eventBus.post(new StatusBarEvent("Project created!"));

				try {
					load(projectFile);
				} catch (EditorException e) {
					DialogUtils.showErrorDialog(Editor.instance.getStage(), "Error occurred while loading project", e);
					Log.exception(e);
				}
			}
		};

		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Creating project...", task).fadeIn());
	}

	public String verify (Project project) {
		File visDir = new File(project.root, "vis");
		if (visDir.exists()) return "This folder is already a VisEditor project. Use File->Load Project.";
		return null;
	}
}
