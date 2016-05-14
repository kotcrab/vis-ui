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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** @author Kotcrab */
public class SuggestionPopupMenu extends PopupMenu {
	private static final Vector2 tmpVector = new Vector2();
	private static final int MAX_SUGGESTIONS = 10;

	private final FileChooser chooser;

	public SuggestionPopupMenu (FileChooser chooser) {
		super(chooser.getChooserStyle().popupMenuStyleName);
		this.chooser = chooser;
	}

	public void fileNameKeyTyped (Stage stage, ObjectMap.Keys<FileHandle> files, VisTextField fileNameField) {
		if (fileNameField.getText().length() == 0) {
			remove();
			return;
		}

		int suggestions = createSuggestions(files, fileNameField);
		if (suggestions == 0) {
			remove();
			return;
		}

		Vector2 pos = fileNameField.localToStageCoordinates(tmpVector.setZero());
		float menuY;
		if (pos.y - getHeight() <= 0) {
			menuY = pos.y + fileNameField.getHeight() + getHeight() - 1;
		} else {
			menuY = pos.y + 1;
		}
		showMenu(stage, pos.x, menuY);
	}

	private int createSuggestions (ObjectMap.Keys<FileHandle> files, VisTextField fileNameField) {
		clearChildren();
		int suggestions = 0;
		for (final FileHandle file : files) {
			if (file.name().startsWith(fileNameField.getText())) {
				MenuItem item = new MenuItem(getCroppedName(file.name()));
				item.getImageCell().size(0);
				item.getShortcutCell().space(0).pad(0);
				item.getSubMenuIconCell().size(0).space(0).pad(0);
				item.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						chooser.highlightFiles(file);
					}
				});
				addItem(item);
				suggestions++;
			}

			if (suggestions == MAX_SUGGESTIONS) {
				break;
			}
		}

		return suggestions;
	}

	private String getCroppedName (String name) {
		if (name.length() > 40) {
			return name.substring(0, 40) + "...";
		} else {
			return name;
		}
	}
}
