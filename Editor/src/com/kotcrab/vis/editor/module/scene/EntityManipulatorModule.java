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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.ui.scene.EntityProperties;

public class EntityManipulatorModule extends SceneModule {
	private CameraModule camera;
	private UndoModule undoModule;

	private ShapeRenderer shapeRenderer;

	private EntityProperties entityProperties;

	private Array<EditorEntity> entities;
	private final Array<EditorEntity> selectedEntities = new Array<>();

	private float lastTouchX;
	private float lastTouchY;

	private boolean selected;
	private boolean dragging;
	private boolean dragged;

	private Array<MoveAction> moveActions = new Array<>();

	@Override
	public void added () {
		this.entities = scene.entities;

		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();
		camera = sceneContainer.get(CameraModule.class);
		undoModule = sceneContainer.get(UndoModule.class);

		ColorPickerModule pickerModule = container.get(ColorPickerModule.class);
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		FontCacheModule fontCacheModule = projectContainer.get(FontCacheModule.class);
		entityProperties = new EntityProperties(fileAccess, fontCacheModule, pickerModule.getPicker(), sceneTab, selectedEntities);
	}

	@Override
	public void render (Batch batch) {
		if (selectedEntities.size > 0) {
			batch.end();

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (EditorEntity entity : selectedEntities) {
				Rectangle bounds = entity.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();

			batch.begin();
		}
	}

	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	private boolean isMouseInsideSelectedEntities (float x, float y) {
		for (EditorEntity entity : selectedEntities)
			if (entity.getBoundingRectangle().contains(x, y)) {
				EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
				if (result == entity) return true;
			}

		return false;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (button == Buttons.LEFT) {
			dragging = true;
			lastTouchX = x;
			lastTouchY = y;

			if (isMouseInsideSelectedEntities(x, y) == false) {
				//multiple select made easy
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) selectedEntities.clear();

				EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
				if (result != null && selectedEntities.contains(result, true) == false)
					selectedEntities.add(result);

				entityProperties.selectedEntitiesChanged();

				selected = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (dragged == false) {
			moveActions.clear();
			for (EditorEntity entity : selectedEntities)
				moveActions.add(new MoveAction(entity));
		}

		if (dragging && selectedEntities.size > 0) {
			dragged = true;
			float deltaX = (x - lastTouchX);
			float deltaY = (y - lastTouchY);

			for (EditorEntity entity : selectedEntities)
				entity.setPosition(entity.getX() + deltaX, entity.getY() + deltaY);

			lastTouchX = x;
			lastTouchY = y;

			sceneTab.setDirty(true);
			entityProperties.updateValues();
		}
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (dragged == false && selected == false) {
			EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null)
				selectedEntities.removeValue(result, true);

			entityProperties.selectedEntitiesChanged();
		}

		if (dragged) {
			for (int i = 0; i < selectedEntities.size; i++) {
				moveActions.get(i).newData.saveFrom(selectedEntities.get(i));
			}

			UndoableActionGroup group = new UndoableActionGroup();

			for (MoveAction action : moveActions)
				group.add(action);

			group.finalizeGroup();

			undoModule.add(group);
		}

		lastTouchX = 0;
		lastTouchY = 0;
		selected = false;
		dragging = false;
		dragged = false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.FORWARD_DEL) { //Delete
			undoModule.execute(new EntityRemoved(selectedEntities));
			selectedEntities.clear();
			entityProperties.selectedEntitiesChanged();

			return true;
		}

		return false;
	}

	/**
	 * Returns entity with smallest surface area that contains point x,y.
	 * <p/>
	 * When selecting entities, and few of them are overlapping, selecting entity with smallest
	 * area gives better results than just selecting first one.
	 */
	private EditorEntity findEntityWithSmallestSurfaceArea (float x, float y) {
		EditorEntity matchingEntity = null;
		float lastSurfaceArea = Float.MAX_VALUE;

		for (EditorEntity entity : entities) {
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

	public Array<EditorEntity> getSelectedEntities () {
		return selectedEntities;
	}

	public void select (EditorEntity entity) {
		selectedEntities.clear();
		selectedEntities.add(entity);
		entityProperties.selectedEntitiesChanged();
	}

	public void resetSelection () {
		selectedEntities.clear();
		entityProperties.selectedEntitiesChanged();
	}

	private class EntityRemoved implements UndoableAction {
		private Array<Integer> indexes;
		private Array<EditorEntity> entities;

		public EntityRemoved (Array<EditorEntity> selectedEntities) {
			indexes = new Array<>(selectedEntities.size);
			entities = new Array<>(selectedEntities);
		}

		@Override
		public void execute () {
			for (EditorEntity entity : entities)
				indexes.add(scene.entities.indexOf(entity, true));

			scene.entities.removeAll(entities, true);
		}

		@Override
		public void undo () {
			for (int i = 0; i < entities.size; i++)
				scene.entities.insert(indexes.get(i), entities.get(i));
		}
	}

	private class MoveAction implements UndoableAction {
		private EntityPositionData oldData = new EntityPositionData();
		private EntityPositionData newData = new EntityPositionData();
		private EditorEntity entity;

		public MoveAction (EditorEntity entity) {
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

		public void saveFrom (EditorEntity entity) {
			x = entity.getX();
			y = entity.getY();
		}

		public void loadTo (EditorEntity entity) {
			entity.setPosition(x, y);
		}
	}
}
