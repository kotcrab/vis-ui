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

import pl.kotcrab.vis.editor.ui.NewProjectDialog;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.components.Menu;
import pl.kotcrab.vis.ui.components.MenuBar;
import pl.kotcrab.vis.ui.components.MenuItem;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Editor extends ApplicationAdapter {

	private Stage stage;
	private Table root;
	private ShapeRenderer shapeRenderer;
	private MenuBar menuBar;

	@Override
	public void create () {
		VisUI.load();

		stage = new Stage(new ScreenViewport());

		Gdx.input.setInputProcessor(stage);

		root = new Table();
		root.setFillParent(true);
		if (VisUI.DEBUG) root.debug();

		stage.addActor(root);

		shapeRenderer = new ShapeRenderer();
		menuBar = new MenuBar(stage);

		root.left().top();
		root.add(menuBar.getTable()).fillX().expandX();

		Menu fileMenu = new Menu("File");

		menuBar.addMenu(fileMenu);
		stage.addActor(new NewProjectDialog(stage));

		fileMenu.addItem(new MenuItem("New project...", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewProjectDialog(stage));
			}
		}));

		fileMenu.addItem(new MenuItem("Load project..."));
		fileMenu.addItem(new MenuItem("Close project"));
		fileMenu.addItem(new MenuItem("Exit"));
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		shapeRenderer.setTransformMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));

		menuBar.resize();
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if (VisUI.DEBUG) {
			shapeRenderer.begin(ShapeType.Line);
			root.drawDebug(shapeRenderer); // This is optional, but enables debug lines for tables.
			shapeRenderer.end();
		}
	}

	@Override
	public void dispose () {
		VisUI.dispose();

		stage.dispose();
		shapeRenderer.dispose();
	}

}
