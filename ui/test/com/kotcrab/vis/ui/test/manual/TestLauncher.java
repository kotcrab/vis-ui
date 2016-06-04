/*
 * Copyright 2014-2016 See AUTHORS file.
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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.VisUI.SkinScale;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;

public class TestLauncher {

	public static void main (String[] args) {
		Lwjgl3ApplicationConfiguration c = new Lwjgl3ApplicationConfiguration();
		c.setTitle("VisUI test application");
		c.setWindowedMode(1280, 720);
		new Lwjgl3Application(new TestApplication(), c);
	}

}

class TestApplication extends ApplicationAdapter {
	private static final int TESTS_VERSION = 5;
	public static final boolean USE_VIS_WIDGETS = true;

	private Stage stage;
	private MenuBar menuBar;

	@Override
	public void create () {
		VisUI.load(SkinScale.X1);

		stage = new Stage(new ScreenViewport());
		final Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);

		Gdx.input.setInputProcessor(stage);

		menuBar = new MenuBar();
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.add().expand().fill();

		createMenus();

		stage.addActor(new TestCollapsible());
		stage.addActor(new TestColorPicker());
		if (Gdx.app.getType() == ApplicationType.Desktop) stage.addActor(new TestFileChooser());
		stage.addActor(new TestWindow());
		stage.addActor(new TestSplitPane());
		stage.addActor(new TestTextAreaAndScroll());
		stage.addActor(new TestTree());
		stage.addActor(new TestVertical());
		stage.addActor(new TestFormValidator());
		stage.addActor(new TestDialogs());
		stage.addActor(new TestValidator());
		stage.addActor(new TestBuilders());
//		stage.addActor(new TestTabbedPane());
//		stage.addActor(new TestFlowGroup());
//		stage.addActor(new TestButtonBar());
//		stage.addActor(new TestListView());
//		stage.addActor(new TestToasts(stage));
		stage.addActor(new TestHighlightTextArea());

		stage.addListener(new InputListener() {
			boolean debug = false;

			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.F12) {
					debug = !debug;
					root.setDebug(debug, true);
					for (Actor actor : stage.getActors()) {
						if (actor instanceof Group) {
							Group group = (Group) actor;
							group.setDebug(debug, true);
						}
					}
					return true;
				}

				return false;
			}
		});
	}

	private void createMenus () {
		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Edit");
		Menu windowMenu = new Menu("Window");
		Menu helpMenu = new Menu("Help");

		fileMenu.addItem(createTestsMenu());
		fileMenu.addItem(new MenuItem("menuitem #1"));
		fileMenu.addItem(new MenuItem("menuitem #2").setShortcut("f1"));
		fileMenu.addItem(new MenuItem("menuitem #3").setShortcut("f2"));
		fileMenu.addItem(new MenuItem("menuitem #4").setShortcut("alt + f4"));

		MenuItem subMenuItem = new MenuItem("submenu #1");
		subMenuItem.setShortcut("alt + insert");
		subMenuItem.setSubMenu(createSubMenu());
		fileMenu.addItem(subMenuItem);

		MenuItem subMenuItem2 = new MenuItem("submenu #2");
		subMenuItem2.setSubMenu(createSubMenu());
		fileMenu.addItem(subMenuItem2);

		MenuItem subMenuItem3 = new MenuItem("submenu disabled");
		subMenuItem3.setDisabled(true);
		subMenuItem3.setSubMenu(createSubMenu());
		fileMenu.addItem(subMenuItem3);

		// ---

		editMenu.addItem(new MenuItem("menuitem #5"));
		editMenu.addItem(new MenuItem("menuitem #6"));
		editMenu.addSeparator();
		editMenu.addItem(new MenuItem("menuitem #7"));
		editMenu.addItem(new MenuItem("menuitem #8"));
		editMenu.addItem(createDoubleNestedMenu());

		MenuItem disabledItem = new MenuItem("disabled menuitem");
		disabledItem.setDisabled(true);
		MenuItem disabledItem2 = new MenuItem("disabled menuitem shortcut").setShortcut("alt + f4");
		disabledItem2.setDisabled(true);

		editMenu.addItem(disabledItem);
		editMenu.addItem(disabledItem2);

		windowMenu.addItem(new MenuItem("menuitem #9"));
		windowMenu.addItem(new MenuItem("menuitem #10"));
		windowMenu.addItem(new MenuItem("menuitem #11"));
		windowMenu.addSeparator();
		windowMenu.addItem(new MenuItem("menuitem #12"));

		helpMenu.addItem(new MenuItem("menuitem #13"));
		helpMenu.addItem(new MenuItem("menuitem #14"));

		helpMenu.addItem(new MenuItem("about", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Dialogs.showOKDialog(stage, "about", "tests version: " + TESTS_VERSION + " \nvisui version: " + VisUI.VERSION);
			}
		}));

		menuBar.addMenu(fileMenu);
		menuBar.addMenu(editMenu);
		menuBar.addMenu(windowMenu);
		menuBar.addMenu(helpMenu);
	}

	private MenuItem createDoubleNestedMenu () {
		MenuItem doubleNestedMenuItem = new MenuItem("submenu nested x2");
		doubleNestedMenuItem.setSubMenu(createSubMenu());

		PopupMenu nestedMenu = new PopupMenu();
		nestedMenu.addItem(doubleNestedMenuItem);
		nestedMenu.addItem(new MenuItem("single nested"));

		MenuItem menuItem = new MenuItem("submenu nested");
		menuItem.setSubMenu(nestedMenu);
		return menuItem;
	}

	private MenuItem createTestsMenu () {
		MenuItem item = new MenuItem("start test");

		PopupMenu menu = new PopupMenu();
		menu.addItem(new MenuItem("tabbed pane", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestTabbedPane());
			}
		}));
		menu.addItem(new MenuItem("flow groups", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestFlowGroup());
			}
		}));
		menu.addItem(new MenuItem("button bar", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestButtonBar());
			}
		}));
		menu.addItem(new MenuItem("list view", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestListView());
			}
		}));
		menu.addItem(new MenuItem("toasts", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestToasts(stage));
			}
		}));
		menu.addItem(new MenuItem("highlight textarea", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestHighlightTextArea());
			}
		}));
		menu.addSeparator();
		menu.addItem(new MenuItem("test issue #131", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new TestIssue131());
			}
		}));

		item.setSubMenu(menu);
		return item;
	}

	private PopupMenu createSubMenu () {
		PopupMenu menu = new PopupMenu();
		menu.addItem(new MenuItem("submenuitem #1"));
		menu.addItem(new MenuItem("submenuitem #2"));
		menu.addSeparator();
		menu.addItem(new MenuItem("submenuitem #3"));
		menu.addItem(new MenuItem("submenuitem #4"));
		return menu;
	}

	@Override
	public void resize (int width, int height) {
		if (width == 0 && height == 0) return; //see https://github.com/libgdx/libgdx/issues/3673#issuecomment-177606278
		stage.getViewport().update(width, height, true);

		WindowResizeEvent resizeEvent = new WindowResizeEvent();
		for (Actor actor : stage.getActors()) {
			actor.fire(resizeEvent);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
	}

	@Override
	public void dispose () {
		VisUI.dispose();
		stage.dispose();
	}
}
