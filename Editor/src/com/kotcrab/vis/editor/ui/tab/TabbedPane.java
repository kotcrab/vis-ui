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

package com.kotcrab.vis.editor.ui.tab;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class TabbedPane {
	private VisTable tabsTable;
	private VisTable mainTable;

	private Array<Tab> tabs;
	private ObjectMap<Tab, VisTextButton> tabsButtonMap;
	private ButtonGroup<Button> group;

	private Tab activeTab;

	private Array<TabbedPaneListener> listeners;

	public TabbedPane (TabbedPaneListener listener) {
		listeners = new Array<>();
		listeners.add(listener);

		group = new ButtonGroup<>();

		mainTable = new VisTable();
		tabsTable = new VisTable();

		tabs = new Array<>();
		tabsButtonMap = new ObjectMap<>();

		mainTable.add(tabsTable).padTop(2).left().expand();
		mainTable.row();
		// if height is not set bottomBar may sometimes disappear for some reason
		mainTable.add(new Image(VisUI.skin.getDrawable("list-selection"))).expand().fill().height(1);
		mainTable.setBackground(VisUI.skin.getDrawable("menu-bg"));
	}

	public void add (Tab tab) {
		tab.setPane(this);
		tabs.add(tab);
		if (activeTab != null) activeTab.onHide();
		activeTab = tab;

		rebuildTabsTable();
	}

	public void add (int index, Tab tab) {
		tab.setPane(this);
		tabs.insert(index, tab);
		rebuildTabsTable();
	}

	public boolean remove (Tab tab) {
		boolean success = tabs.removeValue(tab, true);

		if (success) {
			tab.setPane(null);
			rebuildTabsTable();
			notifyListenersRemoved(tab);

			if (tabs.size == 0)
				notifyListenersRemovedAll();
			else if (activeTab == tab) switchTab(0);
		}

		return success;
	}

	public void switchTab (int index) {
		group.getButtons().get(index).setChecked(true);
	}

	public void switchTab (Tab tab) {
		tabsButtonMap.get(tab).setChecked(true);
	}

	public void updateTabTitle (Tab tab) {
		tabsButtonMap.get(tab).setText(tab.getButtonText());
	}

	private void rebuildTabsTable () {
		tabsTable.clear();
		group.clear();
		tabsButtonMap.clear();

		for (final Tab tab : tabs) {
			final VisTextButton button = new VisTextButton(tab.getButtonText(), "toggle");
			button.setFocusBorderEnabled(false);

			tabsTable.add(button);
			group.add(button);
			tabsButtonMap.put(tab, button);

			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if(button.isChecked()) {
						if (activeTab != null) activeTab.onHide();
						activeTab = tab;
						notifyListenersSwitched(tab);
						tab.onShow();
					}
				}
			});

			if (tabs.size == 1) button.setChecked(true);
			if (tab == activeTab) button.setChecked(true); // maintains current previous tab while rebuilding
		}
	}

	public Table getTable () {
		return mainTable;
	}

	public void addListener (TabbedPaneListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener (TabbedPaneListener listener) {
		return listeners.removeValue(listener, true);
	}

	private void notifyListenersSwitched (Tab tab) {
		for (TabbedPaneListener listener : listeners)
			listener.switchedTab(tab);
	}

	private void notifyListenersRemoved (Tab tab) {
		for (TabbedPaneListener listener : listeners)
			listener.removedTab(tab);
	}

	private void notifyListenersRemovedAll () {
		for (TabbedPaneListener listener : listeners)
			listener.removedAllTabs();
	}
}
