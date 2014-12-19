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

package pl.kotcrab.vis.editor.module;

import pl.kotcrab.vis.editor.Assets;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.EditorException;
import pl.kotcrab.vis.editor.ProjectIO;
import pl.kotcrab.vis.editor.ui.NewProjectDialog;
import pl.kotcrab.vis.editor.ui.NewSceneDialog;
import pl.kotcrab.vis.editor.ui.ProjectStatusWidgetController;
import pl.kotcrab.vis.ui.util.DialogUtils;
import pl.kotcrab.vis.ui.widget.Menu;
import pl.kotcrab.vis.ui.widget.MenuBar;
import pl.kotcrab.vis.ui.widget.MenuItem;
import pl.kotcrab.vis.ui.widget.file.FileChooser;
import pl.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import pl.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import pl.kotcrab.vis.ui.widget.file.FileChooserAdapter;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuBarModule extends ModuleAdapter {
	private Editor editor;

	private Stage stage;
	private MenuBar menuBar;

	private ProjectStatusWidgetController controller;
	private FileChooser chooser;

	public MenuBarModule () {
		editor = Editor.instance;

		this.stage = editor.getStage();
		this.menuBar = new MenuBar(stage);

		controller = new ProjectStatusWidgetController();

		chooser = new FileChooser(Mode.OPEN);
		chooser.setSelectionMode(SelectionMode.FILES_AND_DIRECTORIES);
		chooser.setListener(new FileChooserAdapter() {
			@Override
			public void selected (FileHandle file) {
				try {
					ProjectIO.load(file.file());
				} catch (EditorException e) {
					DialogUtils.showErrorDialog(stage, e.getMessage(), e);
				}
			}
		});

		createFileMenu();
		createSceneMenu();
		createHelpMenu();
	}

	@Override
	public void added () {
		addToStage(Editor.instance.getRoot());
	}

	private void createFileMenu () {
		Menu menu = new Menu("File");
		menuBar.addMenu(menu);

		menu.addItem(new MenuItem("New project...", Assets.getIcon("new"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewProjectDialog().fadeIn());
			}
		}));

		menu.addItem(new MenuItem("Load project...", Assets.getIcon("load"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(chooser.fadeIn());
			}
		}));

		MenuItem closeProject = new MenuItem("Close project", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				editor.requestProjectUnload();
			}
		});

		menu.addItem(closeProject);
		controller.addButton(closeProject);

		menu.addSeparator();

		menu.addItem(new MenuItem("Exit", Assets.getIcon("exit"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				editor.requestExit();
			}
		}));
	}

	private void createSceneMenu () {
		Menu menu = new Menu("Scene");
		menuBar.addMenu(menu);

		MenuItem item = new MenuItem("New Scene...", new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewSceneDialog().fadeIn());
			}
		});

		controller.addButton(item);

		menu.addItem(item);
	}

	private void createHelpMenu () {
		Menu menu = new Menu("Help");
		menuBar.addMenu(menu);

		menu.addItem(new MenuItem("Web"));
		menu.addItem(new MenuItem("About"));
	}

	public void addToStage (Table root) {
		root.add(menuBar.getTable()).fillX().expandX().row();
	}

	@Override
	public void dispose () {
		controller.dispose();
	}
}
