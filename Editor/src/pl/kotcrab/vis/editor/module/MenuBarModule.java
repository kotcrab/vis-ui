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
import pl.kotcrab.vis.editor.EditorListener;
import pl.kotcrab.vis.editor.ui.NewProjectDialog;
import pl.kotcrab.vis.ui.widget.Menu;
import pl.kotcrab.vis.ui.widget.MenuBar;
import pl.kotcrab.vis.ui.widget.MenuItem;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuBarModule {
	private EditorListener listener;

	private Stage stage;
	private MenuBar menuBar;

	public MenuBarModule () {
		listener = Editor.getInstnace();

		this.stage = listener.getStage();
		this.menuBar = new MenuBar(stage);

		createFileMenu();
		createHelpMenu();
	}

	private void createFileMenu () {
		Menu fileMenu = new Menu("File");
		menuBar.addMenu(fileMenu);

		fileMenu.addItem(new MenuItem("New project...", Assets.getIcon("new"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				stage.addActor(new NewProjectDialog().fadeIn());
			}
		}));

		fileMenu.addItem(new MenuItem("Load project...", Assets.getIcon("load")));
		fileMenu.addItem(new MenuItem("Close project"));

		fileMenu.addItem(new MenuItem("Exit", Assets.getIcon("exit"), new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				listener.requestExit();
			}
		}));
	}

	private void createHelpMenu () {
		Menu helpMenu = new Menu("Help");
		menuBar.addMenu(helpMenu);

		helpMenu.addItem(new MenuItem("Web"));
		helpMenu.addItem(new MenuItem("About"));
	}

	public void addToStage (Table root) {
		root.left().top();
		root.add(menuBar.getTable()).fillX().expandX();
	}
}
