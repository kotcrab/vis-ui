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

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.ui.StartPageTab;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.ui.tab.TabbedPane;
import com.kotcrab.vis.editor.ui.tab.TabbedPaneListener;

public class TabsModule extends EditorModule implements EventListener {
	private Editor editor;

	private TabbedPane tabbedPane;

	private StartPageTab startPageTab;

	public TabsModule () {
		editor = Editor.instance;

		tabbedPane = new TabbedPane(new TabbedPaneListener() {
			@Override
			public void switchedTab (Tab tab) {
				editor.tabChanged(tab);
			}

			@Override
			public void removedTab (Tab tab) {

			}

			@Override
			public void removedAllTabs () {
				editor.tabChanged(null);
			}
		});

		startPageTab = new StartPageTab();

		tabbedPane.add(startPageTab);
	}

	public void addTab (Tab tab) {
		tabbedPane.add(tab);
	}

	public boolean removeTab (Tab tab) {
		return tabbedPane.remove(tab);
	}

	public void switchTab (Tab tab) {
		tabbedPane.switchTab(tab);
	}

	public void addListener (TabbedPaneListener listener) {
		tabbedPane.addListener(listener);
	}

	public boolean removeListener (TabbedPaneListener listener) {
		return tabbedPane.removeListener(listener);
	}

	public Table getTable () {
		return tabbedPane.getTable();
	}

	@Override
	public void added () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof ProjectStatusEvent) {
			ProjectStatusEvent event = (ProjectStatusEvent) e;
			if (event.status == ProjectStatusEvent.Status.Loaded)
				tabbedPane.remove(startPageTab);
			else {
				tabbedPane.removeAll();
				tabbedPane.add(startPageTab);
			}
		}

		return false;
	}
}
