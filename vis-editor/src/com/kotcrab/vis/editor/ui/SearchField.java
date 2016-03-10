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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetsUIModule;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * Search field used in {@link AssetsUIModule}
 * @author Kotcrab
 */
public class SearchField extends VisTable {
	private SearchFieldListener listener;
	private VisValidatableTextField searchField;

	public SearchField (SearchFieldListener listener) {
		super(false);
		this.listener = listener;
		this.searchField = new VisValidatableTextField();

		VisImageButton clearButton = new VisImageButton(Icons.CLOSE.drawable());

		add(new Image(Icons.SEARCH.drawable())).spaceRight(3);
		add(searchField).width(200);
		add(clearButton).height(searchField.getHeight());

		clearButton.addListener(new VisChangeListener((event, actor) -> {
			clearSearch();
		}));

		searchField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				boolean result = listener.searchTextChanged(searchField.getText());
				searchField.setInputValid(result);
				return true;
			}
		});
	}

	public String getText () {
		return searchField.getText();
	}

	public void clearSearch () {
		searchField.setText("");
		searchField.setInputValid(listener.searchTextChanged(""));
	}

	public interface SearchFieldListener {
		boolean searchTextChanged (String newText);
	}
}
