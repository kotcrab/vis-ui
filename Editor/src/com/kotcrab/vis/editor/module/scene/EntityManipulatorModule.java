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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.MusicObject;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.MenuUtils;
import com.kotcrab.vis.ui.widget.PopupMenu;

public class EntityManipulatorModule extends SceneModule {
	private CameraModule camera;
	private UndoModule undoModule;
	private SceneIOModule sceneIOModule;
	private TextureCacheModule cacheModule;

	private ShapeRenderer shapeRenderer;

	private EntityProperties entityProperties;

	private Array<EditorEntity> entities;

	//required for setting position of pasted elements
	private float copyAttachX, copyAttachY;
	private Array<EditorEntity> entitiesClipboard = new Array<>();

	private final Array<EditorEntity> selectedEntities = new Array<>();

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
	public void added () {
		this.entities = scene.entities;

		createPopupMenu();

		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();
		camera = sceneContainer.get(CameraModule.class);
		undoModule = sceneContainer.get(UndoModule.class);
		sceneIOModule = projectContainer.get(SceneIOModule.class);
		cacheModule = projectContainer.get(TextureCacheModule.class);

		ColorPickerModule pickerModule = container.get(ColorPickerModule.class);
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		FontCacheModule fontCacheModule = projectContainer.get(FontCacheModule.class);
		entityProperties = new EntityProperties(fileAccess, fontCacheModule, undoModule, pickerModule.getPicker(), sceneTab, selectedEntities);

		rectangularSelection = new RectangularSelection(entities, this);
	}

	@Override
	public void render (Batch batch) {
		batch.end();
		if (selectedEntities.size > 0) {

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (EditorEntity entity : selectedEntities) {
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

		for (EditorEntity entity : entities) {
			if (entity instanceof Disposable) {
				Disposable disposable = (Disposable) entity;
				disposable.dispose();
			}
		}
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

			EditorEntity lastEntity = selectedEntities.peek();

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

			EditorEntity baseEntity = entitiesClipboard.peek();
			float xOffset = baseEntity.getX();
			float yOffset = baseEntity.getY();

			for (EditorEntity entity : entitiesClipboard) {
				float px = x - copyAttachX + (entity.getX() - xOffset);
				float py = y - copyAttachY + (entity.getY() - yOffset);

				entity.setPosition(px, py);
			}

			undoModule.execute(new PastedAction(entitiesClipboard));

			Array<EditorEntity> newClipboard = sceneIOModule.getKryo().copy(entitiesClipboard);
			entitiesClipboard.clear();
			entitiesClipboard.addAll(newClipboard);
		} else
			App.eventBus.post(new StatusBarEvent("Clipboard is empty, nothing to paste!"));
	}

	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	private boolean isMouseInsideSelectedEntities (float x, float y) {
		for (EditorEntity entity : selectedEntities) {
			if (entity.getBoundingRectangle().contains(x, y)) {
				EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
				if (result == entity) return true;
			}
		}

		return false;
	}

	private boolean isMouseInsideEntities (float x, float y) {
		for (EditorEntity entity : entities) {
			if (entity.getBoundingRectangle().contains(x, y)) return true;
		}

		return false;
	}

	public void processDropPayload (Payload payload) {
		Object obj = payload.getObject();

		if (obj instanceof TextureRegion) {
			TextureRegion region = (TextureRegion) payload.getObject();

			Sprite sprite = new Sprite(region);
			float x = camera.getInputX() - sprite.getWidth() / 2;
			float y = camera.getInputY() - sprite.getHeight() / 2;

			final SpriteObject object = new SpriteObject(cacheModule.getRelativePath(region), region, x, y);

			undoModule.execute(new EntityAddedAction(object));
		}

		if (obj instanceof TextObject) {
			TextObject text = (TextObject) obj;
			float x = camera.getInputX() - text.getWidth() / 2;
			float y = camera.getInputY() - text.getHeight() / 2;
			text.setPosition(x, y);

			undoModule.execute(new EntityAddedAction(text));
		}

		if (obj instanceof ParticleObject || obj instanceof TextObject || obj instanceof MusicObject) {
			EditorEntity entity = (EditorEntity) obj;
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

				EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
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
				for (EditorEntity entity : selectedEntities)
					moveActions.add(new MoveAction(entity));
			}

			if (rectangularSelection.touchDragged(x, y) == false) {

				if (dragging && selectedEntities.size > 0) {
					dragged = true;
					float deltaX = (x - lastTouchX);
					float deltaY = (y - lastTouchY);

					for (EditorEntity entity : selectedEntities)
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
			EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null)
				selectedEntities.removeValue(result, true);

			entityProperties.selectedEntitiesChanged();
		}

		if (button == Buttons.RIGHT && cameraDragged == false) {
			if (isMouseInsideSelectedEntities(x, y) == false)
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) selectedEntities.clear();

			EditorEntity result = findEntityWithSmallestSurfaceArea(x, y);
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
		selectedEntities.clear();
		entityProperties.selectedEntitiesChanged();
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

	public int getEntityCount () {
		return entities.size;
	}

	public Array<EditorEntity> getSelectedEntities () {
		return selectedEntities;
	}

	public void select (EditorEntity entity) {
		if (entities.contains(entity, true) == false)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		selectedEntities.clear();
		selectedEntities.add(entity);
		entityProperties.selectedEntitiesChanged();
	}

	void selectAppend (EditorEntity entity) {
		if (entities.contains(entity, true) == false)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		if (selectedEntities.contains(entity, true) == false)
			selectedEntities.add(entity);

		entityProperties.selectedEntitiesChanged();

	}

	private void selectAll () {
		for (EditorEntity entity : entities) {
			if (selectedEntities.contains(entity, true) == false)
				selectedEntities.add(entity);
		}

		entityProperties.selectedEntitiesChanged();
	}

	public void resetSelection () {
		selectedEntities.clear();
		entityProperties.selectedEntitiesChanged();
	}

	private class PastedAction implements UndoableAction {
		private Array<EditorEntity> newEntities;

		public PastedAction (Array<EditorEntity> newEntities) {
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
		private EditorEntity entity;

		public EntityAddedAction (EditorEntity entity) {
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
		private Array<EditorEntity> entities;

		public EntityRemovedAction (Array<EditorEntity> selectedEntities) {
			indexes = new Array<>(selectedEntities.size);
			entities = new Array<>(selectedEntities);
		}

		@Override
		public void execute () {
			for (EditorEntity entity : entities)
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
