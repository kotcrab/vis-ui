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

package pl.kotcrab.vis.editor.ui.components;

import pl.kotcrab.vis.editor.ui.UI;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuItem {
	public TextButton button;

	public MenuItem (String text, ChangeListener listener) {
		this(text);
		button.addListener(listener);
	}

	public MenuItem (String text) {
		button = new TextButton(text, UI.skin, "menu");
		button.getLabel().setAlignment(Align.left);
	}

	public void addListener (ChangeListener listener) {
		button.addListener(listener);
	}
}
