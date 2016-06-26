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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.ui.IndeterminateTextField;
import com.kotcrab.vis.editor.ui.TintImage;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.editor.util.value.FloatProxyValue;
import com.kotcrab.vis.editor.util.vis.EntityUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.value.VisValue;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import java.util.EnumSet;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties.*;

/** @author Kotcrab */
public class BasicEntityPropertiesTable extends VisTable {
	private final EntityProperties properties;
	private final ColorPicker picker;

	private ColorPickerListener pickerListener;
	private TintImage tint;

	private VisTable idTable;

	private VisTable positionTable;
	private VisTable scaleTable;
	private VisTable originTable;
	private VisTable rotationTable;
	private VisTable tintTable;
	private VisTable flipTable;

	private IndeterminateTextField idField;
	private NumberInputField xField;
	private NumberInputField yField;
	private NumberInputField xScaleField;
	private NumberInputField yScaleField;
	private NumberInputField xOriginField;
	private NumberInputField yOriginField;
	private NumberInputField rotationField;
	private IndeterminateCheckbox xFlipCheck;
	private IndeterminateCheckbox yFlipCheck;

	private VisImage positionLock = createLockImage();
	private VisImage originLock = createLockImage();
	private VisImage scaleLock = createLockImage();
	private VisImage rotationLock = createLockImage();

	private EnumSet<LockableField> lockedFields = EnumSet.noneOf(LockableField.class);

