/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.ui.tab.AssetsUsagesTab;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane.TabbedPaneStyle;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneListener;

/**
 * Quick access bar is located at the bottom of VisEditor screen, this is tabbed pane with assets manager, asests usages etc.
 * Plugins can add custom tabs here.
 * @author Kotcrab
 */
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
		tabStyle.separatorBar = null;

		tabbedPane = new TabbedPane(tabStyle);
		tabbedPane.addListener(listener);
		tabbedPane.setAllowTabDeselect(true);
	}

	public Tab getAssetsUsagesTabForFile (FileHandle file) {
		Array<Tab> tabs = tabbedPane.getTabs();

		for (Tab tab : tabs) {
			if (tab instanceof AssetsUsagesTab) {
				AssetsUsagesTab usagesTab = (AssetsUsagesTab) tab;

				if (usagesTab.getUsageFile().equals(file))
					return tab;
			}
		}

		return null;
	}

	public void closeAllUsagesTabForFile (FileHandle file) {
		Array<Tab> tabs = tabbedPane.getTabs();
		Array<Tab> tabsToClose = new Array<>();

		for (Tab tab : tabs) {
			if (tab instanceof AssetsUsagesTab) {
				AssetsUsagesTab usagesTab = (AssetsUsagesTab) tab;

				if (usagesTab.getUsageFile().equals(file))
					tabsToClose.add(tab);
			}
		}

		for (Tab tab : tabsToClose)
			removeTab(tab);
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
