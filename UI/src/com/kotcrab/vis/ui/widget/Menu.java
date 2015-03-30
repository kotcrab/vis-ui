/*
 * Copyright 2014-2015 Pawel Pastuszak
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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Menu used in MenuBar, it is a standard {@link PopupMenu} with tittle displayed in MenuBar
 * @author Kotcrab
 */
public class Menu extends PopupMenu {
	private MenuBar menuBar;
	public TextButton openButton;

	private String title;

	public Menu (String title) {
		this.title = title;

		openButton = new VisTextButton(title, "menu-bar");

		openButton.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if(menuBar.getCurrentMenu() == Menu.this) {
					menuBar.closeMenu();
					return true;
				}

				switchMenu();
				event.stop();
				return true;
			}

			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (menuBar.getCurrentMenu() != null) switchMenu();
			}
		});
	}

	public String getTitle () {
		return title;
	}

	private void switchMenu () {
		menuBar.closeMenu();
		showMenu();
	}

	private void showMenu () {
		Vector2 pos = openButton.localToStageCoordinates(new Vector2(0, 0));
		setPosition(pos.x, pos.y - getHeight());
		openButton.getStage().addActor(this);
		menuBar.setCurrentMenu(this);
	}

	@Override
	public boolean remove () {
		boolean result = super.remove();
		menuBar.setCurrentMenu(null);
		return result;
	}

	/** Called by MenuBar when this menu is added to it */
	void setMenuBar (MenuBar menuBar) {
		if (this.menuBar != null) throw new IllegalStateException("Menu was already added to MenuBar");
		this.menuBar = menuBar;
	}

	TextButton getOpenButton () {
		return openButton;
	}
}
