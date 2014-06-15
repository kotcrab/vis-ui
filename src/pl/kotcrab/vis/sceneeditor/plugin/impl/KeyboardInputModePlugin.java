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

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.EditorAction;
import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IKeybordInputMode;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IUndo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

public class KeyboardInputModePlugin extends PluginState implements IKeybordInputMode {
	private IUndo undoInterface;

	private Array<ObjectRepresentation> selectedObjs;

	private Array<EditorAction> editorActions;

	enum EditType {
		X, Y, ROTATION, WIDTH, HEIGHT
	}

	private EditType type;

	private boolean active = false;

	private int value;
	private String valueString;

	private boolean lockRatio;

	public KeyboardInputModePlugin (IObjectManager objectInterface, IUndo undoInterface) {
		this.undoInterface = undoInterface;

		selectedObjs = objectInterface.getSelectedObjs();
	}

	@Override
	public void disable()
	{
		cancel();
	}
	
	private void beginEdit (EditType type) {
		this.type = type;
		active = true;

		lockRatio = false;

		setStartingValuesForObjects();

		if (checkIfAllObjectHaveSameValue())
			value = getEditingValue(selectedObjs.first());
		else
			value = 0;

		if (value == 0)
			valueString = "";
		else
			valueString = String.valueOf(value);

		editorActions = buildActions();
	}

	private boolean checkIfAllObjectHaveSameValue () {
		int value = getEditingValue(selectedObjs.first());

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != getEditingValue(orep)) return false;
		}

		return true;
	}

	private void setStartingValuesForObjects () {
		for (ObjectRepresentation orep : selectedObjs)
			orep.setStartingValue(getEditingValue(orep));

	}

	@Override
	public boolean keyDown (int keycode) {
		checkTrigger(keycode);

		if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_CANCEL) {
			cancel();
			return true;
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

			return true;
		}

		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		if (state.editing) {
			finish();
		}
		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (state.editing) {
			finish();
		}
		return false;
	}

	private void checkTrigger (int keycode) {
		if (isActive() == false) {

			if (selectedObjs.size > 0) {
				if ((keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSX || keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSY)
					&& doesAllSelectedObjectSupportsMoving()) {
					if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSX) beginEdit(EditType.X);
					if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_POSY) beginEdit(EditType.Y);
				}

				if ((keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_WIDTH || keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_HEIGHT)
					&& doesAllSelectedObjectSupportsScalling()) {
					if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_WIDTH) beginEdit(EditType.WIDTH);
					if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_HEIGHT) beginEdit(EditType.HEIGHT);
				}

				if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_ROTATION && doesAllSelectedObjectSupportsRotating()) {
					if (keycode == SceneEditorConfig.KEY_INPUT_MODE_EDIT_ROTATION) beginEdit(EditType.ROTATION);
				}
			}
		}

	}

	private boolean doesAllSelectedObjectSupportsMoving () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isMovingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support moving.");
				return false;
			}
		}
		return true;
	}

	private boolean doesAllSelectedObjectSupportsScalling () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isScallingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support scalling.");
				return false;
			}
		}
		return true;
	}

	private boolean doesAllSelectedObjectSupportsRotating () {
		for (ObjectRepresentation orep : selectedObjs) {
			if (orep.isRotatingSupported() == false) {
				Gdx.app.log(TAG, "Some of the selected object does not support rotating.");
				return false;
			}
		}
		return true;
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
			undoInterface.addToUndoList(editorActions);
			active = false;
		}
	}

	public void cancel () {
		active = false;
	}

	private Array<EditorAction> buildActions () {
		Array<EditorAction> actions = new Array<EditorAction>();
		for (ObjectRepresentation orep : selectedObjs)
			actions.add(new EditorAction(orep));

		return actions;
	}

	@Override
	public boolean isActive () {
		return active;
	}

	private void setEditingValue (int newValue) {
		for (ObjectRepresentation orep : selectedObjs) {
			switch (type) {
			case X:
				orep.setX(newValue);
				break;
			case Y:
				orep.setY(newValue);
				break;
			case WIDTH:
				if (lockRatio)
					orep.setSize(newValue, newValue / orep.getScaleRatio());
				else
					orep.setSize(newValue, orep.getHeight());
				break;
			case HEIGHT:
				if (lockRatio)
					orep.setSize(newValue / orep.getScaleRatio(), newValue);
				else
					orep.setSize(orep.getWidth(), newValue);
				break;
			case ROTATION:
				orep.setRotation(newValue);
				break;
			default:
				break;
			}
		}
	}

	public int getEditingValue (ObjectRepresentation orep) {
		switch (type) {
		case X:
			return (int)orep.getX();
		case Y:
			return (int)orep.getY();
		case WIDTH:
			return (int)orep.getWidth();
		case HEIGHT:
			return (int)orep.getHeight();
		case ROTATION:
			return (int)orep.getRotation();
		default:
			return 0;
		}
	}

	@Override
	public String getEditingValueText () {
		return valueString;
	}

	@Override
	public String getEditTypeText () {
		if (lockRatio)
			return type.toString().toLowerCase() + " (ratio locked)";
		else
			return type.toString().toLowerCase();
	}

}
