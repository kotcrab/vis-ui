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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;

/**
 * Bar with menus displayed usually on top of the stage.
 * @author Kotcrab
 */
public class MenuBar {
	private static final Drawable BUTTTON_DEFAULT = VisUI.getSkin().getDrawable("button");

	private Table mainTable;
	private Table menuItems;

	private Menu currentMenu;

	private Array<Menu> menus = new Array<Menu>();

	public MenuBar () {
		Skin skin = VisUI.getSkin();

		menuItems = new VisTable();

		mainTable = new VisTable() {
			@Override
			protected void sizeChanged () {
				super.sizeChanged();
				closeMenu();
			}
		};

		mainTable.left();
		mainTable.add(menuItems);
		mainTable.setBackground(skin.getDrawable("menu-bg"));
	}

	public void addMenu (Menu menu) {
		menus.add(menu);
		menu.setMenuBar(this);
		menuItems.add(menu.getOpenButton());
	}

	public boolean removeMenu (Menu menu) {
		boolean removed = menus.removeValue(menu, true);

		if (removed) {
			menu.setMenuBar(null);
			menuItems.removeActor(menu.getOpenButton());
		}

		return removed;
	}

	public void insertMenu (int index, Menu menu) {
		menus.insert(index, menu);
		menu.setMenuBar(this);
		rebuild();
	}

	private void rebuild () {
		menuItems.clear();
		for (Menu menu : menus)
			menuItems.add(menu.getOpenButton());
	}

	/** Closes currently open menu (if any). Used by framework and typically there is no need to call this manually */
	public void closeMenu () {
		if (currentMenu != null) {
			deselectButton(currentMenu.getOpenButton());
			currentMenu.remove();
			currentMenu = null;
		}
	}

	Menu getCurrentMenu () {
		return currentMenu;
	}

	void setCurrentMenu (Menu newMenu) {
		if (newMenu != null) selectButton(newMenu.getOpenButton());
		if (currentMenu != null) deselectButton(currentMenu.getOpenButton());
		currentMenu = newMenu;
	}

	/** Returns table containing all menus that should be added to Stage, typically with expandX and fillX properties. */
	public Table getTable () {
		return mainTable;
	}

	public void selectButton (TextButton button) {
		button.getStyle().up = button.getStyle().over;
	}

	public void deselectButton (TextButton button) {
		button.getStyle().up = BUTTTON_DEFAULT;
	}
}