	public BasicEntityPropertiesTable (EntityProperties properties, ColorPicker picker) {
		this.properties = properties;
		this.picker = picker;

		createIdTable();
		createPositionTable();
		createScaleTable();
		createOriginTable();
		createRotationTintTable();
		createFlipTable();

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (EntityProxy entity : properties.getSelectedEntities())
					entity.setColor(newColor);

				properties.getParentTab().dirty();
				tint.setColor(newColor);
				tint.setUnknown(false);
				picker.setListener(null);
				properties.endSnapshot();
			}
		};
	}

	private VisImage createLockImage () {
		VisImage image = new VisImage(Icons.LOCKED.drawable());
		new Tooltip.Builder("This property is locked by other component setting").target(image).build();
		image.setVisible(false);
		return image;
	}

	private void createIdTable () {
		idField = new IndeterminateTextField();
		idTable = new VisTable(true);
		idTable.add(new VisLabel("ID"));
		idTable.add(idField.getTextField()).expandX().fillX();
		properties.setupStdPropertiesTextField(idField.getTextField());
	}

	private void createPositionTable () {
		positionTable = new VisTable(true);
		positionTable.add(new VisLabel("Position")).width(LABEL_WIDTH);
		positionTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		positionTable.add(xField = properties.createNewNumberField()).width(FIELD_WIDTH);
		positionTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		positionTable.add(yField = properties.createNewNumberField()).width(FIELD_WIDTH).spaceRight(0);
		positionTable.add(positionLock);
	}

	private void createScaleTable () {
		scaleTable = new VisTable(true);
		scaleTable.add(new VisLabel("Scale")).width(LABEL_WIDTH);
		scaleTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(xScaleField = properties.createNewNumberField()).width(FIELD_WIDTH);
		scaleTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(yScaleField = properties.createNewNumberField()).width(FIELD_WIDTH).spaceRight(0);
		scaleTable.add(scaleLock);
	}

	private void createOriginTable () {
		originTable = new VisTable(true);
		originTable.add(new VisLabel("Origin")).width(LABEL_WIDTH);
		originTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		originTable.add(xOriginField = properties.createNewNumberField()).width(FIELD_WIDTH);
		originTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		originTable.add(yOriginField = properties.createNewNumberField()).width(FIELD_WIDTH).spaceRight(0);
		originTable.add(originLock);
	}

	private void createRotationTintTable () {
		tint = new TintImage();
		tint.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				properties.beginSnapshot();
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
		rotationTable.add(rotationField = properties.createNewNumberField()).width(FIELD_WIDTH).spaceRight(0);
		rotationTable.add(rotationLock);
	}

	private void createFlipTable () {
		flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new IndeterminateCheckbox("X"));
		flipTable.add(yFlipCheck = new IndeterminateCheckbox("Y"));

		xFlipCheck.addListener(properties.getSharedCheckBoxChangeListener());
		yFlipCheck.addListener(properties.getSharedCheckBoxChangeListener());
	}

	public void lockField (LockableField field) {
		if (lockedFields.contains(field) == false) {
			field.lockFields(this);
		}
		lockedFields.add(field);
	}

	public void unlockField (LockableField field) {
		if (lockedFields.contains(field)) {
			field.unlockFields(this);
		}
		lockedFields.remove(field);
	}

	public void unlockAllFields () {
		lockedFields.forEach(field -> field.unlockFields(this));
		lockedFields.clear();
	}

	public void rebuildPropertiesTable () {
		ImmutableArray<EntityProxy> entities = properties.getSelectedEntities();

		VisTable rotationTintTable = new VisTable(true);
		if (EntityUtils.isRotationSupportedForEntities(entities)) {
			rotationTintTable.add(rotationTable);
		}
		rotationTintTable.add().expand().fill();
		if (EntityUtils.isTintSupportedForEntities(entities)) {
			rotationTintTable.add(tintTable);
		}

		reset();
		TableUtils.setSpacingDefaults(this);
		defaults().padRight(0).fillX();
		add(idTable).row();
		add(positionTable).row();

		if (EntityUtils.isScaleSupportedForEntities(entities)) {
			add(scaleTable).row();
		}

		if (EntityUtils.isOriginSupportedForEntities(entities)) {
			add(originTable).row();
		}

		if (EntityUtils.isRotationSupportedForEntities(entities) || EntityUtils.isTintSupportedForEntities(entities)) {
			add(rotationTintTable).maxWidth(new VisValue(context -> positionTable.getPrefWidth())).row();
		}

		if (EntityUtils.isFlipSupportedForEntities(entities)) {
			add(flipTable).right().fill(false).spaceBottom(2).row();
		}
	}

	public void setValuesToEntity () {
		ImmutableArray<EntityProxy> entities = properties.getSelectedEntities();

		for (int i = 0; i < entities.size(); i++) {
			EntityProxy entity = entities.get(i);

			if (properties.isGroupSelected() == false && idField.isIndeterminate() == false)
				entity.setId(idField.getText().equals("") ? null : idField.getText());

			entity.setPosition(FieldUtils.getFloat(xField, entity.getX()), FieldUtils.getFloat(yField, entity.getY()));

			if (EntityUtils.isScaleSupportedForEntities(entities))
				entity.setScale(FieldUtils.getFloat(xScaleField, entity.getScaleX()), FieldUtils.getFloat(yScaleField, entity.getScaleY()));

			if (EntityUtils.isOriginSupportedForEntities(entities))
				entity.setOrigin(FieldUtils.getFloat(xOriginField, entity.getOriginX()), FieldUtils.getFloat(yOriginField, entity.getOriginY()));

			if (EntityUtils.isRotationSupportedForEntities(entities))
				entity.setRotation(FieldUtils.getFloat(rotationField, entity.getRotation()));

			if (EntityUtils.isFlipSupportedForEntities(entities)) {
				if (xFlipCheck.isIndeterminate() == false)
					entity.setFlip(xFlipCheck.isChecked(), entity.isFlipY());

				if (yFlipCheck.isIndeterminate() == false)
					entity.setFlip(entity.isFlipX(), yFlipCheck.isChecked());
			}
		}
	}

	public void updateUIValues (boolean updateInvalidFields) {
		ImmutableArray<EntityProxy> entities = properties.getSelectedEntities();

		if (properties.isGroupSelected()) {
			idField.setText("<id cannot be set for group>");
			idField.setDisabled(true);
		} else {
			String id = EntityUtils.getCommonId(entities);
			if (id == null) {
				idField.setIndeterminate(true);
			} else {
				idField.setIndeterminate(false);
				idField.setText(id);
			}
			idField.setDisabled(false);
		}

		xField.setText(getEntitiesFieldFloatValue(EntityProxy::getX));
		yField.setText(getEntitiesFieldFloatValue(EntityProxy::getY));

		if (EntityUtils.isScaleSupportedForEntities(entities)) {
			if (updateInvalidFields || xScaleField.isInputValid())
				xScaleField.setText(getEntitiesFieldFloatValue(EntityProxy::getScaleX));

			if (updateInvalidFields || yScaleField.isInputValid())
				yScaleField.setText(getEntitiesFieldFloatValue(EntityProxy::getScaleY));
		}

		if (EntityUtils.isOriginSupportedForEntities(entities)) {
			if (updateInvalidFields || xOriginField.isInputValid())
				xOriginField.setText(getEntitiesFieldFloatValue(EntityProxy::getOriginX));

			if (updateInvalidFields || yOriginField.isInputValid())
				yOriginField.setText(getEntitiesFieldFloatValue(EntityProxy::getOriginY));
		}

		if (EntityUtils.isRotationSupportedForEntities(entities)) {
			if (updateInvalidFields || rotationField.isInputValid())
				rotationField.setText(getEntitiesFieldFloatValue(EntityProxy::getRotation));
		}

		if (EntityUtils.isTintSupportedForEntities(entities)) {
			setTintUIForEntities();
		}

		if (EntityUtils.isFlipSupportedForEntities(entities)) {
			EntityUtils.setCommonCheckBoxState(entities, xFlipCheck, EntityProxy::isFlipX);
			EntityUtils.setCommonCheckBoxState(entities, yFlipCheck, EntityProxy::isFlipY);
		}
	}

	private String getEntitiesFieldFloatValue (FloatProxyValue floatProxyValue) {
		ImmutableArray<EntityProxy> entities = properties.getSelectedEntities();
		return EntityUtils.getCommonFloatValue(entities, floatProxyValue);
	}

	private void setTintUIForEntities () {
		ImmutableArray<EntityProxy> entities = properties.getSelectedEntities();

		Color firstColor = entities.first().getColor();
		for (EntityProxy entity : entities) {
			if (!firstColor.equals(entity.getColor())) {
				tint.setUnknown(true);
				return;
			}
		}

		tint.setUnknown(false);
		tint.setColor(firstColor);
	}

	public enum LockableField {
		POSITION {
			@Override
			protected void lockFields (BasicEntityPropertiesTable table) {
				table.xField.setDisabled(true);
				table.yField.setDisabled(true);
				table.positionLock.setVisible(true);
			}

			@Override
			protected void unlockFields (BasicEntityPropertiesTable table) {
				table.xField.setDisabled(false);
				table.yField.setDisabled(false);
				table.positionLock.setVisible(false);
			}
		}, SCALE {
			@Override
			protected void lockFields (BasicEntityPropertiesTable table) {
				table.xScaleField.setDisabled(true);
				table.yScaleField.setDisabled(true);
				table.scaleLock.setVisible(true);
			}

			@Override
			protected void unlockFields (BasicEntityPropertiesTable table) {
				table.xScaleField.setDisabled(false);
				table.yScaleField.setDisabled(false);
				table.scaleLock.setVisible(false);
			}
		}, ORIGIN {
			@Override
			protected void lockFields (BasicEntityPropertiesTable table) {
				table.xOriginField.setDisabled(true);
				table.yOriginField.setDisabled(true);
				table.originLock.setVisible(true);
			}

			@Override
			protected void unlockFields (BasicEntityPropertiesTable table) {
				table.xOriginField.setDisabled(false);
				table.yOriginField.setDisabled(false);
				table.originLock.setVisible(false);
			}
		}, ROTATION {
			@Override
			protected void lockFields (BasicEntityPropertiesTable table) {
				table.rotationField.setDisabled(true);
				table.rotationLock.setVisible(true);
			}

			@Override
			protected void unlockFields (BasicEntityPropertiesTable table) {
				table.rotationField.setDisabled(false);
				table.rotationLock.setVisible(false);
			}
		};

		protected abstract void lockFields (BasicEntityPropertiesTable table);

		protected abstract void unlockFields (BasicEntityPropertiesTable table);
	}
}
