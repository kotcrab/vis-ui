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

import pl.kotcrab.utils.event.Event;
import pl.kotcrab.utils.event.EventListener;
import pl.kotcrab.vis.editor.module.MenuBarModule;
import pl.kotcrab.vis.editor.module.ModuleManager;
import pl.kotcrab.vis.editor.module.StatusBar;
import pl.kotcrab.vis.editor.ui.EditorFrame;
import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter implements EditorListener, EventListener {
	public static Editor instance;

	private EditorFrame frame;

	private Stage stage;
	private Table root;

	private ModuleManager moduleManager;

	private StatusBar statusBar;

	@Override
	public void create () {
		instance = this;
		Assets.load();
		VisUI.load();

		App.eventBus.register(this);

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		moduleManager = new ModuleManager();

		moduleManager.add(new MenuBarModule());

		root.add(new Table()).expand().fill().row();

		moduleManager.add(new StatusBar());

		// debug section
		// stage.addActor(new NewProjectDialog());
		// stage.addActor(new AsyncTaskProgressDialog(null));
		// stage.addActor(new FileChooser(stage, "Choose file", FileChooser.Mode.SAVE));
	}

	public StatusBar getStatusBar () {
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

		moduleManager.dispose();
		frame.dispose();
	}

	@Override
	public void requestExit () {
		// here will be fancy 'do you really want to exit' dialog
		exit();
	}

	private void exit () {
		Gdx.app.exit();
	}

	@Override
	public Stage getStage () {
		return stage;
	}

	public Table getRoot () {
		return root;
	}

	public void setEditorFrame (EditorFrame frame) {
		this.frame = frame;
	}

	@Override
	public boolean onEvent (Event event) {
		return false;
	}

}
