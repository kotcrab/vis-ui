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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/** @author Kotcrab */
public class IndeterminateTextField {
	private VisValidatableTextField textField;
	private String text;
	private boolean indeterminate = false;

	public IndeterminateTextField () {
		this(new VisValidatableTextField());
	}

	public IndeterminateTextField (VisValidatableTextField textField) {
		this.textField = textField;
		textField.setStyle(new VisTextField.VisTextFieldStyle(textField.getStyle()));
		textField.setProgrammaticChangeEvents(false);
		textField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (indeterminate) {
					textField.getStyle().fontColor = Color.WHITE;
					indeterminate = false;
				}

				text = textField.getText();
			}
		});
		text = textField.getText();
	}

	public void setText (String text) {
		this.text = text;
		updateText();
	}

	public String getText () {
		if (indeterminate) throw new IllegalStateException("Cannot get text when field is in indeterminate state");
		return text;
	}

	private void updateText () {
		if (indeterminate) {
			textField.setText("<?>");
			textField.getStyle().fontColor = Color.LIGHT_GRAY;
		} else {
			textField.setText(text);
			textField.getStyle().fontColor = Color.WHITE;
		}
	}

	public VisValidatableTextField getTextField () {
		return textField;
	}

	public boolean isIndeterminate () {
		return indeterminate;
	}

	public void setIndeterminate (boolean indeterminate) {
		this.indeterminate = indeterminate;
		updateText();
	}

	public void setDisabled (boolean disabled) {
		textField.setDisabled(disabled);
	}
}
