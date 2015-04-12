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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.scene.ObjectGroup;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.ui.widget.PopupMenu;

public class EntityManipulatorModule extends SceneModule {
	private Stage stage;

	private CameraModule camera;
	private UndoModule undoModule;
	private SceneIOModule sceneIOModule;

	private ShapeRenderer shapeRenderer;

	private EntityProperties entityProperties;

	private Array<EditorObject> entities;

	//required for setting position of pasted elements
	private float copyAttachX, copyAttachY;
	private Array<EditorObject> entitiesClipboard = new Array<>();

	private final Array<EditorObject> selectedEntities = new Array<>();

	private float lastTouchX, lastTouchY;

	private RectangularSelection rectangularSelection;

	private boolean mouseInsideSelected;
	private boolean cameraDragged;
	private boolean dragging;
	private boolean dragged;

	private Array<MoveAction> moveActions = new Array<>();

	private PopupMenu generalPopupMenu;
	private PopupMenu entityPopupMenu;
	private float menuX, menuY;

	@Override
	public void init () {
		stage = Editor.instance.getStage();
		this.entities = scene.entities;

		createPopupMenu();

		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();
		camera = sceneContainer.get(CameraModule.class);
		undoModule = sceneContainer.get(UndoModule.class);
		sceneIOModule = projectContainer.get(SceneIOModule.class);

		ObjectSupportModule supportManager = container.get(ObjectSupportModule.class);
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		ColorPickerModule pickerModule = container.get(ColorPickerModule.class);
		FontCacheModule fontCacheModule = projectContainer.get(FontCacheModule.class);
		entityProperties = new EntityProperties(supportManager, fileAccess, fontCacheModule, undoModule, pickerModule.getPicker(), sceneTab, selectedEntities);

		rectangularSelection = new RectangularSelection(entities, this);
	}

	@Override
	public void render (Batch batch) {
		batch.end();
		if (selectedEntities.size > 0) {

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (EditorObject entity : selectedEntities) {
				Rectangle bounds = entity.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();

		}

		rectangularSelection.render(shapeRenderer);

		batch.begin();
	}

	@Override
	public void dispose () {
		entityProperties.dispose();

		for (EditorObject entity : entities)
			entity.dispose();
	}

	private void createPopupMenu () {
		entityPopupMenu = new PopupMenu();
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Cut", this::cut));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Copy", this::copy));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Remove", this::deleteSelectedEntities));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));

		generalPopupMenu = new PopupMenu();
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void selectedEntitiesToClipboard () {
		if (selectedEntities.size > 0) {
			entitiesClipboard.clear();
			entitiesClipboard.addAll(sceneIOModule.getKryo().copy(selectedEntities));

			EditorObject lastEntity = selectedEntities.peek();

			if (entityPopupMenu.getParent() != null) { //is menu visible
				copyAttachX = menuX - lastEntity.getX();
				copyAttachY = menuY - lastEntity.getY();
			} else {
				copyAttachX = lastEntity.getWidth() / 2;
				copyAttachY = lastEntity.getHeight() / 2;
			}
		}
	}

	private void cut () {
		selectedEntitiesToClipboard();
		deleteSelectedEntities();
		sceneTab.dirty();
	}

	private void copy () {
		selectedEntitiesToClipboard();
	}

	private void paste () {

		if (entitiesClipboard.size > 0) {
			float x = camera.getInputX();
			float y = camera.getInputY();

			EditorObject baseEntity = entitiesClipboard.peek();
			float xOffset = baseEntity.getX();
			float yOffset = baseEntity.getY();

			for (EditorObject entity : entitiesClipboard) {
				float px = x - copyAttachX + (entity.getX() - xOffset);
				float py = y - copyAttachY + (entity.getY() - yOffset);

				entity.setPosition(px, py);
			}

			undoModule.execute(new EntitiesAddedAction(entitiesClipboard));

			Array<EditorObject> newClipboard = sceneIOModule.getKryo().copy(entitiesClipboard);
			entitiesClipboard.clear();
			entitiesClipboard.addAll(newClipboard);
		} else
			App.eventBus.post(new StatusBarEvent("Clipboard is empty, nothing to paste!"));
	}

	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	private boolean isMouseInsideSelectedEntities (float x, float y) {
		for (EditorObject entity : selectedEntities) {
			if (entity.getBoundingRectangle().contains(x, y)) {
				EditorObject result = findEntityWithSmallestSurfaceArea(x, y);
				if (result == entity) return true;
			}
		}

		return false;
	}

