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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.floatToString;

class NumberInputField extends VisValidableTextField {
	private static FieldFilter sharedFieldFilter = new FieldFilter();
	private static FieldValidator sharedFieldValidator = new FieldValidator();

	private TimerRepeatTask repeatTask;

	public NumberInputField (FocusListener sharedFocusListener, ChangeListener sharedChangeListener) {
		addValidator(sharedFieldValidator);

		//without disabling it, it would cause to set old values from new entities on switch
		setProgrammaticChangeEvents(false);

		addListener(sharedFocusListener);
		addListener(sharedChangeListener);
		setTextFieldFilter(sharedFieldFilter);

		repeatTask = new TimerRepeatTask();
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
		public boolean keyDown (InputEvent event, int keycode) {
			repeatTask.cancel();

			int delta = 0;
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) delta = 1;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) delta = 10;

			if (delta != 0) {
				if (keycode == Keys.MINUS) {
					changeFieldValue(delta * -1);

					if (repeatTask.isScheduled() == false || repeatTask.valueDelta != delta) {
						repeatTask.valueDelta = delta * -1;
						repeatTask.cancel();
						Timer.schedule(repeatTask, 0.1f, 0.1f);
						return false;
					}

					return false;
				}
				if (keycode == Keys.PLUS) {
					changeFieldValue(delta);

					if (repeatTask.isScheduled() == false || repeatTask.valueDelta != delta) {
						repeatTask.valueDelta = delta;
						repeatTask.cancel();
						Timer.schedule(repeatTask, 0.1f, 0.1f);
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
		public boolean acceptChar (VisTextField field, char c) {
			if (c == '-' && field.getCursorPosition() > 0 && field.getText().startsWith("-") == false) return false;
			if (c == '.' && field.getText().contains(".")) return false;

			if (c == '.' || c == '-' || c == '?') return true;
			if (c == '+') return false;

			return Character.isDigit(c);
		}
	}
}
