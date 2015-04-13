/*
 * Copyright 2014-2015 See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.util.EntityUtils;
import com.kotcrab.vis.editor.util.FloatValue;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.RedoEvent;
import com.kotcrab.vis.editor.event.UndoEvent;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.UndoableAction;
import com.kotcrab.vis.editor.module.scene.UndoableActionGroup;
import com.kotcrab.vis.editor.scene.*;
import com.kotcrab.vis.editor.util.EventStopper;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.MusicData;
import com.kotcrab.vis.runtime.data.ParticleEffectData;
import com.kotcrab.vis.runtime.data.SoundData;
import com.kotcrab.vis.runtime.data.SpriteData;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Iterator;

public class EntityProperties extends VisTable implements Disposable, EventListener {
	private static final int LABEL_WIDTH = 60;
	private static final int AXIS_LABEL_WIDTH = 10;
	private static final int FIELD_WIDTH = 70;

	private ObjectSupportModule supportModule;

	private FileAccessModule fileAccessModule;
	private FontCacheModule fontCacheModule;
	private UndoModule undoModule;

	private ColorPicker picker;
	private Tab parentTab;

	private Array<EditorObject> entities;

	private ChangeListener sharedChangeListener;
	private ChangeListener sharedCheckBoxChangeListener;
	private FocusListener sharedFocusListener;

	private ColorPickerListener pickerListener;
	private TintImage tint;

	private boolean snapshotInProgress;
	private SnapshotUndoableActionGroup snapshots;

	//UI
	private VisTable propertiesTable;

	private VisTable idTable;

	private VisTable positionTable;
	private VisTable scaleTable;
	private VisTable originTable;
	private VisTable rotationTable;
	private VisTable tintTable;
	private VisTable flipTable;

	private Array<SpecificObjectTable> specificTables = new Array<>();
	private SpecificObjectTable activeSpecificTable;

	private VisValidableTextField idField;
	private NumberInputField xField;
	private NumberInputField yField;
	private NumberInputField xScaleField;
	private NumberInputField yScaleField;
	private NumberInputField xOriginField;
	private NumberInputField yOriginField;
	private NumberInputField rotationField;
	private IndeterminateCheckbox xFlipCheck;
	private IndeterminateCheckbox yFlipCheck;

	public EntityProperties (ObjectSupportModule supportModule, FileAccessModule fileAccessModule, FontCacheModule fontCacheModule,
							 final UndoModule undoModule, final ColorPicker picker, final Tab parentTab, final Array<EditorObject> selectedEntitiesList) {
		super(true);
		this.supportModule = supportModule;
		this.fileAccessModule = fileAccessModule;
		this.fontCacheModule = fontCacheModule;
		this.undoModule = undoModule;

		this.picker = picker;
		this.parentTab = parentTab;

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		entities = selectedEntitiesList;

		sharedChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (actor instanceof VisCheckBox)
					throw new IllegalStateException("SharedChangeListener cannot be used for checkboxes, use sharedCheckBoxChangeListener instead");

				setValuesToEntity();
				parentTab.dirty();
			}
		};

		sharedCheckBoxChangeListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (event.isStopped() == false) {
					beginSnapshot();
					setValuesToEntity();
					parentTab.dirty();
					endSnapshot();
				}
			}
		};

		sharedFocusListener = new FocusListener() {
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (focused)
					beginSnapshot();
				else
					endSnapshot();
			}
		};

		pickerListener = new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				for (EditorObject entity : entities)
					entity.setColor(newColor);

				parentTab.dirty();
				tint.setColor(newColor);
				picker.setListener(null);
				endSnapshot();
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
		specificTables.add(new MusicObjectTable(this));
		specificTables.add(new ObjectGroupTable(this));
		specificTables.add(new ParticleEffectTable(this));

		propertiesTable = new VisTable(true);

		top();
		add(new VisLabel("Entity Properties")).row();
		add(propertiesTable).fill().expand().padRight(0);

		addListener(new EventStopper());

		pack();

		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event event) {
		if (event instanceof UndoEvent || event instanceof RedoEvent)
			updateValues();

		return false;
	}

	private void createIdTable () {
		idTable = new VisTable(true);
		idTable.add(new VisLabel("ID"));
		idTable.add(idField = new VisValidableTextField()).expandX().fillX().padRight(6);
		idField.setProgrammaticChangeEvents(false);
		idField.addListener(sharedChangeListener);
	}

	private void createPositionTable () {
		positionTable = new VisTable(true);
		positionTable.add(new VisLabel("Position")).width(LABEL_WIDTH);
		positionTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		positionTable.add(xField = createNewNumberField()).width(FIELD_WIDTH);
		positionTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		positionTable.add(yField = createNewNumberField()).width(FIELD_WIDTH);
		positionTable.add().expand().fill();
	}

	private void createScaleTable () {
		scaleTable = new VisTable(true);
		scaleTable.add(new VisLabel("Scale")).width(LABEL_WIDTH);
		scaleTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(xScaleField = createNewNumberField()).width(FIELD_WIDTH);
		scaleTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		scaleTable.add(yScaleField = createNewNumberField()).width(FIELD_WIDTH);
		scaleTable.add().expand().fill();
	}

	private void createOriginTable () {
		originTable = new VisTable(true);
		originTable.add(new VisLabel("Origin")).width(LABEL_WIDTH);
		originTable.add(new VisLabel("X")).width(AXIS_LABEL_WIDTH);
		originTable.add(xOriginField = createNewNumberField()).width(FIELD_WIDTH);
		originTable.add(new VisLabel("Y")).width(AXIS_LABEL_WIDTH);
		originTable.add(yOriginField = createNewNumberField()).width(FIELD_WIDTH);
		originTable.add().expand().fill();
	}

	private void createRotationTintTable () {
		tint = new TintImage();
		tint.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				beginSnapshot();
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
		rotationTable.add(rotationField = createNewNumberField()).width(FIELD_WIDTH);
	}

	private void createFlipTable () {
		flipTable = new VisTable(true);

		flipTable.add(new VisLabel("Flip"));
		flipTable.add(xFlipCheck = new IndeterminateCheckbox("X"));
		flipTable.add(yFlipCheck = new IndeterminateCheckbox("Y")).padRight(10);

		xFlipCheck.addListener(sharedCheckBoxChangeListener);
		yFlipCheck.addListener(sharedCheckBoxChangeListener);
	}

	private void rebuildPropertiesTable () {
		propertiesTable.reset();
		TableUtils.setSpacingDefaults(propertiesTable);

		VisTable rotationTintTable = new VisTable(true);
		if (EntityUtils.isRotationSupportedForEntities(entities)) rotationTintTable.add(rotationTable);
		rotationTintTable.add().expand().fill();
		if (EntityUtils.isTintSupportedForEntities(entities)) rotationTintTable.add(tintTable);

		propertiesTable.defaults().padRight(6).fillX();
		propertiesTable.add(idTable).row();
		propertiesTable.add(positionTable).row();
		if (EntityUtils.isScaleSupportedForEntities(entities)) propertiesTable.add(scaleTable).row();
		if (EntityUtils.isOriginSupportedForEntities(entities)) propertiesTable.add(originTable).row();
		if (EntityUtils.isRotationSupportedForEntities(entities) || EntityUtils.isTintSupportedForEntities(entities))
			propertiesTable.add(rotationTintTable).row();
		if (EntityUtils.isFlipSupportedForEntities(entities)) propertiesTable.add(flipTable).right().fill(false).row();

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
		for (EditorObject entity : entities)
			if (!table.isSupported(entity)) return false;

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

	public void selectedEntitiesChanged () {
		rebuildPropertiesTable();
		updateValues();
	}

	void beginSnapshot () {
		if (snapshotInProgress) endSnapshot();
		snapshotInProgress = true;

		snapshots = new SnapshotUndoableActionGroup();

		for (EditorObject entity : entities) {
			snapshots.add(new SnapshotUndoableAction(entity));
		}
	}

	void endSnapshot () {
		if (!snapshotInProgress) return;
		snapshotInProgress = false;

		snapshots.takeSecondSnapshot();
		snapshots.dropUnchanged();
		snapshots.finalizeGroup();
		if (snapshots.size() > 0)
			undoModule.add(snapshots);
	}

	public NumberInputField createNewNumberField () {
		return new NumberInputField(sharedFocusListener, sharedChangeListener);
	}

	public Array<EditorObject> getEntities () {
		return entities;
	}

	public ChangeListener getSharedChangeListener () {
		return sharedChangeListener;
	}

	public ChangeListener getSharedCheckBoxChangeListener () {
		return sharedCheckBoxChangeListener;
	}

	Tab getParentTab () {
		return parentTab;
	}

	FileAccessModule getFileAccessModule () {
		return fileAccessModule;
	}

	FontCacheModule getFontCacheModule () {
		return fontCacheModule;
	}

	private void setTintUIForEntities () {
		Color firstColor = entities.first().getColor();
		for (EditorObject entity : entities) {
			if (!firstColor.equals(entity.getColor())) {
				tint.setUnknown(true);
				return;
			}
		}

		tint.setUnknown(false);
		tint.setColor(firstColor);
	}

	private String getEntitiesFieldValue (FloatValue floatValue) {
		return EntityUtils.getEntitiesCommonFloatValue(entities, floatValue);
	}

	private void setValuesToEntity () {
		for (int i = 0; i < entities.size; i++) {
			EditorObject entity = entities.get(i);

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

		if (activeSpecificTable != null) activeSpecificTable.setValuesToEntities();
	}

	public void updateValues () {
		if (entities.size == 0)
			setVisible(false);
		else {
			setVisible(true);

			idField.setText(EntityUtils.getCommonId(entities));
			xField.setText(getEntitiesFieldValue(EditorObject::getX));
			yField.setText(getEntitiesFieldValue(EditorObject::getY));
			xScaleField.setText(getEntitiesFieldValue(EditorObject::getScaleX));
			yScaleField.setText(getEntitiesFieldValue(EditorObject::getScaleY));
			xOriginField.setText(getEntitiesFieldValue(EditorObject::getOriginX));
			yOriginField.setText(getEntitiesFieldValue(EditorObject::getOriginY));
			rotationField.setText(getEntitiesFieldValue(EditorObject::getRotation));

			if (activeSpecificTable != null) activeSpecificTable.updateUIValues();

			if (EntityUtils.isTintSupportedForEntities(entities)) setTintUIForEntities();
			EntityUtils.setCommonCheckBoxState(entities, xFlipCheck, EditorObject::isFlipX);
			EntityUtils.setCommonCheckBoxState(entities, yFlipCheck, EditorObject::isFlipY);
		}
	}

	public void loadSupportsSpecificTables (ObjectSupportModule supportModule) {
		for(ObjectSupport support : supportModule.getSupports())
			specificTables.add(support.getObjectTable());
	}

	private static class SnapshotUndoableActionGroup extends UndoableActionGroup {

		public void dropUnchanged () {
			Iterator<UndoableAction> iterator = actions.iterator();

			while (iterator.hasNext()) {
				SnapshotUndoableAction action = (SnapshotUndoableAction) iterator.next();
				if (action.isSnapshotsEquals()) iterator.remove();
			}
		}

		public void takeSecondSnapshot () {
			for (UndoableAction action : actions) {
				SnapshotUndoableAction sAction = (SnapshotUndoableAction) action;
				sAction.takeSecondSnapshot();
			}
		}
	}

	private class SnapshotUndoableAction implements UndoableAction {
		EditorObject entity;
		EntityData dataSnapshot;
		EntityData dataSnapshot2;

		public SnapshotUndoableAction (EditorObject entity) {
			this.entity = entity;
			this.dataSnapshot = getDataForEntity(entity);
			this.dataSnapshot2 = getDataForEntity(entity);

			dataSnapshot.saveFrom(entity);
		}

		public void takeSecondSnapshot () {
			dataSnapshot2.saveFrom(entity);
		}

		public boolean isSnapshotsEquals () {
			return EqualsBuilder.reflectionEquals(dataSnapshot, dataSnapshot2, true);
		}

		@Override
		public void execute () {
			dataSnapshot.saveFrom(entity);
			dataSnapshot2.loadTo(entity);
		}

		@Override
		public void undo () {
			dataSnapshot2.saveFrom(entity);
			dataSnapshot.loadTo(entity);
		}

		private EntityData getDataForEntity (EditorObject entity) {
			if (entity instanceof SpriteObject) return new SpriteData();
			if (entity instanceof TextObject) return new TextObjectData();
			if (entity instanceof ParticleObject) return new ParticleEffectData();
			if (entity instanceof MusicObject) return new MusicData();
			if (entity instanceof SoundObject) return new SoundData();
			if (entity instanceof ObjectGroup) return new ObjectGroupData();

			ObjectSupport support = supportModule.get(entity.getClass());
			if (support != null) return support.getEmptyData();

			throw new UnsupportedOperationException("Cannot create snapshots entity data for entity class: " + entity.getClass());
		}
	}

}
