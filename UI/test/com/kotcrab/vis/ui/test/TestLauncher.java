/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package com.kotcrab.vis.ui.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;

public class TestLauncher {

	public static void main (String[] args) {
		LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
		c.width = 1280;
		c.height = 720;
		new LwjglApplication(new TestApplication(), c);
	}

}

class TestApplication extends ApplicationAdapter {
	private static final boolean USE_VIS_COMPONENTS = true;

	private Table root;
	private Stage stage;
	private MenuBar menuBar;

	@Override
	public void create () {
		VisUI.load();

		stage = new Stage(new ScreenViewport());
		root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		Gdx.input.setInputProcessor(stage);

		menuBar = new MenuBar(stage);
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.add().expand().fill();

		createMenus();

		stage.addActor(new TestWindow(USE_VIS_COMPONENTS));
		stage.addActor(new TestTree(USE_VIS_COMPONENTS));
		stage.addActor(new TestTextAreaAndScroll(USE_VIS_COMPONENTS));
		stage.addActor(new TestSplitPane(USE_VIS_COMPONENTS));
		stage.addActor(new TestVertical(USE_VIS_COMPONENTS));
		stage.addActor(new TestValidator(USE_VIS_COMPONENTS));
		stage.addActor(new TestDialogUtils(USE_VIS_COMPONENTS));
		stage.addActor(new TestCollapsible(USE_VIS_COMPONENTS));
		stage.addActor(new TestFormValidator(USE_VIS_COMPONENTS));
	}

	private void createMenus () {
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu windowMenu = new Menu("Window");
		Menu helpMenu = new Menu("Help");

		fileMenu.addItem(new MenuItem("MenuItem #1"));
		fileMenu.addItem(new MenuItem("MenuItem #2").setShortcut(Keys.F1));
		fileMenu.addItem(new MenuItem("MenuItem #3"));
		fileMenu.addItem(new MenuItem("MenuItem #4").setShortcut(Keys.F5));

		editMenu.addItem(new MenuItem("MenuItem #5"));
		editMenu.addItem(new MenuItem("MenuItem #6"));
		editMenu.addSeparator();
		editMenu.addItem(new MenuItem("MenuItem #7"));
		editMenu.addItem(new MenuItem("MenuItem #8"));

		windowMenu.addItem(new MenuItem("MenuItem #9"));
		windowMenu.addItem(new MenuItem("MenuItem #10"));
		windowMenu.addItem(new MenuItem("MenuItem #11"));
		windowMenu.addSeparator();
		windowMenu.addItem(new MenuItem("MenuItem #12"));

		helpMenu.addItem(new MenuItem("MenuItem #13"));
		helpMenu.addItem(new MenuItem("MenuItem #14"));

		menuBar.addMenu(fileMenu);
		menuBar.addMenu(editMenu);
		menuBar.addMenu(windowMenu);
		menuBar.addMenu(helpMenu);
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
		VisUI.dispose();

		stage.dispose();
	}

}
