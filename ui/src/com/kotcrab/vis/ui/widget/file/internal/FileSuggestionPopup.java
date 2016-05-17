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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;

/** @author Kotcrab */
public class FileSuggestionPopup extends AbstractSuggestionPopup {
	public FileSuggestionPopup (FileChooser chooser) {
		super(chooser);
	}

	public void pathFieldKeyTyped (Stage stage, Array<FileHandle> files, VisTextField pathField) {
		if (pathField.getText().length() == 0) {
			remove();
			return;
		}

		int suggestions = createSuggestions(files, pathField);
		if (suggestions == 0) {
			remove();
			return;
		}

		showMenu(stage, pathField);
	}

	private int createSuggestions (Array<FileHandle> files, VisTextField fileNameField) {
		clearChildren();
		int suggestions = 0;
		for (final FileHandle file : files) {
			if (file.name().startsWith(fileNameField.getText()) && file.name().equals(fileNameField.getText()) == false) {
				MenuItem item = createMenuItem((getTrimmedName(file.name())));
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

	private String getTrimmedName (String name) {
		if (name.length() > 40) {
			return name.substring(0, 40) + "...";
		} else {
			return name;
		}
	}
}
