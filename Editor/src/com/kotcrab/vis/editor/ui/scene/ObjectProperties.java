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

package com.kotcrab.vis.editor.ui.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.module.scene.Object2d;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import org.lwjgl.input.Keyboard;

public class ObjectProperties extends VisTable {
	private static final int FIELD_WIDTH = 70;

	private VisValidableTextField idField;
	private VisValidableTextField xField;
	private VisValidableTextField yField;
	private VisValidableTextField xScaleField;
	private VisValidableTextField yScaleField;
	private VisValidableTextField xOriginField;
	private VisValidableTextField yOriginField;
	private VisValidableTextField rotationField;
	private VisCheckBox xFlipCheck;
	private VisCheckBox yFlipCheck;

	private ColorImage tint;

	private Array<Object2d> objects;

	private ChangeListener sharedChangeListener;
	private FieldFilter sharedFieldFilter;
	private FieldValidator sharedFieldValidator;
	private Tab parentTab;

	private ColorPickerListener pickerListener;

	public ObjectProperties (final ColorPicker picker, final Tab parentTab) {
		super(true);
		this.parentTab = parentTab;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setValuesToSprite();
				parentTab.setDirty(true);
			}
		};

		sharedFieldFilter = new FieldFilter();
		sharedFieldValidator = new FieldValidator();

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (Object2d object : objects)
					object.sprite.setColor(newColor);

				parentTab.setDirty(true);
				tint.setColor(newColor);
				picker.setListener(null);
			}
		};

		VisTable propertiesTable = new VisTable(true);
		propertiesTable.top();
		propertiesTable.columnDefaults(0).left();

		VisTable tintTable = new VisTable(true);
		tintTable.add(new VisLabel("Tint"));
		tintTable.add(tint = new ColorImage()).size(20);

		tint.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				picker.setColor(tint.getColor());
				picker.setListener(pickerListener);
				getStage().addActor(picker.fadeIn());
			}
		});


		VisTable idTable = new VisTable(true);

		idTable.add(new VisLabel("ID"));
		idTable.add(idField = new VisValidableTextField()).expandX().fillX();
		idField.setProgrammaticChangeEvents(false);
		idField.addListener(sharedChangeListener);

		propertiesTable.add(idTable).colspan(5).fillX();
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Position"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Scale"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xScaleField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yScaleField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Origin"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xOriginField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yOriginField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Rotation"));
		propertiesTable.add(new VisLabel(" "));
		propertiesTable.add(rotationField = new InputField()).width(FIELD_WIDTH);
		propertiesTable.add(tintTable).colspan(2);
		propertiesTable.row();

		VisTable flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new VisCheckBox("X"));
		flipTable.add(yFlipCheck = new VisCheckBox("Y"));

		xFlipCheck.addListener(sharedChangeListener);
		yFlipCheck.addListener(sharedChangeListener);

		propertiesTable.add(flipTable).colspan(5).right();
		propertiesTable.row();

		top();
		add(new VisLabel("Object Properties"));
		row();
		add(propertiesTable).fill().expand().padRight(0);

		addListeners();
	}

	private static String floatToString (float d) {
		//fk this function
		if (d == (long) d) //if does not have decimal places
			return String.format("%d", (long) d);
		else {
			//round to two decimal places
			d = Math.round(d * 100);
			d = d / 100;
			String s = String.valueOf(d);

			//remove trailing zeros if exists
			return s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
		}
	}

	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		invalidateHierarchy();
	}

	@Override
	public float getPrefHeight () {
		if (isVisible())
			return 210;
		else
			return 0;
	}

	private void addListeners () {
		//stops touchDown and keyDown events from being received by parent
		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
				return true;
			}

			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				event.stop();
				return true;
			}
		});
	}

	public void setValuesToFields (Array<Object2d> objects) {
		this.objects = objects;

		if (objects.size == 0)
			setVisible(false);
		else if (objects.size == 1) {
			setVisible(true);

			Object2d obj = objects.get(0);

			idField.setText(obj.id);

			xField.setText(floatToString(obj.sprite.getX()));
			yField.setText(floatToString(obj.sprite.getY()));
			xScaleField.setText(floatToString(obj.sprite.getScaleX()));
			yScaleField.setText(floatToString(obj.sprite.getScaleY()));
			xOriginField.setText(floatToString(obj.sprite.getOriginX()));
			yOriginField.setText(floatToString(obj.sprite.getOriginY()));
			rotationField.setText(floatToString(obj.sprite.getRotation()));
			tint.setUnknown(false);
			tint.setColor(obj.sprite.getColor());

			xFlipCheck.setChecked(obj.sprite.isFlipX());
			yFlipCheck.setChecked(obj.sprite.isFlipY());
		} else {
			setVisible(true);

			idField.setText(getObjectsId());
			xField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getX();
				}
			}));
			yField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getY();
				}
			}));
			xScaleField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getScaleX();
				}
			}));
			yScaleField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getScaleY();
				}
			}));
			xOriginField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getOriginX();
				}
			}));
			yOriginField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getOriginY();
				}
			}));
			rotationField.setText(getObjectsFieldValue(new ObjectValue() {
				@Override
				public float getValue (Object2d object) {
					return object.sprite.getRotation();
				}
			}));
			setTintForObjects();
			setXCheckForObjects();
			setYCheckForObjects();
		}
	}

	private String getObjectsId () {
		String firstId = objects.first().id;
		for (Object2d object : objects) {
			if (firstId.equals(object.id) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	private void setXCheckForObjects () {
		boolean xFlip = objects.first().sprite.isFlipX();
		for (Object2d object : objects) {
			if (xFlip != object.sprite.isFlipX()) {
				tint.setUnknown(false);
				return;
			}
		}
		xFlipCheck.setChecked(xFlip);
	}

	private void setYCheckForObjects () {
		boolean yFlip = objects.first().sprite.isFlipY();
		for (Object2d object : objects) {
			if (yFlip != object.sprite.isFlipY()) {
				tint.setUnknown(false);
				return;
			}
		}
		yFlipCheck.setChecked(yFlip);
	}

	private void setTintForObjects () {
		Color firstColor = objects.first().sprite.getColor();
		for (Object2d object : objects) {
			if (firstColor.equals(object.sprite.getColor()) == false) {
				tint.setUnknown(true);
				return;
			}
		}
		tint.setColor(firstColor);
	}

	private String getObjectsFieldValue (ObjectValue objValue) {
		float value = objValue.getValue(objects.first());

		for (Object2d object : objects)
			if (value != objValue.getValue(object)) return "?";

		return floatToString(value);
	}

	private void setValuesToSprite () {
		for (Object2d object : objects) {
			Sprite sprite = object.sprite;

			object.id = idField.getText();
			sprite.setPosition(FieldUtils.getFloat(xField, sprite.getX()), FieldUtils.getFloat(yField, sprite.getY()));
			sprite.setScale(FieldUtils.getFloat(xScaleField, sprite.getScaleX()), FieldUtils.getFloat(yScaleField, sprite.getScaleY()));
			sprite.setOrigin(FieldUtils.getFloat(xOriginField, sprite.getOriginX()), FieldUtils.getFloat(yOriginField, sprite.getOriginY()));
			sprite.setRotation(FieldUtils.getFloat(rotationField, sprite.getRotation()));
			sprite.setFlip(xFlipCheck.isChecked(), yFlipCheck.isChecked());
		}
	}

	public void updateValues () {
		setValuesToFields(objects);
	}

	private interface ObjectValue {
		public float getValue (Object2d object);
	}

	private class FieldValidator implements InputValidator {
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

	private class FieldFilter implements TextFieldFilter {
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

	private class InputField extends VisValidableTextField {
		public InputField () {
			addValidator(sharedFieldValidator);

			//without disabling it, it would case to set old values from new object on switch
			setProgrammaticChangeEvents(false);

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

				if (character == '-' && InputField.this.getCursorPosition() > 0 && getText().startsWith("-") == false)
					return keyTypedReturnValue;

				if (character == '.' && getText().contains(".")) return keyTypedReturnValue;

				parentTab.setDirty(true);

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
					InputField.this.setCursorPosition(lastPos);

					setValuesToSprite();
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
	}

	public class ColorImage extends Image {
		private final Drawable alphaBar = Assets.getMisc("alpha-grid-20x20");
		private final Drawable white = VisUI.getSkin().getDrawable("white");
		private final Drawable questionMark = Assets.getIcon("question");

		private boolean unknown;

		public ColorImage () {
			super();
			setDrawable(white);
		}

		@Override
		public void draw (Batch batch, float parentAlpha) {
			batch.setColor(1, 1, 1, parentAlpha);

			if (unknown)
				questionMark.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
			else {
				alphaBar.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
				super.draw(batch, parentAlpha);
			}
		}

		public void setUnknown (boolean unknown) {
			this.unknown = unknown;
		}

		@Override
		public void setColor (Color color) {
			super.setColor(color);
		}
	}

}
