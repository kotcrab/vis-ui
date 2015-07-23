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
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.util.BaseObservable;
import com.kotcrab.vis.runtime.scene.SceneViewport;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.util.Comparator;

/**
 * Editor scene class, serialized by Kryo
 * @author Kotcrab
 */
public class EditorScene extends BaseObservable {
	public static final int ACTIVE_LAYER_CHANGED = 0;
	public static final int LAYER_ADDED = 1;
	public static final int LAYER_INSERTED = 2;
	public static final int LAYER_REMOVED = 3;
	public static final int LAYERS_SORTED = 4;

	private static final Comparator<Layer> LAYER_COMPARATOR = (o1, o2) -> (int) Math.signum(o1.id - o2.id);

	/** Scene file, path is relative to project Vis folder */
	@Tag(0) public String path;
	@Tag(1) public float width;
	@Tag(2) public float height;
	@Tag(7) public float pixelsPerUnit; //this value is float to avoid calculations problems
	@Tag(3) public SceneViewport viewport;

	@Tag(4) private Array<Layer> layers = new Array<>();
	@Tag(5) private int activeLayerId;

	@Tag(6) private Array<EntityScheme> schemes; //for serialization
	//last tag is 7

	public EditorScene (FileHandle file, SceneViewport viewport, float width, float height, int pixelsPerUnit) {
		if (width < 0 || height < 0) throw new IllegalArgumentException("Invalid scene size");
		if (pixelsPerUnit <= 0) throw new IllegalArgumentException("Pixels per units cannot be smaler or equal zero");
		this.path = file.path();
		this.viewport = viewport;
		this.width = width;
		this.height = height;
		this.pixelsPerUnit = pixelsPerUnit;

		layers.add(new Layer("Background", 0));
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

	public Layer getActiveLayer () {
		for (Layer layer : layers) {
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

	public Layer getLayerById (int id) {
		for (Layer layer : layers) {
			if (layer.id == id) return layer;
		}

		return null;
	}

	public Layer getLayerByName (String name) {
		for (Layer layer : layers) {
			if (layer.name.equals(name)) return layer;
		}

		return null;
	}

	public ImmutableArray<Layer> getLayers () {
		return new ImmutableArray<>(layers);
	}

	public Layer addLayer (String name) {
		Layer layer = new Layer(name, getFreeLayerID());
		layers.add(layer);
		layers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_ADDED);
		return layer;
	}

	public void insertLayer (Layer layer) {
		if (isLayerIdUsed(layer.id))
			throw new IllegalStateException("Layer with this id already exist!");

		layers.add(layer);
		layers.sort(LAYER_COMPARATOR);
		postNotification(LAYER_INSERTED);
	}

	public boolean removeLayer (Layer layer) {
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
		for (Layer layer : layers) {
			if (layer.id == id)
				return true;
		}

		return false;
	}
}
