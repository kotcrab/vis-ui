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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** @author Kotcrab */
public class AbstractSuggestionPopup extends PopupMenu {
	private static final Vector2 tmpVector = new Vector2();
	public static final int MAX_SUGGESTIONS = 10;

	final FileChooser chooser;

	public AbstractSuggestionPopup (FileChooser chooser) {
		super(chooser.getChooserStyle().popupMenuStyleName);
		this.chooser = chooser;
	}

	protected MenuItem createMenuItem (String name) {
		MenuItem item = new MenuItem(name);
		item.getImageCell().size(0);
		item.getShortcutCell().space(0).pad(0);
		item.getSubMenuIconCell().size(0).space(0).pad(0);
		return item;
	}

	protected void showMenu (Stage stage, VisTextField pathField) {
		Vector2 pos = pathField.localToStageCoordinates(tmpVector.setZero());
		float menuY;
		if (pos.y - getHeight() <= 0) {
			menuY = pos.y + pathField.getHeight() + getHeight() - 1;
		} else {
			menuY = pos.y + 1;
		}
		showMenu(stage, pos.x, menuY);
	}
}