	private boolean isMouseInsideEntities (float x, float y) {
		for (EditorObject obj : entities) {
			if (obj.getBoundingRectangle().contains(x, y)) return true;
		}

		return false;
	}

	public void processDropPayload (Payload payload) {
		Object obj = payload.getObject();

		if (obj instanceof EditorObject) {
			EditorObject entity = (EditorObject) obj;
			float x = camera.getInputX() - entity.getWidth() / 2;
			float y = camera.getInputY() - entity.getHeight() / 2;
			entity.setPosition(x, y);

			undoModule.execute(new EntityAddedAction(entity));
		}
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (button == Buttons.LEFT) {
			dragging = true;
			lastTouchX = x;
			lastTouchY = y;

			if (isMouseInsideEntities(x, y) == false) {
				rectangularSelection.touchDown(x, y, button);
				return true;
			}

			if (isMouseInsideSelectedEntities(x, y) == false) {
				//multiple select made easy
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) selectedEntities.clear();

				EditorObject result = findEntityWithSmallestSurfaceArea(x, y);
				if (result != null && selectedEntities.contains(result, true) == false)
					selectedEntities.add(result);

				entityProperties.selectedEntitiesChanged();
				mouseInsideSelected = true;
				return true;
			}
		}

		return false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			x = camera.getInputX();
			y = camera.getInputY();

			if (dragged == false) {
				moveActions.clear();
				for (EditorObject entity : selectedEntities)
					moveActions.add(new MoveAction(entity));
			}

