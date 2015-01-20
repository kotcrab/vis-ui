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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.Object2d;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class ObjectProperties extends VisTable {
	private static final int FIELD_WIDTH = 70;
	private VisValidableTextField xField;
	private VisValidableTextField yField;
	private VisValidableTextField xScaleField;
	private VisValidableTextField yScaleField;
	private VisValidableTextField xOriginField;
	private VisValidableTextField yOriginField;
	private VisValidableTextField rotationField;

	private Array<Object2d> objects;

	public ObjectProperties () {
		super(true);
		setBackground(VisUI.skin.getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		NumberValidator numValidator = new NumberValidator();

		VisTable propertiesTable = new VisTable(true);
		propertiesTable.top();
		propertiesTable.columnDefaults(0).padRight(20).left();

		propertiesTable.add(new VisLabel("Position"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Scale"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xScaleField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yScaleField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Origin"));
		propertiesTable.add(new VisLabel("X"));
		propertiesTable.add(xOriginField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.add(new VisLabel("Y"));
		propertiesTable.add(yOriginField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);
		propertiesTable.row();

		propertiesTable.add(new VisLabel("Rotation"));
		propertiesTable.add(new VisLabel(" "));
		propertiesTable.add(rotationField = new VisValidableTextField(numValidator, true)).width(FIELD_WIDTH);

		top();
		add(new VisLabel("Actor Properties"));
		row();
		add(propertiesTable).fill().expand().padRight(0);

		executeForFields(new FieldExecutor() {
			@Override
			public void execute (VisValidableTextField field) {
				field.setProgrammaticChangeEvents(false);
			}
		});
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
			return 200;
		else
			return 0;
	}

	private void addListeners () {
		final ChangeListener fieldChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setValuesToSprite();
			}
		};
		executeForFields(new FieldExecutor() {
			@Override
			public void execute (VisValidableTextField field) {
				field.addListener(fieldChangeListener);
			}
		});

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

			xField.setText(floatToString(obj.sprite.getX()));
			yField.setText(floatToString(obj.sprite.getY()));
			xScaleField.setText(floatToString(obj.sprite.getScaleX()));
			yScaleField.setText(floatToString(obj.sprite.getScaleY()));
			xOriginField.setText(floatToString(obj.sprite.getOriginX()));
			yOriginField.setText(floatToString(obj.sprite.getOriginY()));
			rotationField.setText(floatToString(obj.sprite.getRotation()));
		} else {
			setVisible(true);

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
		}
	}

	private String getObjectsFieldValue (ObjectValue objValue) {
		float value = objValue.getValue(objects.first());

		for (Object2d object : objects)
			if (value != objValue.getValue(object)) return "?";

		return floatToString(value);
	}

	private void setValuesToSprite () {
		System.out.println(objects.size);
		for (Object2d object : objects) {
			Sprite sprite = object.sprite;

			sprite.setPosition(getValueFromField(xField, sprite.getX()), getValueFromField(yField, sprite.getY()));
			sprite.setScale(getValueFromField(xScaleField, sprite.getScaleX()), getValueFromField(yScaleField, sprite.getScaleY()));
			sprite.setOrigin(getValueFromField(xOriginField, sprite.getOriginX()), getValueFromField(yOriginField, sprite.getOriginY()));
			sprite.setRotation(getValueFromField(rotationField, sprite.getRotation()));
		}
	}

	private float getValueFromField (VisTextField field, float valueIfError) {
		try {
			return Float.parseFloat(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}

	private void executeForFields (FieldExecutor executor) {
		executor.execute(xField);
		executor.execute(yField);
		executor.execute(xScaleField);
		executor.execute(yScaleField);
		executor.execute(xOriginField);
		executor.execute(yOriginField);
		executor.execute(rotationField);
	}

	private interface ObjectValue {
		public float getValue (Object2d object);
	}

	private interface FieldExecutor {
		public void execute (VisValidableTextField field);
	}

	private class NumberValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			if (input.endsWith("d") || input.endsWith("f")) return false;
			if (input.equals("?")) return true;
			if (input.equals("")) return true;

			try {
				Float.parseFloat(input);
				return true;
			} catch (NumberFormatException ex) {
			}

			return false;
		}
	}
}
