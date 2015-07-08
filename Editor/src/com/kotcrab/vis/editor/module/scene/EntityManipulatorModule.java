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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.scene.ObjectGroup;
import com.kotcrab.vis.editor.scene.SceneSelectionRoot;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;

/**
 * Entity manipulator module, allows to move entities on scene. Providers right click menu, rectangular selection
 * and properties window. Supports undo and redo.
 * @author Kotcrab
 */
@Deprecated
public class EntityManipulatorModule extends SceneModule{
	@InjectModule private CameraModule camera;
	@InjectModule private UndoModule undoModule;
	@InjectModule private SceneIOModule sceneIOModule;
	@InjectModule private ObjectSupportModule supportManager;
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ColorPickerModule colorPickerModule;
	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private FontCacheModule fontCacheModule;

	private ShapeRenderer shapeRenderer;

	private EntityProperties entityProperties;

	private final Array<EditorObject> selectedEntities = new Array<>();
	private SceneSelectionRoot selectionRoot;

	@Override
	public void init () {
		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();

		entityProperties = new EntityProperties(supportManager, fileAccess, fontCacheModule, undoModule, colorPickerModule.getPicker(), sceneTab, selectedEntities);
	}

	@Override
	public void postInit () {
		entityProperties.loadSupportsSpecificTables(projectContainer.get(ObjectSupportModule.class));
	}

	@Override @Deprecated
	public void dispose () {
		entityProperties.dispose();
	}

	@Deprecated
	public EntityProperties getEntityProperties () {
		return entityProperties;
	}

	@Deprecated
	private boolean preSelect (EditorObject entity, boolean resetSelectionWhenSwitchingRoot) {
		Layer layer = findObjectLayer(entity);
		if (layer == null)
			throw new IllegalArgumentException("Cannot select entity that isn't added to entity list");

		if (layer.locked) return false;
		switchLayer(layer);

		SceneSelectionRoot selectionRoot = findObjectSelectionRoot(entity);
		if (selectionRoot != layer) setSelectionRoot(selectionRoot, resetSelectionWhenSwitchingRoot);
		return true;
	}

	@Deprecated
	public void select (EditorObject entity) {
		if (preSelect(entity, true) == false) return;

		selectedEntities.clear();
		selectedEntities.add(entity);
		entityProperties.selectedEntitiesChanged();
	}


	@Deprecated
	public boolean switchLayer (Layer layer) {
		if (scene.getActiveLayer() != layer) {
			scene.setActiveLayer(layer);
			return true;
		}

		return false;
	}

	private Layer findObjectLayer (EditorObject entity) {
		for (Layer layer : scene.layers) {
			if (isObjectInSelectionRoot(layer, entity))
				return layer;
		}

		return null;
	}

	private boolean isObjectInSelectionRoot (SceneSelectionRoot selectionRoot, EditorObject entity) {
		for (EditorObject object : selectionRoot.getSelectionEntities()) {
			if (object == entity) return true;

			if (object instanceof SceneSelectionRoot) {
				if (isObjectInSelectionRoot((SceneSelectionRoot) object, entity) == true)
					return true;
			}
		}

		return false;
	}

	private SceneSelectionRoot findObjectSelectionRoot (EditorObject entity) {
		for (Layer layer : scene.layers) {
			SceneSelectionRoot root = findObjectInSelectionRoot(layer, entity);
			if (root != null)
				return root;
		}

		return null;
	}

	private SceneSelectionRoot findObjectInSelectionRoot (SceneSelectionRoot selectionRoot, EditorObject entity) {
		if (selectionRoot.getSelectionEntities().contains(entity, true)) return selectionRoot;

		for (EditorObject object : selectionRoot.getSelectionEntities()) {
			if (object instanceof SceneSelectionRoot) {
				SceneSelectionRoot root = findObjectInSelectionRoot((SceneSelectionRoot) object, entity);
				if (root != null) return root;
			}
		}

		return null;
	}

