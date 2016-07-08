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

package com.kotcrab.vis.runtime.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.runtime.scene.Scene;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import com.kotcrab.vis.runtime.scene.SceneViewport;

/**
 * Scene data, used to build {@link Scene} by {@link SceneLoader}.
 * @author Kotcrab
 */
public class SceneData {
	public SceneViewport viewport;
	public float width;
	public float height;
	public float pixelsPerUnit;
	public String textureAtlasPath;

	public PhysicsSettings physicsSettings;
	public Variables variables;

	public IntMap<String> groupIds;

	public Array<LayerData> layers = new Array<LayerData>();

	public Array<EntityData> entities = new Array<EntityData>();
}
