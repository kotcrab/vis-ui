/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.module.project.ExportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.ui.NewProjectDialog;
import com.kotcrab.vis.editor.ui.ProjectStatusWidgetController;
import com.kotcrab.vis.editor.ui.SceneStatusWidgetController;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.ui.scene.SceneMenuButtonsListener;
import com.kotcrab.vis.editor.util.EditorException;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class MenuBarModule extends EditorModule {
	private ProjectModuleContainer projectContainer;
	private Editor editor;

	private Stage stage;
	private MenuBar menuBar;

	private ProjectStatusWidgetController projectController;
	private SceneStatusWidgetController sceneController;

	private FileChooser chooser;

	private ProjectIOModule projectIOModule;
	private SceneMenuButtonsListener sceneButtonsListener;

	public MenuBarModule (ProjectModuleContainer moduleContainer) {
		editor = Editor.instance;
		stage = editor.getStage();
		projectContainer = moduleContainer;

		menuBar = new MenuBar(stage);

		projectController = new ProjectStatusWidgetController();
		sceneController = new SceneStatusWidgetController();

		chooser = new FileChooser(Mode.OPEN);
		chooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setListener(new FileChooserAdapter() {
			@Override
			public void selected (FileHandle file) {
				loadProject(file);
			}
		});

		createFileMenu();
		createSceneMenu();
		createHelpMenu();
	}

	@Override
	public void init () {
		projectIOModule = container.get(ProjectIOModule.class);
	}

	private void loadProject (FileHandle file) {
		try {
			container.get(ProjectIOModule.class).load(file);
		} catch (EditorException e) {
			DialogUtils.showErrorDialog(stage, e.getMessage(), e);
		}
	}

	private void createFileMenu () {
		MenuItem closeProject = new MenuItem("Close Project", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				editor.requestProjectUnload();
			}
		});

		MenuItem settings = new MenuItem("Settings", Assets.getIcon("settings"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				editor.showSettingsWindow();
			}
		});

		MenuItem quickExport = new MenuItem("Quick Export", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				ExportModule exportModule = projectContainer.get(ExportModule.class);
				exportModule.export(true);
			}
		});

		MenuItem export = new MenuItem("Export", Assets.getIcon("export"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				ExportModule exportModule = projectContainer.get(ExportModule.class);
				exportModule.export(false);
			}
		});

		Menu menu = new Menu("File");
		menuBar.addMenu(menu);


		menu.addItem(new MenuItem("New Project...", Assets.getIcon("new"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewProjectDialog(projectIOModule).fadeIn());
			}
		}));

		menu.addItem(new MenuItem("Load Project...", Assets.getIcon("load"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(chooser.fadeIn());
			}
		}));


		menu.addItem(closeProject);

		menu.addSeparator();
		menu.addItem(quickExport);
		menu.addItem(export);
		menu.addSeparator();
		menu.addItem(settings);
		menu.addSeparator();

		menu.addItem(new MenuItem("Exit", Assets.getIcon("exit"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				editor.requestExit();
			}
		}));

		projectController.addButton(closeProject);
		projectController.addButton(quickExport);
		projectController.addButton(export);
	}

	private void createSceneMenu () {
		Menu menu = new Menu("Scene");
		menuBar.addMenu(menu);

		MenuItem newScene = new MenuItem("New Scene...", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
				SceneIOModule sceneIO = projectContainer.get(SceneIOModule.class);
				stage.addActor(new NewSceneDialog(fileAccess, sceneIO).fadeIn());
			}
		});


		projectController.addButton(newScene);

		MenuItem sceneSettings = new MenuItem("Scene Settings", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				sceneButtonsListener.showSceneSettings();
			}
		});

		sceneController.addButton(sceneSettings);

		menu.addItem(newScene);
		menu.addItem(sceneSettings);
	}

	private void createHelpMenu () {
		Menu menu = new Menu("Help");
		menuBar.addMenu(menu);

		menu.addItem(new MenuItem("Web"));
		menu.addItem(new MenuItem("About"));
	}

	public Table getTable () {
		return menuBar.getTable();
	}

	public void setSceneButtonsListener (SceneMenuButtonsListener listener) {
		sceneButtonsListener = listener;
		sceneController.listenrChanged(listener);
	}

	@Override
	public void dispose () {
		projectController.dispose();
	}
}
