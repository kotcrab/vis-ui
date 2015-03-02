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

import com.badlogic.gdx.files.FileHandle;
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
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.SelectFontDialog;
import com.kotcrab.vis.editor.ui.SelectFontDialog.FontDialogListener;
import com.kotcrab.vis.editor.ui.tab.Tab;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;

import java.util.ArrayList;

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
	private ColorImage tint;

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

		specificTables.add(new TextObjectTable());

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
		if (isRotationSupportedForEntities()) rotationTintTable.add(rotationTable);
		rotationTintTable.add().expand().fill();
		if (isTintSupportedForEntities()) rotationTintTable.add(tintTable);

		propertiesTable.defaults().padRight(6).fillX();
		propertiesTable.add(idTable).row();
		propertiesTable.add(positionTable).row();
		if (isScaleSupportedForEntities()) propertiesTable.add(scaleTable).row();
		if (isOriginSupportedForEntities()) propertiesTable.add(originTable).row();
		propertiesTable.add(rotationTintTable).row();
		if (isFlipSupportedForEntities()) propertiesTable.add(flipTable).right().fill(false).row();

		activeSpecificTable = null;
		for (SpecificObjectTable table : specificTables) {
			Class clazz = table.getObjectClass();

			if (checkEntityList(clazz)) {
				activeSpecificTable = table;
				propertiesTable.addSeparator();
				propertiesTable.add(table).row();
				break;
			}
		}

		invalidateHierarchy();
	}

	private boolean checkEntityList (Class clazz) {
		for (EditorEntity entity : entities)
			if (entity.getClass() != clazz) return false;

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

	private boolean isScaleSupportedForEntities () {
		for (EditorEntity entity : entities) {
			if (entity.isScaleSupported() == false) return false;
		}

		return true;
	}

	private boolean isOriginSupportedForEntities () {
		for (EditorEntity entity : entities) {
			if (entity.isOriginSupported() == false) return false;
		}

		return true;
	}

	private boolean isRotationSupportedForEntities () {
		for (EditorEntity entity : entities) {
			if (entity.isRotationSupported() == false) return false;
		}

		return true;
	}

	private boolean isTintSupportedForEntities () {
		for (EditorEntity entity : entities) {
			if (entity.isTintSupported() == false) return false;
		}

		return true;
	}

	private boolean isFlipSupportedForEntities () {
		for (EditorEntity entity : entities) {
			if (entity.isFlipSupported() == false) return false;
		}

		return true;
	}

	private String getEntitiesId () {
		String firstId = entities.first().getId();
		if (firstId == null) firstId = "";

		for (EditorEntity entity : entities) {
			String entityId = entity.getId();
			if (entityId == null) entityId = "";

			if (firstId.equals(entityId) == false) {
				return "<?>";
			}
		}

		return firstId;
	}

	private void setFlipXUICheckForEntities () {
		boolean xFlip = entities.first().isFlipX();
		for (EditorEntity entity : entities) {
			if (xFlip != entity.isFlipX()) {
				tint.setUnknown(false);
				return;
			}
		}
		xFlipCheck.setChecked(xFlip);
	}

	private void setFlipYUICheckForEntities () {
		boolean yFlip = entities.first().isFlipY();
		for (EditorEntity entity : entities) {
			if (yFlip != entity.isFlipY()) {
				tint.setUnknown(false);
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

	private String getEntitiesFieldValue (EntityValue objValue) {
		float value = objValue.getValue(entities.first());

		for (EditorEntity entity : entities)
			if (value != objValue.getValue(entity)) return "?";

		return EntityPropertiesUtils.floatToString(value);
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

		if (activeSpecificTable != null) activeSpecificTable.setValuesToEntities(entities);
	}

	public void updateValues () {
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

			if (activeSpecificTable != null) activeSpecificTable.updateUIValues(entities);

			setTintUIForEntities();
			setFlipXUICheckForEntities();
			setFlipYUICheckForEntities();
		}
	}

	private interface EntityValue {
		public float getValue (EditorEntity entity);
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

	private static abstract class SpecificObjectTable extends VisTable {
		public SpecificObjectTable (boolean useVisDefaults) {
			super(useVisDefaults);
		}

		public abstract Class<? extends EditorEntity> getObjectClass ();

		public abstract void updateUIValues (Array<EditorEntity> entities);

		public abstract void setValuesToEntities (Array<EditorEntity> entities);
	}

	private class TextObjectTable extends SpecificObjectTable {
		private SelectFontDialog selectFontDialog;

		private VisValidableTextField textField;
		private VisLabel fontLabel;
		private VisImageButton selectFontButton;
		private NumberInputField sizeInputField;

		public TextObjectTable () {
			super(true);

			fontLabel = new VisLabel();
			fontLabel.setColor(Color.GRAY);
			fontLabel.setEllipsis(true);
			selectFontButton = new VisImageButton(Assets.getIcon(Icons.MORE));
			sizeInputField = new NumberInputField(sharedChangeListener);
			sizeInputField.addValidator(Validators.INTEGERS);
			sizeInputField.addValidator(new GreaterThanValidator(FontCacheModule.MIN_FONT_SIZE));
			sizeInputField.addValidator(new LesserThanValidator(FontCacheModule.MAX_FONT_SIZE));
			textField = new VisValidableTextField();
			textField.addListener(sharedChangeListener);
			textField.setProgrammaticChangeEvents(false);

			VisTable fontTable = new VisTable(true);
			fontTable.add(new VisLabel("Font"));
			fontTable.add(fontLabel).width(100);
			fontTable.add(selectFontButton);
			fontTable.add(new VisLabel("Size"));
			fontTable.add(sizeInputField).width(40);
			fontTable.add().expand().fill();

			VisTable textTable = new VisTable(true);
			textTable.add(new VisLabel("Text"));
			textTable.add(textField).expandX().fillX();

			defaults().left().expandX().fillX();
			add(textTable);
			row();
			add(fontTable);

			selectFontButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					selectFontDialog.rebuild();
					getStage().addActor(selectFontDialog.fadeIn());
				}
			});

			selectFontDialog = new SelectFontDialog(fileAccessModule, new FontDialogListener() {
				@Override
				public void selected (FileHandle file) {
					for (EditorEntity entity : entities) {
						TextObject obj = (TextObject) entity;
						obj.setFont(fontCacheModule.get(file));
					}

					parentTab.dirty();
					updateValues();
				}
			});
		}

		private String getTextFieldText (Array<EditorEntity> entities) {
			TextObject textObj = (TextObject) entities.get(0);
			String firstText = textObj.getText();

			for (EditorEntity entity : entities) {
				TextObject obj = (TextObject) entity;

				if (obj.getText().equals(firstText) == false) return "<multiple values>";
			}

			return firstText;
		}

		private String getFontLabelText (Array<EditorEntity> entities) {
			String firstText = getFontTextForEntity(entities.get(0));

			for (EditorEntity entity : entities) {
				TextObject obj = (TextObject) entity;

				if (getFontTextForEntity(obj).equals(firstText) == false) return "<?>";
			}

			return firstText;
		}

		private String getFontTextForEntity (EditorEntity entity) {
			TextObject obj = (TextObject) entity;
			return obj.getRelativeFontPath().substring(fileAccessModule.getTTFFontFolderRelative().length() + 1);
		}

		@Override
		public Class<? extends EditorEntity> getObjectClass () {
			return TextObject.class;
		}

		@Override
		public void updateUIValues (Array<EditorEntity> entities) {
			textField.setText(getTextFieldText(entities));
			fontLabel.setText(getFontLabelText(entities));
			sizeInputField.setText(getEntitiesFieldValue(new EntityValue() {
				@Override
				public float getValue (EditorEntity entity) {
					return ((TextObject) entity).getFontSize();
				}
			}));
		}

		@Override
		public void setValuesToEntities (Array<EditorEntity> entities) {
			for (EditorEntity entity : entities) {
				TextObject obj = (TextObject) entity;

				obj.setText(textField.getText());
				obj.setFontSize(FieldUtils.getInt(sizeInputField, obj.getFontSize()));
			}
		}
	}

}
