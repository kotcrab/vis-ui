/**
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

package pl.kotcrab.vis.editor.module;

import pl.kotcrab.vis.editor.App;
import pl.kotcrab.vis.editor.Editor;
import pl.kotcrab.vis.editor.event.Event;
import pl.kotcrab.vis.editor.event.EventListener;
import pl.kotcrab.vis.editor.event.ProjectStatusEvent;
import pl.kotcrab.vis.editor.ui.StartPageTab;
import pl.kotcrab.vis.editor.ui.tab.Tab;
import pl.kotcrab.vis.editor.ui.tab.TabbedPane;
import pl.kotcrab.vis.editor.ui.tab.TabbedPaneListener;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TabsModule extends EditorModule implements EventListener {
	private Editor editor;

	private TabbedPane tabbedPane;

	private StartPageTab startPageTab;

	public TabsModule () {
		editor = Editor.instance;

		tabbedPane = new TabbedPane(new TabbedPaneListener() {
			@Override
			public void switched (Tab tab) {
				editor.tabChanged(tab);
			}

			@Override
			public void removed (Tab tab) {

			}

			@Override
			public void removedAll () {
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

	@Override
	public void added () {
		App.eventBus.register(this);
		addToStage(editor.getRoot());
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	public void addToStage (Table root) {
		root.add(tabbedPane.getTable()).fillX().expandX().row();
	}

	@Override
	public boolean onEvent (Event e) {
		if (e instanceof ProjectStatusEvent) {
			ProjectStatusEvent event = (ProjectStatusEvent)e;
			if (event.status == ProjectStatusEvent.Status.Loaded)
				tabbedPane.remove(startPageTab);
			else
				tabbedPane.add(0, startPageTab);

		}

		return false;
	}

}
