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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ProjectMenuBarEvent;
import com.kotcrab.vis.editor.event.SceneMenuBarEvent;
import com.kotcrab.vis.editor.ui.ButtonListener;
import com.kotcrab.vis.editor.ui.ProjectStatusWidgetController;
import com.kotcrab.vis.editor.ui.SceneStatusWidgetController;
import com.kotcrab.vis.editor.ui.dialog.AboutDialog;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.scene2d.MenuUtils;
import com.kotcrab.vis.editor.util.vis.WikiPages;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;

import static com.kotcrab.vis.editor.event.ProjectMenuBarEventType.EXPORT;
import static com.kotcrab.vis.editor.event.ProjectMenuBarEventType.SHOW_NEW_SCENE_DIALOG;
import static com.kotcrab.vis.editor.event.SceneMenuBarEventType.*;

/**
 * VisEditor menu bar UI component.
 * @author Kotcrab
 */
public class MenuBarModule extends EditorModule {
	private Stage stage;
	private Editor editor;

	private MenuBar menuBar;

	private VisTable mainTable;

	private ProjectStatusWidgetController projectController;
	private SceneStatusWidgetController sceneController;

	private SceneTab activeSceneTab;

	private VisTable updateInfoTable;

	private MenuItem undoMenuItem;
	private Menu editMenu;
	private Menu toolsMenu;

	public MenuBarModule () {
		editor = Editor.instance;

		menuBar = new MenuBar();

		updateInfoTable = new VisTable();

		projectController = new ProjectStatusWidgetController();
		sceneController = new SceneStatusWidgetController();

		createFileMenu();
		createEditMenu();
		createSceneMenu();
		createToolsMenu();
		createHelpMenu();

		mainTable = new VisTable(true);
		mainTable.setBackground(VisUI.getSkin().getDrawable("menu-bg"));

		mainTable.add(menuBar.getTable());
		mainTable.add().expand().fill();
		mainTable.add(updateInfoTable).expand().fill();
	}