	private void selectAll () {
		if (scene.getActiveLayer().locked == true) return;
		for (EditorObject entity : selectionRoot.getSelectionEntities()) {
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
			//App.eventBus.post(new StatusBarEvent("Nothing to group!"));
			return;
		}

		UndoableActionGroup actionGroup = new UndoableActionGroup();

		ObjectGroup objGroup = new ObjectGroup();
		objGroup.addEntities(selectedEntities);

		actionGroup.execute(new EntityRemovedAction(selectionRoot, selectedEntities));
		actionGroup.execute(new EntityAddedAction(selectionRoot, objGroup));
		actionGroup.finalizeGroup();

		undoModule.add(actionGroup);
	}

	public void ungroupSelection () {
		if (selectedEntities.size == 0) {
			//App.eventBus.post(new StatusBarEvent("Nothing to ungroup!"));
			return;
		}

		UndoableActionGroup actionGroup = new UndoableActionGroup();

		for (EditorObject obj : selectedEntities) {
			if (obj instanceof ObjectGroup) {
				ObjectGroup group = (ObjectGroup) obj;

				actionGroup.add(new EntityRemovedAction(selectionRoot, group));
				actionGroup.add(new EntitiesAddedAction(selectionRoot, group.getObjects()));
			}
		}

		actionGroup.finalizeGroup();

		undoModule.execute(actionGroup);
	}

	public SceneSelectionRoot getSelectionRoot () {
		return selectionRoot;
	}

	public void setSelectionRoot (SceneSelectionRoot selectionRoot) {
		setSelectionRoot(selectionRoot, true);
	}

	public void setSelectionRoot (SceneSelectionRoot selectionRoot, boolean resetSelection) {
		this.selectionRoot = selectionRoot;
		if (resetSelection) resetSelection();
	}

	@Deprecated
	private class EntitiesAddedAction implements UndoableAction {
		private SceneSelectionRoot selectionRoot;
		private Array<EditorObject> newEntities;

		public EntitiesAddedAction (SceneSelectionRoot selectionRoot, Array<EditorObject> newEntities) {
			this.selectionRoot = selectionRoot;
			this.newEntities = new Array<>(newEntities);
		}

		@Override
		public void execute () {
			selectionRoot.getSelectionEntities().addAll(newEntities);
			resetSelection();
//			newEntities.forEach(EntityManipulatorModule.this::selectAppend);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			selectionRoot.getSelectionEntities().removeAll(newEntities, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	@Deprecated
	private class EntityAddedAction implements UndoableAction {
		private SceneSelectionRoot selectionRoot;
		private EditorObject entity;

		public EntityAddedAction (SceneSelectionRoot selectionRoot, EditorObject entity) {
			this.selectionRoot = selectionRoot;
			this.entity = entity;
		}

		@Override
		public void execute () {
			selectionRoot.getSelectionEntities().add(entity);
			select(entity);
			sceneTab.dirty();
		}

		@Override
		public void undo () {
			selectionRoot.getSelectionEntities().removeValue(entity, true);
			resetSelection();
			sceneTab.dirty();
		}
	}

	@Deprecated
	private class EntityRemovedAction implements UndoableAction {
		private SceneSelectionRoot selectionRoot;
		private Array<Integer> indexes;
		private Array<EditorObject> entities;

		public EntityRemovedAction (SceneSelectionRoot selectionRoot, Array<EditorObject> selectedEntities) {
			this.selectionRoot = selectionRoot;
			indexes = new Array<>(selectedEntities.size);
			entities = new Array<>(selectedEntities);
		}

		public EntityRemovedAction (SceneSelectionRoot selectionRoot, EditorObject object) {
			this.selectionRoot = selectionRoot;
			indexes = new Array<>(1);
			entities = new Array<>(1);
			entities.add(object);
		}

		@Override
		public void execute () {
			for (EditorObject entity : entities)
				indexes.add(selectionRoot.getSelectionEntities().indexOf(entity, true));

			selectionRoot.getSelectionEntities().removeAll(entities, true);

			resetSelection();
		}

		@Override
		public void undo () {
			for (int i = 0; i < entities.size; i++) {
				int index = indexes.get(i);
				EditorObject obj = entities.get(i);

				if (index > selectionRoot.getSelectionEntities().size)
					selectionRoot.getSelectionEntities().add(obj);
				else
					selectionRoot.getSelectionEntities().insert(index, obj);
			}

			resetSelection();
		}
	}

}
