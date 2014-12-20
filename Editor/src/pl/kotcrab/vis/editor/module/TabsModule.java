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
import pl.kotcrab.vis.editor.TabAdapater;
import pl.kotcrab.vis.editor.TabbedPaneListener;
import pl.kotcrab.vis.editor.ui.TabbedPane;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TabsModule extends ModuleAdapter {
	private Editor editor;

	private Stage stage;
	private TabbedPane tabbedPane;

	public TabsModule () {
		editor = Editor.instance;

		this.stage = editor.getStage();
		this.tabbedPane = new TabbedPane(new TabbedPaneListener() {
			@Override
			public void switchTab (Tab tab) {

			}
		});

		tabbedPane.addTab(new TabAdapater() {
			@Override
			public String getButtonText () {
				return "Start Page";
			}
		});

		tabbedPane.addTab(new TabAdapater() {
			@Override
			public String getButtonText () {
				return "Tab 1";
			}
		});

		tabbedPane.addTab(new TabAdapater() {
			@Override
			public String getButtonText () {
				return "Tab 2";
			}
		});
	}

	@Override
	public void added () {
		addToStage(Editor.instance.getRoot());
	}

	public void addToStage (Table root) {
		root.add(tabbedPane.getTable()).fillX().expandX().row();
	}

}
