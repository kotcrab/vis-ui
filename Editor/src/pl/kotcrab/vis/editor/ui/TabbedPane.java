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

package pl.kotcrab.vis.editor.ui;

import pl.kotcrab.vis.editor.Tab;
import pl.kotcrab.vis.editor.TabbedPaneListener;
import pl.kotcrab.vis.ui.VisTable;
import pl.kotcrab.vis.ui.VisUI;
import pl.kotcrab.vis.ui.widget.VisTextButton;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class TabbedPane {
	private static final Drawable bottomBar = VisUI.skin.getDrawable("list-selection");

	private VisTable tabItems;
	private VisTable mainTable;

	private Array<Tab> tabs;
	private ButtonGroup group;

	private TabbedPaneListener listener;

	public TabbedPane (TabbedPaneListener listener) {
		this.listener = listener;

		group = new ButtonGroup();

		mainTable = new VisTable();
		tabItems = new VisTable();

		tabs = new Array<Tab>();

		mainTable.add(tabItems).padTop(2).left().expand();
		mainTable.row();
		mainTable.add(new Image(bottomBar)).expand().fill();
		mainTable.setBackground(VisUI.skin.getDrawable("menu-bg"));
	}

	public void add (final Tab tab) {
		tabs.add(tab);
		rebuildTabsTable();
	}

	private void rebuildTabsTable () {
		tabItems.clear();

		for (final Tab tab : tabs) {
			final VisTextButton button = new VisTextButton(tab.getButtonText(), "toggle");
			button.setFocusBorderEnabled(false);

			tabItems.add(button);
			group.add(button);

			if (tabs.size == 1) {
				button.setChecked(true);
				listener.switchTab(tab);
			}

			button.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if (button.isChecked()) listener.switchTab(tab);
				}
			});
		}
	}

	public Table getTable () {
		return mainTable;
	}
}
