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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.api.Icons;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.ui.ButtonListener;
import com.kotcrab.vis.editor.ui.ProjectStatusWidgetController;
import com.kotcrab.vis.editor.ui.SceneStatusWidgetController;
import com.kotcrab.vis.editor.ui.dialog.AboutDialog;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.ui.scene.SceneMenuButtonsListener;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;

public class MenuBarModule extends EditorModule {
	private ProjectModuleContainer projectContainer;
	private Editor editor;

	private Stage stage;
	private MenuBar menuBar;

	private ProjectStatusWidgetController projectController;
	private SceneStatusWidgetController sceneController;

	private SceneMenuButtonsListener sceneButtonsListener;

	public MenuBarModule (ProjectModuleContainer moduleContainer) {
		editor = Editor.instance;
		stage = editor.getStage();
		projectContainer = moduleContainer;

		menuBar = new MenuBar();

		projectController = new ProjectStatusWidgetController();
		sceneController = new SceneStatusWidgetController();

		createFileMenu();
		createEditMenu();
		createSceneMenu();
		createHelpMenu();
	}

	private void createFileMenu () {
		Menu menu = new Menu("File");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem("New Project...", Icons.NEW, editor::newProjectDialog));
		menu.addItem(createMenuItem("Load Project...", Icons.LOAD, editor::loadProjectDialog));
		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Close Project", editor::requestProjectUnload));

		menu.addSeparator();

		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Export", Icons.EXPORT, () -> {
			ExportModule exportModule = projectContainer.get(ExportModule.class);
			exportModule.export(false);
		}));

		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Quick Export", () -> {
			ExportModule exportModule = projectContainer.get(ExportModule.class);
			exportModule.export(true);
		}));

		menu.addSeparator();

		menu.addItem(createMenuItem("Settings", Icons.SETTINGS, editor::showSettingsWindow));

		menu.addSeparator();

		menu.addItem(createMenuItem("Exit", Icons.EXIT, editor::requestExit));
	}

	@SuppressWarnings("Convert2MethodRef")
	private void createEditMenu () {
		Menu menu = new Menu("Edit");
		menuBar.addMenu(menu);

		//DO NOT replace this with method reference!!!
		menu.addItem(createMenuItem("Undo", Icons.UNDO, () -> sceneButtonsListener.undo()));
		menu.addItem(createMenuItem("Redo", Icons.REDO, () -> sceneButtonsListener.redo()));
		menu.addSeparator();
		menu.addItem(createMenuItem("Group", null, () -> sceneButtonsListener.group()));
		menu.addItem(createMenuItem("Ungroup", null, () -> sceneButtonsListener.ungroup()));
	}

	@SuppressWarnings("Convert2MethodRef")
	private void createSceneMenu () {
		Menu menu = new Menu("Scene");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "New Scene...", Icons.NEW, () -> {
			FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
			SceneTabsModule sceneTabsModule = projectContainer.get(SceneTabsModule.class);
			SceneIOModule sceneIO = projectContainer.get(SceneIOModule.class);
			stage.addActor(new NewSceneDialog(fileAccess, sceneTabsModule, sceneIO).fadeIn());
		}));

		menu.addSeparator();

		//DO NOT replace this with method reference!!!
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Reset Camera", () -> sceneButtonsListener.resetCamera()));
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Reset Camera Zoom", () -> sceneButtonsListener.resetCameraZoom()));
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Scene Settings", () -> sceneButtonsListener.showSceneSettings()));

	}

	private void createHelpMenu () {
		Menu menu = new Menu("Help");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem("Web", Icons.GLOBE, () -> Gdx.net.openURI("http://vis.kotcrab.com")));
		menu.addItem(createMenuItem("About", Icons.INFO, () -> stage.addActor(new AboutDialog().fadeIn())));
	}

	public Table getTable () {
		return menuBar.getTable();
	}

	public void setSceneButtonsListener (SceneMenuButtonsListener listener) {
		sceneButtonsListener = listener;
		sceneController.listenerChanged(listener);
	}

	@Override
	public void dispose () {
		projectController.dispose();
	}

	private MenuItem createMenuItem (String text, Icons icon, ButtonListener listener) {
		return createMenuItem(ControllerPolicy.NONE, text, icon, listener);
	}

	private MenuItem createMenuItem (ControllerPolicy policy, String text, ButtonListener listener) {
		return createMenuItem(policy, text, null, listener);
	}

	private MenuItem createMenuItem (ControllerPolicy policy, String text, Icons icon, ButtonListener listener) {
		MenuItem item = MenuUtils.createMenuItem(text, icon, listener);

		switch (policy) {
			case PROJECT:
				projectController.addButton(item);
				break;
			case SCENE:
				sceneController.addButton(item);
				break;
			case NONE:
				break;
		}

		return item;
	}

	private enum ControllerPolicy {
		NONE, PROJECT, SCENE
	}
}
