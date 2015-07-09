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

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.scene.SceneSelectionRoot;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;

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

	private EntityProperties entityProperties;

	private final Array<EditorObject> selectedEntities = new Array<>();
	private SceneSelectionRoot selectionRoot;

	@Override
	public void init () {
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

	public void resetSelection () {
		selectedEntities.clear();
		entityProperties.selectedEntitiesChanged();
	}


	public void setSelectionRoot (SceneSelectionRoot selectionRoot, boolean resetSelection) {
		this.selectionRoot = selectionRoot;
		if (resetSelection) resetSelection();
	}



}
