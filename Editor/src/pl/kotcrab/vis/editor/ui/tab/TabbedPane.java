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

package pl.kotcrab.vis.editor.ui.tab;

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

	private TabbedPaneListener listener;

	public TabbedPane (TabbedPaneListener listener) {
		this.listener = listener;

		group = new ButtonGroup<Button>();

		mainTable = new VisTable();
		tabsTable = new VisTable();

		tabs = new Array<Tab>();
		tabsButtonMap = new ObjectMap<Tab, VisTextButton>();

		mainTable.add(tabsTable).padTop(2).left().expand();
		mainTable.row();
		// if height is not set bottomBar may sometimes disappear for some reason
		mainTable.add(new Image(VisUI.skin.getDrawable("list-selection"))).expand().fill().height(1);
		mainTable.setBackground(VisUI.skin.getDrawable("menu-bg"));
	}

	public void add (Tab tab) {
		tabs.add(tab);
		if (activeTab != null) activeTab.onHide();
		activeTab = tab;

		rebuildTabsTable();
	}

	public void add (int index, Tab tab) {
		tabs.insert(index, tab);
		rebuildTabsTable();
	}

	public boolean remove (Tab tab) {
		boolean success = tabs.removeValue(tab, true);

		if (success) {
			rebuildTabsTable();
			listener.removed(tab);

			if (activeTab == tab) switchTab(0);
			if (tabs.size == 0) listener.removedAll();
		}

		return success;
	}

	public void switchTab (int index) {
		group.getButtons().get(index).setChecked(true);
	}

	public void switchTab (Tab tab) {
		tabsButtonMap.get(tab).setChecked(true);
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
					if (activeTab != null) activeTab.onHide();
					activeTab = tab;
					listener.switched(tab);
					tab.onShow();
				}
			});

			if (tabs.size == 1) button.setChecked(true);
			if (tab == activeTab) button.setChecked(true); // maintains current previous tab while rebuilding
		}
	}

	public Table getTable () {
		return mainTable;
	}
}
