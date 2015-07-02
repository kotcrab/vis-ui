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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.util.BaseObservable;
import com.kotcrab.vis.editor.util.gdx.ImmutableArray;
import com.kotcrab.vis.runtime.scene.SceneViewport;

import java.util.Comparator;

/**
 * Editor scene class, serialized by Kryo
 * @author Kotcrab
 */
public class EditorScene extends BaseObservable {
	@Deprecated public static final int ACTIVE_LAYER_CHANGED = 999;
	public static final int ECS_ACTIVE_LAYER_CHANGED = 0;
	public static final int LAYER_ADDED = 1;
	public static final int LAYER_INSERTED = 2;
	public static final int LAYER_REMOVED = 3;
	public static final int LAYERS_SORTED = 4;

	private static final Comparator<ECSLayer> LAYER_COMPARATOR = (o1, o2) -> (int) Math.signum(o1.id - o2.id) * -1;

	/** Scene file, path is relative to project Vis folder */
	public String path;
	public int width;
	public int height;
	public SceneViewport viewport;

	@Deprecated public Array<Layer> layers = new Array<>();
	@Deprecated private transient Layer activeLayer;

	private Array<ECSLayer> ecsLayers = new Array<>();
	private int activeLayerId;

	private Array<EntityScheme> schemes; //for serialization

	public EditorScene (FileHandle file, SceneViewport viewport, int width, int height) {
		this.path = file.path();
		this.viewport = viewport;
		this.width = width;
		this.height = height;

		layers.add(new Layer("Background"));
		ecsLayers.add(new ECSLayer("Background", 0));
		schemes = new Array<>();
	}

	@Override
	public void onDeserialize () {
		super.onDeserialize();
		forceSortLayers();
	}

	public void setSchemes (Array<EntityScheme> schemes) {
		this.schemes = schemes;
	}

	/** Returns entity schemes for this scene. Warning: if scene is currently opened in editor this list will be outdated if scene is dirty */
	public Array<EntityScheme> getSchemes () {
		return schemes;
	}

	public FileHandle getFile () {
		return Gdx.files.absolute(path);
	}

	@Deprecated
	public void setActiveLayer (Layer layer) {
		this.activeLayer = layer;
		postNotification(ACTIVE_LAYER_CHANGED);
	}

	@Deprecated
	public Layer getActiveLayer () {
		return activeLayer;
	}

	public ECSLayer getActiveECSLayer () {
		for (ECSLayer layer : ecsLayers) {
			if (layer.id == activeLayerId)
				return layer;
		}

		throw new IllegalStateException("No active layer");
	}

	public int getActiveLayerId () {
		return activeLayerId;
	}

	public boolean setActiveECSLayer (int layerId) {
		if (this.activeLayerId != layerId) {
			this.activeLayerId = layerId;
			postNotification(ECS_ACTIVE_LAYER_CHANGED);
			return true;
		}

		return false;
	}

	public ECSLayer getECSLayerById (int id) {
		for (ECSLayer layer : ecsLayers) {
			if (layer.id == id) return layer;
		}

		return null;
	}

	public ECSLayer getECSLayerByName (String name) {
		for (ECSLayer layer : ecsLayers) {
			if (layer.name.equals(name)) return layer;
		}

		return null;
	}

	public ImmutableArray<ECSLayer> getECSLayers () {
		return new ImmutableArray<>(ecsLayers);
	}

	public ECSLayer addLayer (String name) {
		ECSLayer layer = new ECSLayer(name, getFreeLayerID());
		ecsLayers.add(layer);
		ecsLayers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_ADDED);
		return layer;
	}

	public void insertLayer (ECSLayer layer) {
		if (isLayerIdUsed(layer.id))
			throw new IllegalStateException("Layer with this id already exist!");

		ecsLayers.add(layer);
		ecsLayers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_INSERTED);
	}

	public boolean removeLayer (ECSLayer layer) {
		boolean result = ecsLayers.removeValue(layer, true);
		ecsLayers.sort(LAYER_COMPARATOR);
		if (layer.id == activeLayerId)
			activeLayerId = ecsLayers.first().id;

		postNotification(LAYER_REMOVED);
		return result;
	}

	public void forceSortLayers () {
		ecsLayers.sort(LAYER_COMPARATOR);
		postNotification(LAYERS_SORTED);
	}

	private int getFreeLayerID () {
		int freeID = 0;

		while (isLayerIdUsed(freeID)) {
			freeID++;
		}

		return freeID;
	}

	private boolean isLayerIdUsed (int id) {
		for (ECSLayer layer : ecsLayers) {
			if (layer.id == id)
				return true;
		}

		return false;
	}
}
