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

import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.Tab;
import pl.kotcrab.vis.editor.TabbedPaneListener;
import pl.kotcrab.vis.editor.ui.StartPageTab;
import pl.kotcrab.vis.editor.ui.TabbedPane;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TabsModule extends ModuleAdapter {
	private Editor editor;

	private Stage stage;
	private TabbedPane tabbedPane;

	public TabsModule () {
		editor = Editor.instance;
		stage = editor.getStage();
		
		tabbedPane = new TabbedPane(new TabbedPaneListener() {
			@Override
			public void switchTab (Tab tab) {
				editor.tabChanged(tab);
			}
		});

		tabbedPane.add(new StartPageTab());
	}

	@Override
	public void added () {
		addToStage(editor.getRoot());
	}

	public void addToStage (Table root) {
		root.add(tabbedPane.getTable()).fillX().expandX().row();
	}

}
