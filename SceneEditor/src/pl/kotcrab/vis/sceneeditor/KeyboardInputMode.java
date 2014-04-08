/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Input.Keys;

@SuppressWarnings({"rawtypes", "unchecked"})
class KeyboardInputMode {
	private KeyboardInputActionFinished listener;

	private EditType type;
	private SceneEditorSupport sup;
	private Object obj;

	private boolean active = false;

	private int startingValue;
	private int value;
	private String valueString;

	private float scaleRatio;
	private boolean lockRatio;

	public KeyboardInputMode (KeyboardInputActionFinished listener) {
		this.listener = listener;
	}

	public void setObject (EditType type, SceneEditorSupport sup, Object obj) {
		this.type = type;
		this.sup = sup;
		this.obj = obj;
		active = true;

		lockRatio = false;
		scaleRatio = 0;

		startingValue = getEditingValue();
		value = startingValue;
		valueString = String.valueOf(value);

		if (type == EditType.HEIGHT || type == EditType.WIDTH) {
			scaleRatio = sup.getWidth(obj) / sup.getHeight(obj);
		}
	}

	public void keyDown (int keycode) {
		if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_CANCEL) {
			cancel();
			return;
		}

		if (active) {
			if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_CONFIRM) finish();
			if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_BACKSPACE) removeDigit();

			if (type == EditType.HEIGHT || type == EditType.WIDTH) {
				if (keycode == SceneEditorConfig.KEY_SCALE_LOCK_RATIO) lockRatio = !lockRatio;
			}

			if (keycode == Keys.MINUS) {
				// if string is empty pressing minus would not decrease value, but we have to add '-' to text string
				if (valueString.length() == 0)
					valueString = "-";
				else
					decreaseValue();
			}

			if (keycode == Keys.PLUS) increaseValue();

			if (keycode == Keys.NUM_0 || keycode == Keys.NUMPAD_0) addDigit(0);
			if (keycode == Keys.NUM_1 || keycode == Keys.NUMPAD_1) addDigit(1);
			if (keycode == Keys.NUM_2 || keycode == Keys.NUMPAD_2) addDigit(2);
			if (keycode == Keys.NUM_3 || keycode == Keys.NUMPAD_3) addDigit(3);
			if (keycode == Keys.NUM_4 || keycode == Keys.NUMPAD_4) addDigit(4);
			if (keycode == Keys.NUM_5 || keycode == Keys.NUMPAD_5) addDigit(5);
			if (keycode == Keys.NUM_6 || keycode == Keys.NUMPAD_6) addDigit(6);
			if (keycode == Keys.NUM_7 || keycode == Keys.NUMPAD_7) addDigit(7);
			if (keycode == Keys.NUM_8 || keycode == Keys.NUMPAD_8) addDigit(8);
			if (keycode == Keys.NUM_9 || keycode == Keys.NUMPAD_9) addDigit(9);
		}
	}

	private void addDigit (int num) {
		valueString += String.valueOf(num);
		try {
			value = Integer.parseInt(valueString);
		} catch (NumberFormatException e) {

		}
		setEditingValue(value);
	}

	private void increaseValue () {
		value++;
		valueString = String.valueOf(value);
		setEditingValue(value);
	}

	private void decreaseValue () {
		value--;
		valueString = String.valueOf(value);
		setEditingValue(value);
	}

	private void removeDigit () {
		if (valueString.length() > 0) {
			valueString = valueString.substring(0, valueString.length() - 1);

			try {
				value = Integer.parseInt(valueString);
			} catch (NumberFormatException e) {

			}
			setEditingValue(value);
		}
	}

	public void finish () {
		if (active) {
			setEditingValue(value);
			listener.editingFinished(buildAction());
			active = false;
		}
	}

	public void cancel () {
		active = false;
	}

	private EditorAction buildAction () {
		switch (type) {
		case X:
			return new EditorAction(obj, ActionType.POS, value, sup.getY(obj));
		case Y:
			return new EditorAction(obj, ActionType.POS, sup.getX(obj), value);
		case WIDTH:
			return new EditorAction(obj, ActionType.SIZE, value, sup.getHeight(obj));
		case HEIGHT:
			return new EditorAction(obj, ActionType.SIZE, sup.getWidth(obj), value);
		case ROTATION:
			return new EditorAction(obj, ActionType.ROTATION, value, 0);
		default:
			return null;

		}
	}

	public boolean isActive () {
		return active;
	}

	public String getEditingValueText () {
		return valueString;
	}

	public int getEditingValue () {
		switch (type) {
		case X:
			return (int)sup.getX(obj);
		case Y:
			return (int)sup.getY(obj);
		case WIDTH:
			return (int)sup.getWidth(obj);
		case HEIGHT:
			return (int)sup.getHeight(obj);
		case ROTATION:
			return (int)sup.getRotation(obj);
		default:
			return 0;
		}
	}

	private void setEditingValue (int newValue) {
		switch (type) {
		case X:
			sup.setX(obj, newValue);
			break;
		case Y:
			sup.setY(obj, newValue);
			break;
		case WIDTH:
			if (lockRatio)
				sup.setSize(obj, newValue, newValue / scaleRatio);
			else
				sup.setSize(obj, newValue, sup.getHeight(obj));
			break;
		case HEIGHT:
			if (lockRatio)
				sup.setSize(obj, newValue / scaleRatio, newValue);
			else
				sup.setSize(obj, sup.getWidth(obj), newValue);
			break;
		case ROTATION:
			sup.setRotation(obj, newValue);
			break;
		default:
			break;
		}
	}

	public String getEditTypeText () {
		if (lockRatio)
			return type.toString().toLowerCase() + " (ratio locked)";
		else
			return type.toString().toLowerCase();
	}
}

enum EditType {
	X, Y, ROTATION, WIDTH, HEIGHT
}

interface KeyboardInputActionFinished {
	public void editingFinished (EditorAction action);
}
