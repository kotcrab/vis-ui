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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import org.apache.commons.lang3.StringUtils;

import static com.kotcrab.vis.editor.util.NumberUtils.floatToString;

/**
 * Extended {@link VisValidatableTextField} made specifically for entering numeric values. Support keys ctrl/shift + plus/minus
 * to quickly change field value.
 * @author Kotcrab
 */
public class NumberInputField extends VisValidatableTextField {
	private static FieldValidator sharedFieldValidator = new FieldValidator();

	private TimerRepeatTask repeatTask;

	public NumberInputField (FocusListener sharedFocusListener, ChangeListener sharedChangeListener) {
		this(sharedFocusListener, sharedChangeListener, true);
	}

	/** @param floatInput if true field fill allow to enter floats, if false only integers will be allowed */
	public NumberInputField (FocusListener sharedFocusListener, ChangeListener sharedChangeListener, boolean floatInput) {
		addValidator(sharedFieldValidator);

		//without disabling it, it would cause to set old values from new entities on switch
		setProgrammaticChangeEvents(false);

		addListener(sharedFocusListener);
		addListener(sharedChangeListener);
		setTextFieldFilter(new FieldFilter(floatInput));

		repeatTask = new TimerRepeatTask();

		addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				fire(new ChangeEvent());
				return false;
			}
		});

		addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				int matches = StringUtils.countMatches(getText(), "-");
				if (matches > 1) {
					event.cancel();
					return;
				}

				if (matches == 1 && getText().startsWith("-") == false) {
					event.cancel();
				}
			}
		});
	}

	@Override
	protected InputListener createInputListener () {
		return new InputFieldListener();
	}

	private void changeFieldValue (float value) {
		try {
			float fieldValue = Float.parseFloat(getText());
			fieldValue += value;

			int lastPos = getCursorPosition();
			setText(floatToString(fieldValue));
			NumberInputField.this.setCursorPosition(lastPos);
			fire(new ChangeEvent());
		} catch (NumberFormatException ex) {
		}
	}

	public class InputFieldListener extends TextFieldClickListener {

		@Override
		public boolean keyTyped (InputEvent event, char character) {
			if (character == '-' && UIUtils.shift()) return false;
			return super.keyTyped(event, character);
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			repeatTask.cancel();

			int delta = 0;
			if (UIUtils.ctrl()) delta = 1;
			if (UIUtils.shift()) delta = 10;

			if (delta != 0) {
				if (keycode == Keys.MINUS) {
					changeFieldValue(delta * -1);

					if (repeatTask.isScheduled() == false || repeatTask.valueDelta != delta) {
						repeatTask.valueDelta = delta * -1;
						repeatTask.cancel();
						Timer.schedule(repeatTask, 0.2f, 0.2f);
						return false;
					}

					return false;
				}
				if (keycode == Keys.PLUS) {
					changeFieldValue(delta);

					if (repeatTask.isScheduled() == false || repeatTask.valueDelta != delta) {
						repeatTask.valueDelta = delta;
						repeatTask.cancel();
						Timer.schedule(repeatTask, 0.2f, 0.2f);
						return false;
					}

					return false;
				}

			}

			return super.keyDown(event, keycode);
		}

		@Override
		public boolean keyUp (InputEvent event, int keycode) {
			repeatTask.cancel();
			return super.keyUp(event, keycode);
		}
	}

	private class TimerRepeatTask extends Task {
		public int valueDelta;

		@Override
		public void run () {
			changeFieldValue(valueDelta);
		}
	}

	private static class FieldValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			if (input.endsWith(".")) return false;
			if (input.equals("?")) return true;

			try {
				Float.parseFloat(input);
				return true;
			} catch (NumberFormatException ex) {
			}

			return false;
		}
	}

	private static class FieldFilter implements TextFieldFilter {
		private boolean floatInput;

		public FieldFilter (boolean floatInput) {
			this.floatInput = floatInput;
		}

		@Override
		public boolean acceptChar (VisTextField field, char c) {
			if (c == '.' && field.getText().contains(".")) return false;
			if (field.getCursorPosition() > 0 && c == '-' && UIUtils.shift() == false && UIUtils.ctrl() == false)
				return false;
			if (field.getText().startsWith("-") && c == '-') return false;

			if (c == '-' || c == '?') return true;
			if (c == '.') return floatInput;
			if (c == '+') return false;

			return Character.isDigit(c);
		}
	}
}
