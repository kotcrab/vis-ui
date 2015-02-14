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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class ColorInputField extends VisValidableTextField {
	private ChannelBar.ColorBarListener callback;
	private int value;
	private int maxValue;

	public ColorInputField (int maxValue, final ChannelBar.ColorBarListener callback) {
		super(new ColorFieldValidator(maxValue));
		this.value = 0;
		this.maxValue = maxValue;
		this.callback = callback;

		setProgrammaticChangeEvents(false);
		setMaxLength(3);
		setTextFieldFilter(new NumberFilter());
		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (getText().length() > 0)
					value = Integer.valueOf(getText());
			}
		});

		addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				ColorInputField field = (ColorInputField) event.getListenerActor();
				if (character == '+') field.changeValue(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? 10 : 1);
				if (character == '-') field.changeValue(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) ? -10 : -1);

				if (character != 0) {
					callback.valueChanged(getValue());
				}

				return true;
			}
		});
	}

	public void changeValue (int byValue) {
		this.value += byValue;

		if (value > maxValue) value = maxValue;
		if (value < 0) value = 0;

		updateUI();
	}

	private void updateUI () {
		setText(String.valueOf(value));
		setCursorPosition(getMaxLength());
	}

	public int getValue () {
		return value;
	}

	public void setValue (int value) {
		this.value = value;
		updateUI();
	}

	interface ColorBarListener {
		public void updateFields ();

		public void draw (Pixmap pixmap);
	}

	private static class NumberFilter implements TextFieldFilter {
		@Override
		public boolean acceptChar (VisTextField textField, char c) {
			return Character.isDigit(c);
		}
	}

	private static class ColorFieldValidator implements InputValidator {
		private int maxValue;

		public ColorFieldValidator (int maxValue) {
			this.maxValue = maxValue;
		}

		@Override
		public boolean validateInput (String input) {
			if (input.equals("")) return false;

			Integer number = Integer.parseInt(input);
			if (number > maxValue) return false;

			return true;
		}
	}
}
