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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import java.util.ArrayList;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.TintImage;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.getEntitiesId;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isFlipSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isOriginSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isRotationSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isScaleSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isTintSupportedForEntities;

public class EntityProperties extends VisTable {
	private static final int LABEL_WIDTH = 60;
	private static final int AXIS_LABEL_WIDTH = 10;
	private static final int FIELD_WIDTH = 70;

	private FileAccessModule fileAccessModule;
	private FontCacheModule fontCacheModule;
	private ColorPicker picker;
	private Tab parentTab;

	private Array<EditorEntity> entities;

	private ChangeListener sharedChangeListener;

	private ColorPickerListener pickerListener;
	private TintImage tint;

	//UI
	private VisTable propertiesTable;

	private VisTable idTable;

	private VisTable positionTable;
	private VisTable scaleTable;
	private VisTable originTable;
	private VisTable rotationTable;
	private VisTable tintTable;
	private VisTable flipTable;

	private ArrayList<SpecificObjectTable> specificTables = new ArrayList<>();
	private SpecificObjectTable activeSpecificTable;

	private VisValidableTextField idField;
	private NumberInputField xField;
	private NumberInputField yField;
	private NumberInputField xScaleField;
	private NumberInputField yScaleField;
	private NumberInputField xOriginField;
	private NumberInputField yOriginField;
	private NumberInputField rotationField;
	private VisCheckBox xFlipCheck;
	private VisCheckBox yFlipCheck;