	private void createFileMenu () {
		Menu menu = new Menu("File");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem("New Project...", Icons.NEW, editor::newProjectDialog));
		menu.addItem(createMenuItem("Load Project...", Icons.LOAD, editor::loadProjectDialog));
		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Close Project", editor::requestProjectUnload));
		menu.addSeparator();

		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Export", Icons.EXPORT,
				() -> App.eventBus.post(new ProjectMenuBarEvent(EXPORT))).setShortcut(Keys.CONTROL_LEFT, Keys.E));
		//menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "Quick Export", () -> projectContainer.get(ExportModule.class).export(true))); //TODO quick export
		menu.addSeparator();

		menu.addItem(createMenuItem("Settings...", Icons.SETTINGS, editor::showSettingsWindow));
		menu.addSeparator();

		menu.addItem(createMenuItem("Exit", Icons.EXIT, editor::requestExit));
	}

	@SuppressWarnings("Convert2MethodRef")
	private void createEditMenu () {
		editMenu = new Menu("Edit");
		menuBar.addMenu(editMenu);

		MenuItem addNewMenuItem = createMenuItem(ControllerPolicy.SCENE, "Add New", null, null);
		PopupMenu addNewPopupMenu = new PopupMenu();
		addNewMenuItem.setSubMenu(addNewPopupMenu);
		editMenu.addItem(addNewMenuItem);

		//DO NOT replace this with method reference!!!
		editMenu.addSeparator();
		editMenu.addItem(createMenuItem(ControllerPolicy.SCENE, "Alignment Tools", Icons.ALIGN_LEFT, () -> App.eventBus.post(new SceneMenuBarEvent(SHOW_ALIGNMENT_TOOLS))));
		editMenu.addSeparator();
		editMenu.addItem(undoMenuItem = createMenuItem(ControllerPolicy.SCENE, "Undo", Icons.UNDO,
				() -> App.eventBus.post(new SceneMenuBarEvent(UNDO))).setShortcut(Keys.CONTROL_LEFT, Keys.Z));
		editMenu.addItem(createMenuItem(ControllerPolicy.SCENE, "Redo", Icons.REDO, () -> App.eventBus.post(new SceneMenuBarEvent(REDO))).setShortcut(
				Keys.CONTROL_LEFT, Keys.Y));
		editMenu.addSeparator();
		editMenu.addItem(createMenuItem(ControllerPolicy.SCENE, "Group", null, () -> App.eventBus.post(new SceneMenuBarEvent(GROUP))));
		editMenu.addItem(createMenuItem(ControllerPolicy.SCENE, "Ungroup", null, () -> App.eventBus.post(new SceneMenuBarEvent(UNGROUP))));

		addNewPopupMenu.addItem(createMenuItem(ControllerPolicy.SCENE, "Point", null, () -> App.eventBus.post(new SceneMenuBarEvent(ADD_NEW_POINT))));
	}

	@SuppressWarnings("Convert2MethodRef")
	private void createSceneMenu () {
		Menu menu = new Menu("Scene");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem(ControllerPolicy.PROJECT, "New Scene...", Icons.NEW, () -> App.eventBus.post(new ProjectMenuBarEvent(SHOW_NEW_SCENE_DIALOG))));

		menu.addSeparator();
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Scene Settings...", () -> App.eventBus.post(new SceneMenuBarEvent(SHOW_SCENE_SETTINGS))));
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Scene Variables...", () -> App.eventBus.post(new SceneMenuBarEvent(SHOW_SCENE_VARIABLES_SETTINGS))));
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Physics Settings...", () -> App.eventBus.post(new SceneMenuBarEvent(SHOW_PHYSICS_SETTINGS))));
		menu.addSeparator();
		//DO NOT replace this with method reference!!!
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Reset Camera", () -> App.eventBus.post(new SceneMenuBarEvent(RESET_CAMERA))));
		menu.addItem(createMenuItem(ControllerPolicy.SCENE, "Reset Camera Zoom", () -> App.eventBus.post(new SceneMenuBarEvent(RESET_ZOOM))));

	}

	private void createToolsMenu () {
		toolsMenu = new Menu("Tools");
		//menuBar.addMenu(toolsMenu);

//		toolsMenu.addItem(createMenuItem("Plugin API Manager", null, () -> stage.addActor(new PluginApiManagerDialog(container).fadeIn())));
		toolsMenu.addItem(createMenuItem("Plugin API Manager", null, () -> Dialogs.showOKDialog(stage, "Message", "Plugin API Manager is not avaiable yet.")));

//		menu.addItem(createMenuItem("Hiero", null, () -> System.out.println("not yet")));
//		menu.addItem(createMenuItem("Particle Editor", null, () -> System.out.println("not yet")));
//		menu.addSeparator();
	}

	private void createHelpMenu () {
		Menu menu = new Menu("Help");
		menuBar.addMenu(menu);

		menu.addItem(createMenuItem("Website", Icons.GLOBE, () -> Gdx.net.openURI("http://vis.kotcrab.com")));
		menu.addItem(createMenuItem("Documentation", null, WikiPages.QUICK_START::open));
		menu.addItem(createMenuItem("Show Log", null, () -> {
			Log.flush();
			FileUtils.open(Log.getLogFile());
		}));
		menu.addItem(createMenuItem("About", Icons.INFO, () -> stage.addActor(new AboutDialog().fadeIn())));
	}

	public Table getTable () {
		return mainTable;
	}

	public Menu getToolsMenu () {
		return toolsMenu;
	}

	@Override
	public void dispose () {
		projectController.dispose();
	}

	public MenuItem createMenuItem (String text, Icons icon, ButtonListener listener) {
		return createMenuItem(ControllerPolicy.NONE, text, icon, listener);
	}

	public MenuItem createMenuItem (ControllerPolicy policy, String text, ButtonListener listener) {
		return createMenuItem(policy, text, null, listener);
	}

	public MenuItem createMenuItem (ControllerPolicy policy, String text, Icons icon, ButtonListener listener) {
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

	public void setUpdateInfoTableContent (VisTable content) {
		updateInfoTable.clear();
		if (content != null)
			updateInfoTable.add(content).right().expand().padRight(5);
	}

	public void updateUndoButtonText () {
		//perform update next frame, this is to allow menu to close before updating it's text, otherwise menu size
		//could change and it would miss user click and thus menu will remain opened
		Gdx.app.postRunnable(() -> {
			if (activeSceneTab != null) {
				String name = activeSceneTab.getNextUndoActionName();
				undoMenuItem.setText(name == null ? "Undo" : "Undo " + name);
				editMenu.pack();
			}
		});

	}

	public void setSceneTab (SceneTab sceneTab) {
		this.activeSceneTab = sceneTab;
		sceneController.setSceneTabActive(sceneTab != null);
		updateUndoButtonText();
	}

	public enum ControllerPolicy {
		NONE, PROJECT, SCENE
	}
}
