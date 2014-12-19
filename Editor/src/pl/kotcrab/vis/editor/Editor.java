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

package pl.kotcrab.vis.editor;

import java.io.File;

import pl.kotcrab.utils.event.Event;
import pl.kotcrab.utils.event.EventListener;
import pl.kotcrab.vis.editor.event.ProjectStatusEvent;
import pl.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import pl.kotcrab.vis.editor.event.StatusBarEvent;
import pl.kotcrab.vis.editor.module.FileAccessModule;
import pl.kotcrab.vis.editor.module.MenuBarModule;
import pl.kotcrab.vis.editor.module.ModuleContainer;
import pl.kotcrab.vis.editor.module.ProjectModule;
import pl.kotcrab.vis.editor.module.ProjectModuleContainer;
import pl.kotcrab.vis.editor.module.SceneIOModule;
import pl.kotcrab.vis.editor.module.StatusBarModule;
import pl.kotcrab.vis.editor.module.TabsModule;
import pl.kotcrab.vis.editor.ui.EditorFrame;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.util.DialogUtils;
import pl.kotcrab.vis.ui.widget.VisTextButton;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter implements EventListener {
	public static Editor instance;

	private EditorFrame frame;

	private Stage stage;
	private Table root;

	private ModuleContainer moduleContainer;
	private ProjectModuleContainer projectModuleContainer;

	private boolean projectLoaded = false;

	private StatusBarModule statusBar;

	public Editor (EditorFrame frame) {
		this.frame = frame;
	}

	@Override
	public void create () {
		instance = this;
		Assets.load();
		VisUI.load();
		VisUI.setDefualtTitleAlign(Align.center);

		App.eventBus.register(this);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		moduleContainer = new ModuleContainer();
		projectModuleContainer = new ProjectModuleContainer();

		moduleContainer.add(new MenuBarModule());

		root.add(new Table()).expand().fill().row();
		
		root.row();

		moduleContainer.add(new StatusBarModule());

		// debug section
		try {
			ProjectIO.load(new File("F:\\Poligon\\TestProject"));
		} catch (EditorException e) {
			e.printStackTrace();
		}
		// stage.addActor(new NewProjectDialog());
		// stage.addActor(new AsyncTaskProgressDialog(null));
		// stage.addActor(new FileChooser(stage, "Choose file", FileChooser.Mode.SAVE));
	}

	public StatusBarModule getStatusBar () {
		return statusBar;
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose () {
		stage.dispose();

		Assets.dispose();
		VisUI.dispose();

		moduleContainer.dispose();
		frame.dispose();
	}

	public void requestExit () {
		// here will be fancy 'do you really want to exit' dialog
		exit();
	}

	private void exit () {
		Gdx.app.exit();
	}

	public Stage getStage () {
		return stage;
	}

	public Table getRoot () {
		return root;
	}

	@Override
	public boolean onEvent (Event e) {
		return false;
	}

	public void requestProjectUnload () {
		projectLoaded = false;
		projectModuleContainer.dispose();

		App.eventBus.post(new StatusBarEvent("Project unloaded", 3));
		App.eventBus.post(new ProjectStatusEvent(Status.Unloaded));
	}

	public boolean isProjectLoaded () {
		return projectLoaded;
	}

	public void projectLoaded (Project project) {
		// TODO unload previous project dialog
		if (projectLoaded) {
			DialogUtils.showErrorDialog(getStage(), "Other project is already loaded!");
			return;
		}

		projectLoaded = true;
		projectModuleContainer.setProject(project);

		projectModuleContainer.add(new FileAccessModule());
		projectModuleContainer.add(new SceneIOModule());

		App.eventBus.post(new StatusBarEvent("Project loaded", 3));
		App.eventBus.post(new ProjectStatusEvent(Status.Loaded));
	}

	public ProjectModule getProjectModule (Class<? extends ProjectModule> moduleClass) {
		return (ProjectModule)projectModuleContainer.get(moduleClass);
	}

	public File getProjectVisFolder () {
		return new File(projectModuleContainer.getProject().root, "vis");
	}
}
