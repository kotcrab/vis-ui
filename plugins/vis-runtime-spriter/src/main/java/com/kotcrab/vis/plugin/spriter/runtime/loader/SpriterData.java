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

package com.kotcrab.vis.plugin.spriter.runtime.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Loader;

/**
 * Loaded by {@link AssetManager}
 * @author Kotcrab
 */
public class SpriterData {
	public final Data data;
	public final Loader<Sprite> loader;

	public SpriterData (Data data, Loader<Sprite> loader) {
		this.data = data;
		this.loader = loader;
	}
}
