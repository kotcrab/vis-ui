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

package com.kotcrab.vis.runtime;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.Scene;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/**
 * Stores various runtime object that are common for loaded {@link Scene}s.
 * @author Kotcrab
 */
public class RuntimeContext {
	public RuntimeConfiguration configuration;
	public Batch batch;
	public AssetManager assetsManager;
	public ImmutableArray<EntitySupport> supports;

	public RuntimeContext (RuntimeConfiguration configuration, Batch batch, AssetManager assetsManager, ImmutableArray<EntitySupport> supports) {
		this.configuration = configuration;
		this.batch = batch;
		this.assetsManager = assetsManager;
		this.supports = supports;
	}
}