			if (rectangularSelection.touchDragged(x, y) == false) {

				if (dragging && selectedEntities.size > 0) {
					dragged = true;
					float deltaX = (x - lastTouchX);
					float deltaY = (y - lastTouchY);

					for (EditorObject entity : selectedEntities)
						entity.setPosition(entity.getX() + deltaX, entity.getY() + deltaY);

					lastTouchX = x;
					lastTouchY = y;

					sceneTab.dirty();
					entityProperties.updateValues();
				}

			}
		}

		if (Gdx.input.isButtonPressed(Buttons.RIGHT))
			cameraDragged = true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (button == Buttons.LEFT && dragged == false && mouseInsideSelected == false) {
			EditorObject result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null)
				selectedEntities.removeValue(result, true);

			entityProperties.selectedEntitiesChanged();
		}

		if (button == Buttons.RIGHT && cameraDragged == false) {
			if (isMouseInsideSelectedEntities(x, y) == false)
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) selectedEntities.clear();

			EditorObject result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null && selectedEntities.contains(result, true) == false)
				selectedEntities.add(result);

			entityProperties.selectedEntitiesChanged();
			mouseInsideSelected = true;

			if (selectedEntities.size > 0) {
				menuX = x;
				menuY = y;

				entityPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
			} else
				generalPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
		}

		if (dragged) {
			for (int i = 0; i < selectedEntities.size; i++)
				moveActions.get(i).newData.saveFrom(selectedEntities.get(i));

			UndoableActionGroup group = new UndoableActionGroup();

			for (MoveAction action : moveActions)
				group.add(action);

			group.finalizeGroup();

			undoModule.add(group);
		}

		rectangularSelection.touchUp();

		lastTouchX = 0;
		lastTouchY = 0;
		mouseInsideSelected = false;
		dragging = false;
		dragged = false;
		cameraDragged = false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.FORWARD_DEL) { //Delete
			deleteSelectedEntities();
			return true;
		}

		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.A) selectAll();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C) copy();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V) paste();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.X) cut();

		return false;
	}

	private void deleteSelectedEntities () {
		undoModule.execute(new EntityRemovedAction(selectedEntities));
	}

	/**
	 * Returns entity with smallest surface area that contains point x,y.
	 * <p>
	 * When selecting entities, and few of them are overlapping, selecting entity with smallest
	 * area gives better results than just selecting first one.
	 */
	private EditorObject findEntityWithSmallestSurfaceArea (float x, float y) {
		EditorObject matchingEntity = null;
		float lastSurfaceArea = Float.MAX_VALUE;

		for (EditorObject entity : entities) {
			Rectangle entityBoundingRectangle = entity.getBoundingRectangle();
			if (entityBoundingRectangle.contains(x, y)) {

				float currentSurfaceArea = entityBoundingRectangle.width * entityBoundingRectangle.height;

				if (currentSurfaceArea < lastSurfaceArea) {
					matchingEntity = entity;
					lastSurfaceArea = currentSurfaceArea;
				}
			}
		}

		return matchingEntity;
	}

	public int getEntityCount () {
		return entities.size;
	}

	public Array<EditorObject> getSelectedEntities () {
		return selectedEntities;
	}

	public void select (EditorObject entity) {
		if (entities.contains(entity, true) == false)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		selectedEntities.clear();
		selectedEntities.add(entity);
		entityProperties.selectedEntitiesChanged();
	}

	void selectAppend (EditorObject entity) {
		if (entities.contains(entity, true) == false)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		if (selectedEntities.contains(entity, true) == false)
			selectedEntities.add(entity);

		entityProperties.selectedEntitiesChanged();

	}

	private void selectAll () {
		for (EditorObject entity : entities) {
			if (selectedEntities.contains(entity, true) == false)
				selectedEntities.add(entity);
		}

		entityProperties.selectedEntitiesChanged();
	}

	public void resetSelection () {
		selectedEntities.clear();
		entityProperties.selectedEntitiesChanged();
	}

	public void groupSelection () {
		if (selectedEntities.size == 0) {
			App.eventBus.post(new StatusBarEvent("Nothing to group!"));
			return;
		}

		UndoableActionGroup actionGroup = new UndoableActionGroup();

		ObjectGroup objGroup = new ObjectGroup();
		objGroup.addEntities(selectedEntities);

		actionGroup.execute(new EntityRemovedAction(selectedEntities));
		actionGroup.execute(new EntityAddedAction(objGroup));
		actionGroup.finalizeGroup();

		undoModule.add(actionGroup);
	}

	public void ungroupSelection () {
		if (selectedEntities.size == 0) {
			App.eventBus.post(new StatusBarEvent("Nothing to ungroup!"));
			return;
		}

		UndoableActionGroup actionGroup = new UndoableActionGroup();

		for (EditorObject obj : selectedEntities) {
			if (obj instanceof ObjectGroup) {
				ObjectGroup group = (ObjectGroup) obj;

				actionGroup.add(new EntityRemovedAction(group));
				actionGroup.add(new EntitiesAddedAction(group.getObjects()));
			}
		}

		actionGroup.finalizeGroup();

		undoModule.execute(actionGroup);
	}

	private class EntitiesAddedAction implements UndoableAction {
		private Array<EditorObject> newEntities;

		public EntitiesAddedAction (Array<EditorObject> newEntities) {
			this.newEntities = new Array<>(newEntities);
		}

		@Override
		public void execute () {
			entities.addAll(newEntities);
			resetSelection();
			newEntities.forEach(EntityManipulatorModule.this::selectAppend);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			scene.entities.removeAll(newEntities, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	private class EntityAddedAction implements UndoableAction {
		private EditorObject entity;

		public EntityAddedAction (EditorObject entity) {
			this.entity = entity;
		}

		@Override
		public void execute () {
			scene.entities.add(entity);
			select(entity);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			scene.entities.removeValue(entity, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	private class EntityRemovedAction implements UndoableAction {
		private Array<Integer> indexes;
		private Array<EditorObject> entities;

		public EntityRemovedAction (Array<EditorObject> selectedEntities) {
			indexes = new Array<>(selectedEntities.size);
			entities = new Array<>(selectedEntities);
		}

		public EntityRemovedAction (EditorObject object) {
			indexes = new Array<>(1);
			entities = new Array<>(1);
			entities.add(object);
		}

		@Override
		public void execute () {
			for (EditorObject entity : entities)
				indexes.add(scene.entities.indexOf(entity, true));

			scene.entities.removeAll(entities, true);

			resetSelection();
		}

		@Override
		public void undo () {
			for (int i = 0; i < entities.size; i++)
				scene.entities.insert(indexes.get(i), entities.get(i));

			resetSelection();
		}
	}

	private class MoveAction implements UndoableAction {
		private EntityPositionData oldData = new EntityPositionData();
		private EntityPositionData newData = new EntityPositionData();
		private EditorObject entity;

		public MoveAction (EditorObject entity) {
			this.entity = entity;
			oldData.saveFrom(entity);
		}

		@Override
		public void execute () {
			newData.loadTo(entity);
		}

		@Override
		public void undo () {
			oldData.loadTo(entity);
		}
	}

	private class EntityPositionData {
		public float x;
		public float y;

		public void saveFrom (EditorObject entity) {
			x = entity.getX();
			y = entity.getY();
		}

		public void loadTo (EditorObject entity) {
			entity.setPosition(x, y);
		}
	}
}
