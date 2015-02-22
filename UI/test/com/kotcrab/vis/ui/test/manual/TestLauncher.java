/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.DialogUtils;
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
	private static final int TESTS_VERSION = 1;
	private static final boolean USE_VIS_WIDGETS = true;

	private Stage stage;
	private MenuBar menuBar;

	@Override
	public void create () {
		VisUI.load();

		stage = new Stage(new ScreenViewport());
		Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		Gdx.input.setInputProcessor(stage);

		menuBar = new MenuBar(stage);
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.add().expand().fill();

		createMenus();

		stage.addActor(new TestCollapsible());
		if (Gdx.app.getType() != ApplicationType.WebGL) stage.addActor(new TestColorPicker());
		stage.addActor(new TestDialogUtils());
		stage.addActor(new TestFormValidator());
		stage.addActor(new TestSplitPane(USE_VIS_WIDGETS));
		stage.addActor(new TestTextAreaAndScroll(USE_VIS_WIDGETS));
		stage.addActor(new TestTree(USE_VIS_WIDGETS));
		stage.addActor(new TestValidator());
		stage.addActor(new TestVertical(USE_VIS_WIDGETS));
		stage.addActor(new TestWindow(USE_VIS_WIDGETS));

//		FileChooser fileChooser = new FileChooser(FileChooser.Mode.OPEN);
//		fileChooser.setMultiselectionEnabled(true);
//		fileChooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
//		stage.addActor(fileChooser);
	}

	private void createMenus () {
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu windowMenu = new Menu("Window");
		Menu helpMenu = new Menu("Help");

		fileMenu.addItem(new MenuItem("MenuItem #1"));
		fileMenu.addItem(new MenuItem("MenuItem #2").setShortcut(Keys.F1));
		fileMenu.addItem(new MenuItem("MenuItem #3").setShortcut(Keys.F2));
		fileMenu.addItem(new MenuItem("MenuItem #4").setShortcut("Alt + F4"));

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

		helpMenu.addItem(new MenuItem("About", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				DialogUtils.showOKDialog(stage, "About", "Tests version: " + TESTS_VERSION + " \nVisUI version: " + VisUI.VERSION );
			}
		}));

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
