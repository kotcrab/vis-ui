/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.event.StatusBarEvent;
import pl.kotcrab.vis.editor.module.project.Project;
import pl.kotcrab.vis.editor.ui.AsyncTaskProgressDialog;
import pl.kotcrab.vis.editor.util.AsyncTask;
import pl.kotcrab.vis.editor.util.CopyFileVisitor;
import pl.kotcrab.vis.editor.util.EditorException;
import pl.kotcrab.vis.ui.util.DialogUtils;
import pl.kotcrab.vis.ui.widget.file.FileUtils;

import com.badlogic.gdx.utils.Json;

public class ProjectIOModule extends ModuleAdapter {

	public boolean load (File projectRoot) throws EditorException {
		if (projectRoot.exists() == false) throw new EditorException("Selected folder does not exist!");
		if (projectRoot.getName().equals("project.json")) return loadProject(projectRoot);
		if (projectRoot.getName().equals("vis") && projectRoot.isDirectory())
			return loadProject(new File(projectRoot, "project.json"));

		File visFolder = new File(projectRoot, "vis");
		if (visFolder.exists()) return loadProject(new File(visFolder, "project.json"));

		throw new EditorException("Selected folder is not a Vis project!");
	}

	private boolean loadProject (File jsonProjectFile) throws EditorException {

		if (jsonProjectFile.exists() == false) throw new EditorException("Project file does not exist!");
		Json json = new Json();

		Project project = json.fromJson(Project.class, FileUtils.toFileHandle(jsonProjectFile));
		project.root = jsonProjectFile.getParentFile().getParent();

		Editor.instance.projectLoaded(project);

		return true;
	}

	public void create (final Project project, boolean signFiles) {
		AsyncTask task = new AsyncTask("ProjectCreator") {

			@Override
			public void execute () {
				setMessage("Creating directory structure...");

				File standardAssetsDir = new File(project.root, project.assets);
				File visDir = new File(project.root, "vis");
				File visAssetsDir = new File(visDir, "assets");

				visDir.mkdir();
				visAssetsDir.mkdir();

				new File(visAssetsDir, "scene").mkdir();
				new File(visDir, "modules").mkdir();

				setProgressPercent(33);
				setMessage("Moving assets...");

				try {
					Files.walkFileTree(standardAssetsDir.toPath(), new CopyFileVisitor(visAssetsDir.toPath()));
				} catch (IOException e) {
					failed(e.getMessage(), e);
					e.printStackTrace();
				}

				setProgressPercent(66);
				setMessage("Saving project files...");

				File projectFile = new File(visDir, "project.json");

				Json json = new Json();
				json.toJson(project, FileUtils.toFileHandle(projectFile));

				setProgressPercent(100);
				App.eventBus.post(new StatusBarEvent("Project created!", 3));

				try {
					load(projectFile);
				} catch (EditorException e) {
					DialogUtils.showErrorDialog(Editor.instance.getStage(), "Error occurred while loading project", e);
					e.printStackTrace();
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
