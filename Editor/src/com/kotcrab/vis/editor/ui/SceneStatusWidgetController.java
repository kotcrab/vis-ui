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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.ui.scene.SceneMenuButtonsListener;

public class SceneStatusWidgetController {
	private Array<Button> buttons;
	private boolean loaded = false;

	public SceneStatusWidgetController () {
		buttons = new Array<>();
	}

	public void listenerChanged (SceneMenuButtonsListener listener) {
		if (listener == null)
			loaded = false;
		else
			loaded = true;

		updateWidgets();

	}

	private void updateWidgets () {
		for (Button b : buttons)
			b.setDisabled(!loaded);
	}

	public void addButton (Button button) {
		buttons.add(button);
		button.setDisabled(!loaded);
	}
}
