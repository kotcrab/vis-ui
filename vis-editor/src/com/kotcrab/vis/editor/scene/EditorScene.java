/*
 * Copyright 2014-2016 See AUTHORS file.
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
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.util.BaseObservable;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.runtime.data.PhysicsSettings;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.util.Comparator;

/**
 * Editor scene class, serialized by Kryo
 * @author Kotcrab
 */
public class EditorScene extends BaseObservable {
	private static final int VERSION_CODE = 2;

	public static final int ACTIVE_LAYER_CHANGED = 0;
	public static final int LAYER_ADDED = 1;
	public static final int LAYER_INSERTED = 2;
	public static final int LAYER_REMOVED = 3;
	public static final int LAYERS_SORTED = 4;
	public static final int LAYER_DATA_CHANGED = 5;

	private static final Comparator<EditorLayer> LAYER_COMPARATOR = (o1, o2) -> (int) Math.signum(o1.id - o2.id);

	private int versionCode = VERSION_CODE;

	/** Scene file, path is relative to project Vis folder */
	public String path;
	public float width;
	public float height;
	/** This value is float to avoid calculations problems */
	public float pixelsPerUnit;
	public SceneViewport viewport;

	public PhysicsSettings physicsSettings = new PhysicsSettings();
	public Variables variables = new Variables();

	private Array<EditorLayer> layers = new Array<>();
	private int activeLayerId;

	private IntMap<String> groupIds = new IntMap<>();

	private Array<EntityScheme> schemes; //for serialization

	public EditorScene (FileHandle file, SceneViewport viewport, float width, float height, int pixelsPerUnit) {
		if (width < 0 || height < 0) throw new IllegalArgumentException("Invalid scene size");
		if (pixelsPerUnit <= 0) throw new IllegalArgumentException("Pixels per units cannot be smaller or equal zero");
		this.path = file.path();
		this.viewport = viewport;
		this.width = width;
		this.height = height;
		this.pixelsPerUnit = pixelsPerUnit;

		layers.add(new EditorLayer("Background", 0));
		schemes = new Array<>();
	}

	@Override
	public void onDeserialize () {
		super.onDeserialize();
		forceSortLayers();

		if (variables == null) variables = new Variables();

		if (versionCode == 1) {
			Log.info("Scene::onDeserialize", "Updating scene " + path + " to versionCode 2");
			variables = new Variables();
			versionCode = 2;
		}
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

	public String getGroupStringId (int id) {
		return groupIds.get(id, "");
	}

	public void setGroupStringId (int id, String stringId) {
		if (stringId.equals("")) return;

		groupIds.put(id, stringId);
	}

	public IntMap<String> getGroups () {
		return groupIds;
	}

	public EditorLayer getActiveLayer () {
		for (EditorLayer layer : layers) {
			if (layer.id == activeLayerId)
				return layer;
		}

		throw new IllegalStateException("No active layer");
	}

	public int getActiveLayerId () {
		return activeLayerId;
	}

	public boolean setActiveLayer (int layerId) {
		if (this.activeLayerId != layerId) {
			this.activeLayerId = layerId;
			postNotification(ACTIVE_LAYER_CHANGED);
			return true;
		}

		return false;
	}

	public EditorLayer getLayerById (int id) {
		for (EditorLayer layer : layers) {
			if (layer.id == id) return layer;
		}

		return null;
	}

	public EditorLayer getLayerByName (String name) {
		for (EditorLayer layer : layers) {
			if (layer.name.equals(name)) return layer;
		}

		return null;
	}

	public ImmutableArray<EditorLayer> getLayers () {
		return new ImmutableArray<>(layers);
	}

	public EditorLayer addLayer (String name) {
		EditorLayer layer = new EditorLayer(name, getFreeLayerID());
		layers.add(layer);
		layers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_ADDED);
		return layer;
	}

	public void insertLayer (EditorLayer layer) {
		if (isLayerIdUsed(layer.id))
			throw new IllegalStateException("Layer with this id already exist!");

		layers.add(layer);
		layers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_INSERTED);
	}

	public boolean removeLayer (EditorLayer layer) {
		boolean result = layers.removeValue(layer, true);
		layers.sort(LAYER_COMPARATOR);
		if (layer.id == activeLayerId)
			activeLayerId = layers.first().id;

		postNotification(LAYER_REMOVED);
		return result;
	}

	public void forceSortLayers () {
		layers.sort(LAYER_COMPARATOR);
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
		for (EditorLayer layer : layers) {
			if (layer.id == id)
				return true;
		}

		return false;
	}
}
