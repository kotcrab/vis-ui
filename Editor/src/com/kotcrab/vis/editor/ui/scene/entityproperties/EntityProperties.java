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
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.RedoEvent;
import com.kotcrab.vis.editor.event.UndoEvent;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.UndoableAction;
import com.kotcrab.vis.editor.module.scene.UndoableActionGroup;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.IndeterminateCheckbox;
import com.kotcrab.vis.editor.ui.tabbedpane.Tab;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.ParticleEffectData;
import com.kotcrab.vis.runtime.data.SpriteData;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisValidableTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Iterator;

import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.TintImage;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.getEntitiesId;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isFlipSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isOriginSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isRotationSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isScaleSupportedForEntities;
import static com.kotcrab.vis.editor.ui.scene.entityproperties.Utils.isTintSupportedForEntities;

public class EntityProperties extends VisTable implements Disposable, EventListener {
	private static final int LABEL_WIDTH = 60;
	private static final int AXIS_LABEL_WIDTH = 10;
	private static final int FIELD_WIDTH = 70;

	private FileAccessModule fileAccessModule;
	private FontCacheModule fontCacheModule;
	private UndoModule undoModule;

	private ColorPicker picker;
	private Tab parentTab;

	private Array<EditorEntity> entities;

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

	public EntityProperties (FileAccessModule fileAccessModule, FontCacheModule fontCacheModule, final UndoModule undoModule, final ColorPicker picker, final Tab parentTab, final Array<EditorEntity> selectedEntitiesList) {
		super(true);
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
				for (EditorEntity entity : entities)
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

		propertiesTable = new VisTable(true);

		top();
		add(new VisLabel("Entity Properties")).row();
		add(propertiesTable).fill().expand().padRight(0);

		addListeners();

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
		if (isRotationSupportedForEntities(entities) || isTintSupportedForEntities(entities))
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

	void beginSnapshot () {
		if (snapshotInProgress) endSnapshot();
		snapshotInProgress = true;

		snapshots = new SnapshotUndoableActionGroup();

		for (EditorEntity entity : entities) {
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

	void dropSnapshot () {
		snapshotInProgress = false;
		snapshots = null;
	}

	NumberInputField createNewNumberField () {
		return new NumberInputField(sharedFocusListener, sharedChangeListener);
	}

	Array<EditorEntity> getEntities () {
		return entities;
	}

	Tab getParentTab () {
		return parentTab;
	}

	ChangeListener getSharedChangeListener () {
		return sharedChangeListener;
	}

	public ChangeListener getSharedCheckBoxChangeListener () {
		return sharedCheckBoxChangeListener;
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
				xFlipCheck.setIndeterminate(true);
				return;
			}
		}

		xFlipCheck.setChecked(xFlip);
	}

	private void setFlipYUICheckForEntities () {
		boolean yFlip = entities.first().isFlipY();
		for (EditorEntity entity : entities) {
			if (yFlip != entity.isFlipY()) {
				yFlipCheck.setIndeterminate(true);
				return;
			}
		}

		yFlipCheck.setChecked(yFlip);
	}

	private void setTintUIForEntities () {
		Color firstColor = entities.first().getColor();
		for (EditorEntity entity : entities) {
			if (!firstColor.equals(entity.getColor())) {
				tint.setUnknown(true);
				return;
			}
		}

		tint.setUnknown(false);
		tint.setColor(firstColor);
	}

	private String getEntitiesFieldValue (EntityValue entityValue) {
		return Utils.getEntitiesFieldValue(entities, entityValue);
	}

	private void setValuesToEntity () {
		for (int i = 0; i < entities.size; i++) {
			EditorEntity entity = entities.get(i);

			entity.setId(idField.getText().equals("") ? null : idField.getText());
			entity.setPosition(FieldUtils.getFloat(xField, entity.getX()), FieldUtils.getFloat(yField, entity.getY()));

			if (isScaleSupportedForEntities(entities))
				entity.setScale(FieldUtils.getFloat(xScaleField, entity.getScaleX()), FieldUtils.getFloat(yScaleField, entity.getScaleY()));

			if (isOriginSupportedForEntities(entities))
				entity.setOrigin(FieldUtils.getFloat(xOriginField, entity.getOriginX()), FieldUtils.getFloat(yOriginField, entity.getOriginY()));

			if (isRotationSupportedForEntities(entities))
				entity.setRotation(FieldUtils.getFloat(rotationField, entity.getRotation()));

			if (isFlipSupportedForEntities(entities)) {
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

			idField.setText(getEntitiesId(entities));
			xField.setText(getEntitiesFieldValue(EditorEntity::getX));
			yField.setText(getEntitiesFieldValue(EditorEntity::getY));
			xScaleField.setText(getEntitiesFieldValue(EditorEntity::getScaleX));
			yScaleField.setText(getEntitiesFieldValue(EditorEntity::getScaleY));
			xOriginField.setText(getEntitiesFieldValue(EditorEntity::getOriginX));
			yOriginField.setText(getEntitiesFieldValue(EditorEntity::getOriginY));
			rotationField.setText(getEntitiesFieldValue(EditorEntity::getRotation));

			if (activeSpecificTable != null) activeSpecificTable.updateUIValues();

			if (isTintSupportedForEntities(entities)) setTintUIForEntities();
			setFlipXUICheckForEntities();
			setFlipYUICheckForEntities();
		}
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

	private static class SnapshotUndoableAction implements UndoableAction {
		EditorEntity entity;
		EntityData dataSnapshot;
		EntityData dataSnapshot2;

		public SnapshotUndoableAction (EditorEntity entity) {
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

		private EntityData getDataForEntity (EditorEntity entity) {
			if (entity instanceof SpriteObject) return new SpriteData();
			if (entity instanceof TextObject) return new TextObjectData();
			if (entity instanceof ParticleObject) return new ParticleEffectData();

			throw new UnsupportedOperationException("Cannot create snapshots entity data for entity class: " + entity.getClass());
		}
	}
}
