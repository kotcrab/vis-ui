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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.*;
import com.kotcrab.vis.editor.module.scene.action.EntitiesAddedAction;
import com.kotcrab.vis.editor.module.scene.action.EntitiesRemovedAction;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityMoveTimerTask.Direction;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.ECSLayer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.LayersDialog;
import com.kotcrab.vis.editor.util.gdx.ImmutableArray;
import com.kotcrab.vis.editor.util.gdx.MenuUtils;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.assets.TextureAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.LayerComponent;
import com.kotcrab.vis.runtime.component.RenderableComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;
import com.kotcrab.vis.ui.widget.PopupMenu;

/** @author Kotcrab */
@Wire
public class ECSEntityManipulatorModule extends SceneModule {
	@InjectModule private StatusBarModule statusBar;

	@InjectModule private SceneIOModule sceneIO;

	@InjectModule private CameraModule camera;
	@InjectModule private UndoModule undoModule;
	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private RendererModule rendererModule;

	private ShapeRenderer shapeRenderer;
	private EntityProxyCache entityProxyCache;
	private ZIndexManipulatorManager zIndexManipulator;

	private LayersDialog layersDialog;

	private Tool currentTool;
	private Array<EntityProxy> selectedEntities = new Array<>();
	private ImmutableArray<EntityProxy> immutableSelectedEntities = new ImmutableArray<>(selectedEntities);

	private float copyAttachX, copyAttachY;
	private Array<ProtoEntity> entitiesClipboard = new Array<>();

	private boolean mouseDragged;
	private float menuX, menuY;
	private PopupMenu generalPopupMenu;
	private PopupMenu entityPopupMenu;

	private EntityMoveTimerTask entityMoveTimerTask;

	@Override
	public void init () {
		shapeRenderer = rendererModule.getShapeRenderer();
		entityProxyCache = entityEngine.getManager(EntityProxyCache.class);
		zIndexManipulator = entityEngine.getManager(ZIndexManipulatorManager.class);

		layersDialog = new LayersDialog(sceneTab, sceneContainer.getEntityEngine(), sceneContainer);
		createGeneralMenu();

		entityMoveTimerTask = new EntityMoveTimerTask(scene, immutableSelectedEntities);

		switchTool(new SelectionTool());

		scene.addObservable(notificationId -> {
			if (notificationId == EditorScene.ECS_ACTIVE_LAYER_CHANGED) {
				resetSelection();
			}
		});
	}

