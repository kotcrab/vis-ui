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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.util.gdx.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class SearchField extends VisTable {
	private VisTextField searchTextField;

	public SearchField () {
		super(true);

		searchTextField = new VisValidableTextField();
		add(new Image(Assets.getIcon(Icons.SEARCH))).spaceRight(3);
		add(searchTextField).width(200).spaceRight(0);

		VisImageButton clearButton = new VisImageButton(VisUI.getSkin().getDrawable("icon-close"));
		add(clearButton).space(0);

		clearButton.addListener(new VisChangeListener((event, actor) -> {
			searchTextField.setText("");
			searchTextField.setInputValid(true);
		}));
	}

	public void setText (String str) {
		searchTextField.setText(str);
	}

	public String getText () {
		return searchTextField.getText();
	}

	public boolean isInputValid () {
		return searchTextField.isInputValid();
	}

	public void setInputValid (boolean inputValid) {
		searchTextField.setInputValid(inputValid);
	}
}
