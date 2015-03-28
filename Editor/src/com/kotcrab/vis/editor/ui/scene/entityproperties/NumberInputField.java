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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import org.lwjgl.input.Keyboard;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.floatToString;

class NumberInputField extends VisValidableTextField {

	private static FieldFilter sharedFieldFilter = new FieldFilter();
	private static FieldValidator sharedFieldValidator = new FieldValidator();

	public NumberInputField (FocusListener sharedFocusListener, ChangeListener sharedChangeListener) {
		addValidator(sharedFieldValidator);

		//without disabling it, it would case to set old values from new entities on switch
		setProgrammaticChangeEvents(false);

		addListener(sharedFocusListener);
		addListener(sharedChangeListener);
		setTextFieldFilter(sharedFieldFilter);
	}

	@Override
	protected InputListener createInputListener () {
		return new InputFieldListener();
	}

	public class InputFieldListener extends TextFieldClickListener {
		private TimerRepeatTask timerTask;
		private boolean keyTypedReturnValue;

		public InputFieldListener () {
			timerTask = new TimerRepeatTask();
		}

		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			return super.keyDown(event, keycode);
		}

		@Override
		public boolean keyTyped (InputEvent event, char character) {
			keyTypedReturnValue = false;

			checkKeys();

			if (character == '-' && NumberInputField.this.getCursorPosition() > 0 && getText().startsWith("-") == false)
				return keyTypedReturnValue;

			if (character == '.' && getText().contains(".")) return keyTypedReturnValue;

			return (keyTypedReturnValue || super.keyTyped(event, character));
		}

		private void checkKeys () {
			float delta = 0;
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) delta = 1;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) delta = 10;

			if (delta != 0) {
				//current workaround for https://github.com/libgdx/libgdx/pull/2592
				if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) changeFieldValue(delta * -1);
				if (Gdx.input.isKeyPressed(Keys.PLUS)) changeFieldValue(delta);

				if (keyTypedReturnValue) {
					timerTask.cancel();
					Timer.schedule(timerTask, 0.1f);
				}
			}
		}

		private void changeFieldValue (float value) {
			keyTypedReturnValue = true;

			try {
				float fieldValue = Float.parseFloat(getText());
				fieldValue += value;

				int lastPos = getCursorPosition();
				setText(floatToString(fieldValue));
				NumberInputField.this.setCursorPosition(lastPos);
			} catch (NumberFormatException ex) {
			}
		}

		private class TimerRepeatTask extends Task {
			@Override
			public void run () {
				checkKeys();
			}
		}
	}

	private static class FieldValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
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
		@Override
		public boolean acceptChar (VisTextField textField, char c) {
			//if(textField.getCursorPosition() > 0 && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && c == '-') return false;
			//if(textField.getCursorPosition() > 0 && c == '-') return false;
			if (c == '.') return true;
			if (c == '-') return true;
			if (c == '+') return false;

			if (c == '?') return true;

			return Character.isDigit(c);
		}
	}
}
