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

import pl.kotcrab.vis.editor.module.MenuBarModule;
import pl.kotcrab.vis.editor.ui.NewProjectDialog;
import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter implements EditorListener {
	private static Editor instance;

	private Stage stage;
	private Table root;
	private ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		instance = this;
		Assets.load();
		VisUI.load();

		stage = new Stage(new ScreenViewport());
		shapeRenderer = new ShapeRenderer();
		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		MenuBarModule menuBar = new MenuBarModule();
		menuBar.addToStage(root);

		
		// debug section
		stage.addActor(new NewProjectDialog());
		// stage.addActor(new FileChooser(stage, "Choose file", FileChooser.Mode.SAVE));
	}
	
	public static Editor getInstnace () {
		return instance;
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		shapeRenderer.setTransformMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
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
		shapeRenderer.dispose();

		Assets.dispose();
		VisUI.dispose();
	}

	@Override
	public void requestExit () {
		// here will be fancy do you really want to exit dialog
		Gdx.app.exit();
	}

	@Override
	public Stage getStage () {
		return stage;
	}

}
