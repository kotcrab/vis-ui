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
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.scene.EditorEntity;
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

public class EntityProperties extends VisTable {
	private static final int LABEL_WIDTH = 60;
	private static final int AXIS_LABEL_WIDTH = 10;
	private static final int FIELD_WIDTH = 70;

	private Tab parentTab;
	private ColorPicker picker;

	private Array<EditorEntity> entities;

	private ChangeListener sharedChangeListener;
	private FieldFilter sharedFieldFilter;
	private FieldValidator sharedFieldValidator;

	private ColorPickerListener pickerListener;
	private ColorImage tint;

	//UI
	private VisTable propertiesTable;

	private VisTable idTable;

	private VisTable positionTable;
	private VisTable scaleTable;
	private VisTable originTable;

	private VisTable rotationTintTable;
	private VisTable rotationTable;
	private VisTable tintTable;

	private VisTable flipTable;

	private VisValidableTextField idField;
	private InputField xField;
	private InputField yField;
	private InputField xScaleField;
	private InputField yScaleField;
	private InputField xOriginField;
	private InputField yOriginField;
	private InputField rotationField;
	private VisCheckBox xFlipCheck;
	private VisCheckBox yFlipCheck;

	public EntityProperties (final ColorPicker picker, final Tab parentTab) {
		super(true);
		this.picker = picker;
		this.parentTab = parentTab;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		sharedFieldFilter = new FieldFilter();
		sharedFieldValidator = new FieldValidator();

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setValuesToSprite();
				parentTab.setDirty(true);
			}
		};

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (EditorEntity entity : entities)
					entity.setColor(newColor);

				parentTab.setDirty(true);
				tint.setColor(newColor);
				picker.setListener(null);
			}
		};

		createIdTable();
		createPositionTable();
		createScaleTable();
		createOriginTable();
		createRotationTintTable();
		createFlipTable();

		createPropertiesTable();

		top();
		add(new VisLabel("Entity Properties")).row();
		add(propertiesTable).fill().expand().padRight(0);

		addListeners();

		pack();
	}

	private void createIdTable () {
		idTable = new VisTable(true);
		idTable.add(new VisLabel("ID"));
		idTable.add(idField = new VisValidableTextField()).expandX().fillX();
		idField.setProgrammaticChangeEvents(false);
		idField.addListener(sharedChangeListener);
	}

	private void createPositionTable () {
		positionTable = new VisTable(true);
		positionTable.add(new VisLabel("Position")).width(LABEL_WIDTH);
		positionTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		positionTable.add(xField = new InputField()).width(FIELD_WIDTH);
		positionTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		positionTable.add(yField = new InputField()).width(FIELD_WIDTH);
	}

	private void createScaleTable () {
		scaleTable = new VisTable(true);
		scaleTable.add(new VisLabel("Scale")).width(LABEL_WIDTH);
		scaleTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(xScaleField = new InputField()).width(FIELD_WIDTH);
		scaleTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(yScaleField = new InputField()).width(FIELD_WIDTH);
	}

	private void createOriginTable () {
		originTable = new VisTable(true);
		originTable.add(new VisLabel("Origin")).width(LABEL_WIDTH);
		originTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		originTable.add(xOriginField = new InputField()).width(FIELD_WIDTH);
		originTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		originTable.add(yOriginField = new InputField()).width(FIELD_WIDTH);
	}

	private void createRotationTintTable () {
		tint = new ColorImage();
		tint.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				picker.setColor(tint.getColor());
				picker.setListener(pickerListener);
				getStage().addActor(picker.fadeIn());
			}
		});

		tintTable = new VisTable(true);
		tintTable.add(new VisLabel("Tint"));
		tintTable.add(tint).size(20);

		rotationTable = new VisTable(true);
		rotationTable.add(new VisLabel("Rotation")).width(LABEL_WIDTH);
		rotationTable.add(new VisLabel(" ")).width(AXIS_LABEL_WIDTH);
		rotationTable.add(rotationField = new InputField()).width(FIELD_WIDTH);

		rotationTintTable = new VisTable(true);
		rotationTintTable.add(rotationTable);
		rotationTintTable.add().expand().fill();
		rotationTintTable.add(tintTable);
	}

	private void createFlipTable () {
		flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new VisCheckBox("X"));
		flipTable.add(yFlipCheck = new VisCheckBox("Y"));

		xFlipCheck.addListener(sharedChangeListener);
		yFlipCheck.addListener(sharedChangeListener);
	}

	private void createPropertiesTable () {
		propertiesTable = new VisTable(true);
		propertiesTable.defaults().padRight(6);
		propertiesTable.add(idTable).fillX().row();
		propertiesTable.add(positionTable).row();
		propertiesTable.add(scaleTable).row();
		propertiesTable.add(originTable).row();
		propertiesTable.add(rotationTintTable).fillX().row();
		propertiesTable.add(flipTable).right();
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
			return super.getPrefHeight() + 5;
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

	public void setValuesToFields (Array<EditorEntity> entities) {
		this.entities = entities;

		if (entities.size == 0)
			setVisible(false);
		else {
			setVisible(true);

			idField.setText(getEntitiesId());
			xField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getX();
				}
			}));
			yField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getY();
				}
			}));

			xScaleField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getScaleX();
				}
			}));
			yScaleField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getScaleY();
				}
			}));

			xOriginField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getOriginX();
				}
			}));
			yOriginField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getOriginY();
				}
			}));

			rotationField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return entity.getRotation();
				}
			}));

			setTintForEntities();
			setFlipXCheckForEntities();
			setFlipYCheckForEntities();
		}
	}

	private String getEntitiesId () {
		String firstId = entities.first().id;
		if (firstId == null) firstId = "";

		for (EditorEntity entity : entities) {
			String entityId = entity.id;
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	private void setFlipXCheckForEntities () {
		boolean xFlip = entities.first().isFlipX();
		for (EditorEntity entity : entities) {
			if (xFlip != entity.isFlipX()) {
				tint.setUnknown(false);
				return;
			}
		}
		xFlipCheck.setChecked(xFlip);
	}

	private void setFlipYCheckForEntities () {
		boolean yFlip = entities.first().isFlipY();
		for (EditorEntity entity : entities) {
			if (yFlip != entity.isFlipY()) {
				tint.setUnknown(false);
				return;
			}
		}
		yFlipCheck.setChecked(yFlip);
	}

	private void setTintForEntities () {
		Color firstColor = entities.first().getColor();
		for (EditorEntity entity : entities) {
			if (firstColor.equals(entity.getColor()) == false) {
				tint.setUnknown(true);
				return;
			}
		}
		tint.setColor(firstColor);
	}

	private String getEntitiesFieldValue (EntityValue objValue) {
		float value = objValue.getValue(entities.first());

		for (EditorEntity entity : entities)
			if (value != objValue.getValue(entity)) return "?";

		return floatToString(value);
	}

	private void setValuesToSprite () {
		for (EditorEntity entity : entities) {

			entity.id = idField.getText().equals("") ? null : idField.getText();
			entity.setPosition(FieldUtils.getFloat(xField, entity.getX()), FieldUtils.getFloat(yField, entity.getY()));
			entity.setScale(FieldUtils.getFloat(xScaleField, entity.getScaleX()), FieldUtils.getFloat(yScaleField, entity.getScaleY()));
			entity.setOrigin(FieldUtils.getFloat(xOriginField, entity.getOriginX()), FieldUtils.getFloat(yOriginField, entity.getOriginY()));
			entity.setRotation(FieldUtils.getFloat(rotationField, entity.getRotation()));
			entity.setFlip(xFlipCheck.isChecked(), yFlipCheck.isChecked());
		}
	}

	public void updateValues () {
		setValuesToFields(entities);
	}

	private interface EntityValue {
		public float getValue (EditorEntity entity);
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

	private static class ColorImage extends Image {
		private final Drawable alphaBar = Assets.getMisc("alpha-grid-20x20");
		private final Drawable white = VisUI.getSkin().getDrawable("white");
		private final Drawable questionMark = Assets.getIcon(Icons.QUESTION);

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

	private class InputField extends VisValidableTextField {
		public InputField () {
			addValidator(sharedFieldValidator);

			//without disabling it, it would case to set old values from new entities on switch
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

}
