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

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.ui.tab.StartPageTab;
import com.kotcrab.vis.editor.ui.tabbedpane.MainContentTab;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

public class TabsModule extends EditorModule implements EventListener {
	private TabbedPane tabbedPane;
	private TabbedPaneListener listener;

	private StartPageTab startPageTab;

	public TabsModule (TabbedPaneListener listener) {
		this.listener = listener;
	}

	@Override
	public void init () {
		tabbedPane = new TabbedPane();
		tabbedPane.addListener(listener);

		startPageTab = new StartPageTab();

		tabbedPane.add(startPageTab);
	}

	public void addTab (MainContentTab tab) {
		tabbedPane.add(tab);
	}

	public void removeTab (MainContentTab tab) {
		tabbedPane.remove(tab);
	}

	public void switchTab (MainContentTab tab) {
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

	public Array<Tab> getTabs () {
		return tabbedPane.getTabs();
	}

	public int getDirtyTabCount () {
		Array<Tab> tabs = getTabs();

		int count = 0;

		for (Tab tab : tabs)
			if (tab.isDirty()) count++;

		return count;
	}
}
