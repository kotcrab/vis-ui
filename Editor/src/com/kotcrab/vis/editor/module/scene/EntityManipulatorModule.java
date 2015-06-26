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
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.project.ObjectSupportModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.scene.ObjectGroup;
import com.kotcrab.vis.editor.ui.scene.LayersDialog;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.ui.widget.PopupMenu;

/**
 * Entity manipulator module, allows to move entities on scene. Providers right click menu, rectangular selection
 * and properties window. Supports undo and redo.
 * @author Kotcrab
 */
public class EntityManipulatorModule extends SceneModule {
	@InjectModule private CameraModule camera;
	@InjectModule private UndoModule undoModule;
	@InjectModule private SceneIOModule sceneIOModule;
	@InjectModule private ObjectSupportModule supportManager;
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ColorPickerModule colorPickerModule;
	@InjectModule private FontCacheModule fontCacheModule;

	private ShapeRenderer shapeRenderer;

	private EntityProperties entityProperties;
	private LayersDialog layersDialog;

	//required for setting position of pasted elements
	private float copyAttachX, copyAttachY;
	private Array<EditorObject> entitiesClipboard = new Array<>();

	private RectangularSelection rectangularSelection;
	private final Array<EditorObject> selectedEntities = new Array<>();

	private float lastTouchX, lastTouchY;

	private boolean mouseInsideSelected;
	private boolean cameraDragged;
	private boolean dragging;
	private boolean dragged;

	private Array<MoveAction> moveActions = new Array<>();

	private PopupMenu generalPopupMenu;
	private PopupMenu entityPopupMenu;
	private float menuX, menuY;

	private static float keyRepeatInitialTime = 0.4f;
	private static float keyRepeatTime = 0.05f;
	private EntityMoveTimerTask entityMoveTimerTask;

	@Override
	public void init () {
		createPopupMenu();

		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();

		entityProperties = new EntityProperties(supportManager, fileAccess, fontCacheModule, undoModule, colorPickerModule.getPicker(), sceneTab, selectedEntities);
		layersDialog = new LayersDialog(this, undoModule, scene);

		rectangularSelection = new RectangularSelection(scene, this);

		entityMoveTimerTask = new EntityMoveTimerTask();
	}