	private void createGeneralMenu () {
		entityPopupMenu = new PopupMenu();
		buildEntityPopupMenu(null);

		generalPopupMenu = new PopupMenu();
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		generalPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void buildEntityPopupMenu (Array<EntityProxy> entities) {
		entityPopupMenu.clearChildren();

//		if (entities != null) {
//			if (entities.size == 1 && entities.peek() instanceof SceneSelectionRoot) {
//				entityPopupMenu.addItem(MenuUtils.createMenuItem("Enter into group", () -> {
//					setSelectionRoot((SceneSelectionRoot) entities.peek());
//					selectAll();
//				}));
//				entityPopupMenu.addSeparator();
//			}
//		}

		entityPopupMenu.addItem(MenuUtils.createMenuItem("Cut", this::cut));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Copy", this::copy));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Paste", this::paste));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Remove", this::deleteSelectedEntities));
		entityPopupMenu.addItem(MenuUtils.createMenuItem("Select All", this::selectAll));
	}

	private void copy () {
		if (selectedEntities.size > 0) {
			entitiesClipboard.clear();
			selectedEntities.forEach(proxy -> entitiesClipboard.add(sceneIO.createProtoEntity(entityEngine, proxy.getEntity(), false)));

			EntityProxy proxy = selectedEntities.peek();

			if (entityPopupMenu.getParent() != null) { //is menu visible
				copyAttachX = menuX - proxy.getX();
				copyAttachY = menuY - proxy.getY();
			} else {
				copyAttachX = proxy.getWidth() / 2;
				copyAttachY = proxy.getHeight() / 2;
			}
		} else
			statusBar.setText("Nothing to copy!");
	}

	private void paste () {
		if (entitiesClipboard.size > 0) {
			selectedEntities.clear();

			Array<EntityProxy> proxies = new Array<>(selectedEntities.size);
			Array<Entity> entities = new Array<>(selectedEntities.size);

			entitiesClipboard.forEach(protoEntity -> {
				Entity entity = protoEntity.build();
				entities.add(entity);
				proxies.add(entityProxyCache.get(entity));
			});

			float x = camera.getInputX();
			float y = camera.getInputY();

			EntityProxy baseProxy = proxies.peek();
			float xOffset = baseProxy.getX();
			float yOffset = baseProxy.getY();

			for (EntityProxy proxy : proxies) {
				float px = x - copyAttachX + (proxy.getX() - xOffset);
				float py = y - copyAttachY + (proxy.getY() - yOffset);

				proxy.setPosition(px, py);
			}

			undoModule.add(new EntitiesAddedAction(sceneContainer, entityEngine, entities));

			selectedEntitiesChanged();
		} else
			statusBar.setText("Nothing to paste!");
	}

	private void cut () {
		copy();
		deleteSelectedEntities();
	}

	private void deleteSelectedEntities () {
		Array<Entity> entities = new Array<>();

		selectedEntities.forEach(proxy -> entities.add(proxy.getEntity()));
		selectedEntities.clear();
		selectedEntitiesChanged();

		undoModule.execute(new EntitiesRemovedAction(sceneContainer, entityEngine, entities));
	}

	public void processDropPayload (Payload payload) {
		if (scene.getActiveECSLayer().locked) {
			statusBar.setText("Layer is locked!");
			return;
		}

		Object obj = payload.getObject();

		if (obj instanceof TextureAssetDescriptor) {
			TextureAssetDescriptor asset = (TextureAssetDescriptor) obj;

			Entity entity = new EntityBuilder(entityEngine)
					.with(new SpriteComponent(textureCache.getSprite(asset)), new AssetComponent(asset),
							new RenderableComponent(0), new LayerComponent(scene.getActiveLayerId()))
					.build();

			EntityProxy proxy = entityProxyCache.get(entity);

			float x = camera.getInputX() - proxy.getWidth() / 2;
			float y = camera.getInputY() - proxy.getHeight() / 2;
			proxy.setPosition(x, y);

			undoModule.add(new EntitiesAddedAction(sceneContainer, entityEngine, entity));
		}
	}

	public void switchTool (Tool tool) {
		if (currentTool != null) currentTool.deactivated();
		currentTool = tool;
		currentTool.setModules(sceneContainer, scene);
		currentTool.activated();
	}

	public void select (Entity entity) {
		select(entityProxyCache.get(entity));
	}

	public void select (EntityProxy proxy) {
		ECSLayer layer = scene.getECSLayerById(proxy.getLayerID());
		if (layer.locked) return;

		scene.setActiveECSLayer(layer.id);

		selectedEntities.clear();
		selectedEntities.add(proxy);
//		entityProperties.selectedEntitiesChanged();
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (Entity entity) {
		selectAppend(entityProxyCache.get(entity));
	}

	/** Appends to current selection, however if entity layer is different than current layer then selection will be reset */
	public void selectAppend (EntityProxy proxy) {
		ECSLayer layer = scene.getECSLayerById(proxy.getLayerID());
		if (layer.locked) return;

		if (scene.getActiveLayerId() != layer.id) {
			scene.setActiveECSLayer(layer.id);
			selectedEntities.clear();
		}

		selectedEntities.add(proxy);
		selectedEntitiesChanged();
	}

	public void selectAll () {
		if (scene.getActiveECSLayer().locked) return;

		int layerId = scene.getActiveLayerId();

		selectedEntities.clear();
		entityProxyCache.getCache().values().forEach(proxy -> {
			if (proxy.getLayerID() == layerId)
				selectedEntities.add(proxy);
		});

		selectedEntitiesChanged();
	}

	public boolean isSelected (EntityProxy proxy) {
		return selectedEntities.contains(proxy, true);
	}

	public void resetSelection () {
		selectedEntities.clear();
		selectedEntitiesChanged();
	}

	public void deselect (EntityProxy result) {
		selectedEntities.removeValue(result, true);
		selectedEntitiesChanged();
	}

	public void selectedEntitiesChanged () {
		//entityProperties.selectedEntitesChanged()

		//debug
		if(selectedEntities.size > 0)
			System.out.println(selectedEntities.get(0).getZIndex());
	}

	public void markSceneDirty () {
		sceneTab.dirty();
	}

	@Override
	public void render (Batch batch) {
		batch.end();
		if (selectedEntities.size > 0) {

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (EntityProxy entity : selectedEntities) {
				Rectangle bounds = entity.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();

		}

		currentTool.render(shapeRenderer);

		batch.begin();

		currentTool.render(batch);
	}

	@Override
	public void dispose () {
		layersDialog.dispose();
	}

	public LayersDialog getLayersDialog () {
		return layersDialog;
	}

	public ImmutableArray<EntityProxy> getSelectedEntities () {
		return immutableSelectedEntities;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (scene.getActiveECSLayer().locked) return false;
		return currentTool.touchDown(event, x, y, pointer, button);
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (scene.getActiveECSLayer().locked) return;
		mouseDragged = true;
		currentTool.touchDragged(event, x, y, pointer);
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (scene.getActiveECSLayer().locked) return;
		currentTool.touchUp(event, x, y, pointer, button);

		if (button == Buttons.RIGHT && mouseDragged == false) {
			if (selectedEntities.size > 0) {
				menuX = x;
				menuY = y;

				buildEntityPopupMenu(selectedEntities);
				entityPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
			} else
				generalPopupMenu.showMenu(event.getStage(), event.getStageX(), event.getStageY());
		}

		mouseDragged = false;
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		if (scene.getActiveECSLayer().locked) return false;
		return currentTool.mouseMoved(event, x, y);
	}

	@Override
	public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
		if (scene.getActiveECSLayer().locked) return;
		currentTool.enter(event, x, y, pointer, fromActor);
	}

	@Override
	public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
		if (scene.getActiveECSLayer().locked) return;
		currentTool.exit(event, x, y, pointer, toActor);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		if (scene.getActiveECSLayer().locked) return false;
		return currentTool.scrolled(event, x, y, amount);
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (scene.getActiveECSLayer().locked) return false;
		boolean result = currentTool.keyDown(event, keycode);

		if (result == false) {
			entityMoveTimerTask.cancel();

			if (keycode == Keys.FORWARD_DEL) { //Delete
				deleteSelectedEntities();
				return true;
			}

			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.A) selectAll();
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C) copy();
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V) paste();
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.X) cut();
			if (Gdx.input.isKeyPressed(Keys.PAGE_UP)) zIndexManipulator.moveSelectedEntities(getSelectedEntities(), true);
			if (Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) zIndexManipulator.moveSelectedEntities(getSelectedEntities(), false);

			int delta = 1;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) delta *= 10;
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) delta *= 10;
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
				float keyRepeatInitialTime = 0.4f;
				float keyRepeatTime = 0.05f;
				Timer.schedule(entityMoveTimerTask, keyRepeatInitialTime, keyRepeatTime);
				return true;
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean keyUp (InputEvent event, int keycode) {
		if (scene.getActiveECSLayer().locked) return false;
		entityMoveTimerTask.cancel();
		return currentTool.keyUp(event, keycode);
	}

	@Override
	public boolean keyTyped (InputEvent event, char character) {
		if (scene.getActiveECSLayer().locked) return false;
		return currentTool.keyTyped(event, character);
	}
}
