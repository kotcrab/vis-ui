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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.ui.OptionDialogAdapter;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.DialogUtils;
import com.kotcrab.vis.ui.util.DialogUtils.OptionDialogType;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

public class TabbedPane {
	private static final Drawable highlightBg = VisUI.getSkin().getDrawable("list-selection");

	private VisTable tabsTable;
	private VisTable mainTable;

	private Array<Tab> tabs;
	private ObjectMap<Tab, TabButtonTable> tabsButtonMap;
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
		mainTable.add(new Image(highlightBg)).expand().fill().height(1);
		mainTable.setBackground(VisUI.getSkin().getDrawable("menu-bg"));
	}

	public void add (Tab tab) {
		tab.setPane(this);
		tabs.add(tab);

		rebuildTabsTable();
		switchTab(tab);
	}

	public void add (int index, Tab tab) {
		tab.setPane(this);
		tabs.insert(index, tab);
		rebuildTabsTable();
	}

	public void remove (Tab tab) {
		remove(tab, true);
	}

	public void remove (final Tab tab, boolean ignoreTabDirty) {
		if(ignoreTabDirty) {
			removeTab(tab);
			return;
		}

		if (tab.isSavable() && tab.isDirty()) {
				DialogUtils.showOptionDialog(Editor.instance.getStage(), "Unsaved changes", "Do you want to save changes in this resource before closing it?",
						OptionDialogType.YES_NO_CANCEL, new OptionDialogAdapter() {
							@Override
							public void yes () {
								tab.save();
								removeTab(tab);
							}

							@Override
							public void no () {
								removeTab(tab);
							}
						});
		} else
			removeTab(tab);
	}

	private void removeTab (Tab tab) {
		int index = tabs.indexOf(tab, true);
		boolean success = tabs.removeValue(tab, true);

		if (success) {
			tab.setPane(null);
			notifyListenersRemoved(tab);

			if (tabs.size == 0)
				notifyListenersRemovedAll();
			else if (activeTab == tab && index != 0) switchTab(--index);

			rebuildTabsTable();
		}
	}

	public void removeAll () {
		for (Tab tab : tabs)
			tab.setPane(null);

		tabs.clear();

		rebuildTabsTable();
		notifyListenersRemovedAll();
	}

	public void switchTab (int index) {
		tabsButtonMap.get(tabs.get(index)).select();
	}

	public void switchTab (Tab tab) {
		tabsButtonMap.get(tab).select();
	}

	public void updateTabTitle (Tab tab) {
		tabsButtonMap.get(tab).button.setText(tab.getButtonText());
	}

	private void rebuildTabsTable () {
		Tab lastSelectedTab = activeTab;
		tabsTable.clear();
		group.clear();
		tabsButtonMap.clear();

		for (final Tab tab : tabs) {
			final TabButtonTable buttonTable = new TabButtonTable(tab);

			tabsTable.add(buttonTable);
			tabsButtonMap.put(tab, buttonTable);
			group.add(buttonTable.button); //this will change activeTab

			if (tabs.size == 1) {
				buttonTable.select();
				notifyListenersSwitched(tab);
			}

			if (tab == lastSelectedTab)
				buttonTable.select(); // maintains current previous tab while rebuilding
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

	private class TabButtonTable extends VisTable {
		public VisTextButton button;
		public VisImageButton closeButton;
		private Tab tab;

		private VisTextButtonStyle buttonStyle;
		private VisImageButtonStyle closeButtonStyle;

		public TabButtonTable (final Tab tab) {
			this.tab = tab;
			button = new VisTextButton(tab.getButtonText(), "toggle");
			button.setFocusBorderEnabled(false);

			closeButton = new VisImageButton("close");
			closeButton.getImage().setScaling(Scaling.fill);
			closeButton.getImage().setColor(Color.RED);

			addListeners();

			buttonStyle = (VisTextButtonStyle) button.getStyle();
			closeButtonStyle = closeButton.getStyle();

			add(button);
			if (tab.isCloseableByUser()) add(closeButton).size(14, button.getHeight());
		}

		private void addListeners () {
			closeButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					TabbedPane.this.remove(tab, false);
				}
			});

			button.addListener(new InputListener() {
				@Override
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int mouseButton) {
					closeButtonStyle.up = buttonStyle.down;
					return false;
				}

				@Override
				public boolean mouseMoved (InputEvent event, float x, float y) {
					if (activeTab != tab)
						closeButtonStyle.up = buttonStyle.over;
					return false;
				}

				@Override
				public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
					if (activeTab != tab)
						closeButtonStyle.up = buttonStyle.up;
				}

				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					if (activeTab != tab && Gdx.input.justTouched() == false)
						closeButtonStyle.up = buttonStyle.over;
				}
			});

			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (button.isChecked()) {
						if (activeTab != null) {
							TabButtonTable table = tabsButtonMap.get(activeTab);
							if (table != null) table.deselect();
						}


						if (activeTab != null) activeTab.onHide();
						activeTab = tab;
						notifyListenersSwitched(tab);
						tab.onShow();
						closeButtonStyle.up = buttonStyle.down;
						closeButtonStyle.over = null;
					}
				}
			});
		}

		public void select () {
			button.setChecked(true);
		}

		public void deselect () {
			closeButtonStyle.up = buttonStyle.up;
			closeButtonStyle.over = buttonStyle.over;
		}
	}
}