	@Override
	public void postInit () {
		entityProperties.loadSupportsSpecificTables(projectContainer.get(ObjectSupportModule.class));
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
		scene.dispose();
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

			undoModule.execute(new EntitiesAddedAction(scene.activeLayer, entitiesClipboard));

			Array<EditorObject> newClipboard = sceneIOModule.getKryo().copy(entitiesClipboard);
			entitiesClipboard.clear();
			entitiesClipboard.addAll(newClipboard);
		} else
			App.eventBus.post(new StatusBarEvent("Clipboard is empty, nothing to paste!"));
	}

	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	public LayersDialog getLayersDialog () {
		return layersDialog;
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
		for (EditorObject obj : scene.activeLayer.entities) {
			if (obj.getBoundingRectangle().contains(x, y)) return true;
		}

		return false;
	}

	public void processDropPayload (Payload payload) {
		if (scene.activeLayer.locked) {
			App.eventBus.post(new StatusBarEvent("Layer is locked!"));
			return;
		}

		Object obj = payload.getObject();

		if (obj instanceof EditorObject) {
			EditorObject entity = (EditorObject) obj;
			float x = camera.getInputX() - entity.getWidth() / 2;
			float y = camera.getInputY() - entity.getHeight() / 2;
			entity.setPosition(x, y);

			undoModule.execute(new EntityAddedAction(scene.activeLayer, entity));
		}
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (scene.activeLayer.locked) return false;
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
		entityMoveTimerTask.cancel();

		if (keycode == Keys.FORWARD_DEL) { //Delete
			deleteSelectedEntities();
			return true;
		}

		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.A) selectAll();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C) copy();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V) paste();
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.X) cut();

		int delta = 1;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) delta = 10;
		boolean runTask = false;

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			entityMoveTimerTask.set(Direction.UP, delta);
			runTask = true;
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			entityMoveTimerTask.set(Direction.DOWN, delta);
			runTask = true;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			entityMoveTimerTask.set(Direction.LEFT, delta);
			runTask = true;
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			entityMoveTimerTask.set(Direction.RIGHT, delta);
			runTask = true;
		}

		if (runTask) {
			entityMoveTimerTask.run();
			Timer.schedule(entityMoveTimerTask, keyRepeatInitialTime, keyRepeatTime);
			return true;
		}

		return false;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		entityMoveTimerTask.cancel();
		return false;
	}

	private void deleteSelectedEntities () {
		undoModule.execute(new EntityRemovedAction(scene.activeLayer, selectedEntities));
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

		for (EditorObject entity : scene.activeLayer.entities) {
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

	public int getTotalEntityCount () {
		int count = 0;

		for (Layer layer : scene.layers)
			count += layer.entities.size;

		return count;
	}

	public Array<EditorObject> getSelectedEntities () {
		return selectedEntities;
	}

	public void select (EditorObject entity) {
		Layer layer = findObjectLayer(entity);
		if (layer == null)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		if (layer.locked) return;
		switchLayer(layer);

		selectedEntities.clear();
		selectedEntities.add(entity);
		entityProperties.selectedEntitiesChanged();
	}

	void selectAppend (EditorObject entity) {
		Layer layer = findObjectLayer(entity);
		if (layer == null)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		if (layer.locked) return;
		switchLayer(layer);

		if (selectedEntities.contains(entity, true) == false)
			selectedEntities.add(entity);

		entityProperties.selectedEntitiesChanged();
	}

	public boolean switchLayer (Layer layer) {
		if (scene.activeLayer != layer) {
			scene.activeLayer = layer;
			resetSelection();
			return true;
		}

		return false;
	}

	public void sceneDirty () {
		sceneTab.dirty();
	}

	private Layer findObjectLayer (EditorObject entity) {
		for (Layer layer : scene.layers) {
			if (layer.entities.contains(entity, true)) return layer;
		}

		return null;
	}

	private void selectAll () {
		if (scene.activeLayer.locked == true) return;
		for (EditorObject entity : scene.activeLayer.entities) {
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

		actionGroup.execute(new EntityRemovedAction(scene.activeLayer, selectedEntities));
		actionGroup.execute(new EntityAddedAction(scene.activeLayer, objGroup));
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

				actionGroup.add(new EntityRemovedAction(scene.activeLayer, group));
				actionGroup.add(new EntitiesAddedAction(scene.activeLayer, group.getObjects()));
			}
		}

		actionGroup.finalizeGroup();

		undoModule.execute(actionGroup);
	}

	private class EntitiesAddedAction implements UndoableAction {
		private Layer layer;
		private Array<EditorObject> newEntities;

		public EntitiesAddedAction (Layer layer, Array<EditorObject> newEntities) {
			this.layer = layer;
			this.newEntities = new Array<>(newEntities);
		}

		@Override
		public void execute () {
			layer.entities.addAll(newEntities);
			resetSelection();
			newEntities.forEach(EntityManipulatorModule.this::selectAppend);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			layer.entities.removeAll(newEntities, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	private class EntityAddedAction implements UndoableAction {
		private Layer layer;
		private EditorObject entity;

		public EntityAddedAction (Layer layer, EditorObject entity) {
			this.layer = layer;
			this.entity = entity;
		}

		@Override
		public void execute () {
			layer.entities.add(entity);
			select(entity);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			layer.entities.removeValue(entity, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	private class EntityRemovedAction implements UndoableAction {
		private Layer layer;
		private Array<Integer> indexes;
		private Array<EditorObject> entities;

		public EntityRemovedAction (Layer layer, Array<EditorObject> selectedEntities) {
			this.layer = layer;
			indexes = new Array<>(selectedEntities.size);
			entities = new Array<>(selectedEntities);
		}

		public EntityRemovedAction (Layer layer, EditorObject object) {
			this.layer = layer;
			indexes = new Array<>(1);
			entities = new Array<>(1);
			entities.add(object);
		}

		@Override
		public void execute () {
			for (EditorObject entity : entities)
				indexes.add(layer.entities.indexOf(entity, true));

			layer.entities.removeAll(entities, true);

			resetSelection();
		}

		@Override
		public void undo () {
			for (int i = 0; i < entities.size; i++) {
				int index = indexes.get(i);
				EditorObject obj = entities.get(i);

				if (index > layer.entities.size)
					layer.entities.add(obj);
				else
					layer.entities.insert(index, obj);
			}

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

	private enum Direction {UP, DOWN, LEFT, RIGHT}

	private class EntityMoveTimerTask extends Task {
		private Direction dir;
		private int delta;

		@Override
		public void run () {
			if (scene.activeLayer.locked) return;

			for (EditorObject obj : selectedEntities) {
				switch (dir) {
					case UP:
						obj.setY(obj.getY() + delta);
						break;
					case DOWN:
						obj.setY(obj.getY() + delta * -1);
						break;
					case LEFT:
						obj.setX(obj.getX() + delta * -1);
						break;
					case RIGHT:
						obj.setX(obj.getX() + delta);
						break;
				}
			}
		}

		public void set (Direction dir, int delta) {
			this.dir = dir;
			this.delta = delta;
		}
	}
}
