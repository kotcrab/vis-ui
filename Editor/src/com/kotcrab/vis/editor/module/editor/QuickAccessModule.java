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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

public class QuickAccessModule extends EditorModule {
	private TabbedPane tabbedPane;
	private TabbedPaneListener listener;

	public QuickAccessModule (TabbedPaneListener listener) {
		this.listener = listener;
	}

	@Override
	public void init () {
		TabbedPaneStyle tabStyle = new TabbedPaneStyle(VisUI.getSkin().get(TabbedPaneStyle.class));
		tabStyle.buttonStyle = new VisTextButtonStyle(VisUI.getSkin().get("toggle", VisTextButtonStyle.class));
		tabStyle.buttonStyle.font = VisUI.getSkin().getFont("small-font");
		tabStyle.bottomBar = null;

		tabbedPane = new TabbedPane(tabStyle);
		tabbedPane.addListener(listener);
		tabbedPane.setAllowTabDeselect(true);
	}

	public void addTab (Tab tab) {
		tabbedPane.add(tab);
	}

	public void removeTab (Tab tab) {
		tabbedPane.remove(tab);
	}

	public void switchTab (Tab tab) {
		tabbedPane.switchTab(tab);
	}

	public void insertTab (int index, Tab tab) {
		tabbedPane.insert(index, tab);
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

	public Array<Tab> getTabs () {
		return tabbedPane.getTabs();
	}
}