	public EntityProperties (FileAccessModule fileAccessModule, FontCacheModule fontCacheModule, final ColorPicker picker, final Tab parentTab, Array<EditorEntity> selectedEntitiesList) {
		super(true);
		this.fileAccessModule = fileAccessModule;
		this.fontCacheModule = fontCacheModule;
		this.picker = picker;
		this.parentTab = parentTab;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		entities = selectedEntitiesList;

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				setValuesToEntity();
				parentTab.dirty();
			}
		};

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (EditorEntity entity : entities)
					entity.setColor(newColor);

				parentTab.dirty();
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

		specificTables.add(new TTFTextObjectTable(this));
		specificTables.add(new BMPTextObjectTable(this));

		propertiesTable = new VisTable(true);

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
		positionTable.add(xField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		positionTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		positionTable.add(yField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		positionTable.add().expand().fill();
	}

	private void createScaleTable () {
		scaleTable = new VisTable(true);
		scaleTable.add(new VisLabel("Scale")).width(LABEL_WIDTH);
		scaleTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(xScaleField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		scaleTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(yScaleField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		scaleTable.add().expand().fill();
	}

	private void createOriginTable () {
		originTable = new VisTable(true);
		originTable.add(new VisLabel("Origin")).width(LABEL_WIDTH);
		originTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		originTable.add(xOriginField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		originTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		originTable.add(yOriginField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
		originTable.add().expand().fill();
	}

	private void createRotationTintTable () {
		tint = new TintImage();
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
		tintTable.add(tint).size(20).padRight(10);

		rotationTable = new VisTable(true);
		rotationTable.add(new VisLabel("Rotation")).width(LABEL_WIDTH);
		rotationTable.add(new VisLabel(" ")).width(AXIS_LABEL_WIDTH);
		rotationTable.add(rotationField = new NumberInputField(sharedChangeListener)).width(FIELD_WIDTH);
	}

	private void createFlipTable () {
		flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new VisCheckBox("X"));
		flipTable.add(yFlipCheck = new VisCheckBox("Y"));

		xFlipCheck.addListener(sharedChangeListener);
		yFlipCheck.addListener(sharedChangeListener);
	}

	private void rebuildPropertiesTable () {
		propertiesTable.reset();
		TableUtils.setSpaceDefaults(propertiesTable);

		VisTable rotationTintTable = new VisTable(true);
		if (isRotationSupportedForEntities(entities)) rotationTintTable.add(rotationTable);
		rotationTintTable.add().expand().fill();
		if (isTintSupportedForEntities(entities)) rotationTintTable.add(tintTable);

		propertiesTable.defaults().padRight(6).fillX();
		propertiesTable.add(idTable).row();
		propertiesTable.add(positionTable).row();
		if (isScaleSupportedForEntities(entities)) propertiesTable.add(scaleTable).row();
		if (isOriginSupportedForEntities(entities)) propertiesTable.add(originTable).row();
		propertiesTable.add(rotationTintTable).row();
		if (isFlipSupportedForEntities(entities)) propertiesTable.add(flipTable).right().fill(false).row();

		activeSpecificTable = null;
		for (SpecificObjectTable table : specificTables) {
			if (checkEntityList(table)) {
				activeSpecificTable = table;
				propertiesTable.addSeparator();
				propertiesTable.add(table).row();
				break;
			}
		}

		invalidateHierarchy();
	}

	private boolean checkEntityList (SpecificObjectTable table) {
		for (EditorEntity entity : entities)
			if (table.isSupported(entity) == false) return false;

		return true;
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

	public void selectedEntitiesChanged () {
		rebuildPropertiesTable();
		updateValues();
	}

	Array<EditorEntity> getEntities () {
		return entities;
	}

	Tab getParentTab () {
		return parentTab;
	}

	public ChangeListener getSharedChangeListener () {
		return sharedChangeListener;
	}

	FileAccessModule getFileAccessModule () {
		return fileAccessModule;
	}

	FontCacheModule getFontCacheModule () {
		return fontCacheModule;
	}

	private void setFlipXUICheckForEntities () {
		boolean xFlip = entities.first().isFlipX();
		for (EditorEntity entity : entities) {
			if (xFlip != entity.isFlipX()) {
				xFlipCheck.setChecked(false);
				return;
			}
		}
		xFlipCheck.setChecked(xFlip);
	}

	private void setFlipYUICheckForEntities () {
		boolean yFlip = entities.first().isFlipY();
		for (EditorEntity entity : entities) {
			if (yFlip != entity.isFlipY()) {
				yFlipCheck.setChecked(false);
				return;
			}
		}
		yFlipCheck.setChecked(yFlip);
	}

	private void setTintUIForEntities () {
		Color firstColor = entities.first().getColor();
		for (EditorEntity entity : entities) {
			if (firstColor.equals(entity.getColor()) == false) {
				tint.setUnknown(true);
				return;
			}
		}
		tint.setColor(firstColor);
	}

	private String getEntitiesFieldValue (EntityValue entityValue) {
		return Utils.getEntitiesFieldValue(entities, entityValue);
	}

	private void setValuesToEntity () {
		for (EditorEntity entity : entities) {

			entity.setId(idField.getText().equals("") ? null : idField.getText());
			entity.setPosition(FieldUtils.getFloat(xField, entity.getX()), FieldUtils.getFloat(yField, entity.getY()));
			entity.setScale(FieldUtils.getFloat(xScaleField, entity.getScaleX()), FieldUtils.getFloat(yScaleField, entity.getScaleY()));
			entity.setOrigin(FieldUtils.getFloat(xOriginField, entity.getOriginX()), FieldUtils.getFloat(yOriginField, entity.getOriginY()));
			entity.setRotation(FieldUtils.getFloat(rotationField, entity.getRotation()));
			entity.setFlip(xFlipCheck.isChecked(), yFlipCheck.isChecked());

		}

		if (activeSpecificTable != null) activeSpecificTable.setValuesToEntities();
	}

	public void updateValues () {
		if (entities.size == 0)
			setVisible(false);
		else {
			setVisible(true);

			idField.setText(getEntitiesId(entities));
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

			if (activeSpecificTable != null) activeSpecificTable.updateUIValues();

			setTintUIForEntities();
			setFlipXUICheckForEntities();
			setFlipYUICheckForEntities();
		}
	}
}
