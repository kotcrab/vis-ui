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

package com.kotcrab.vis.runtime;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.scene.Scene;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import com.kotcrab.vis.runtime.scene.ShaderLoader;

public class VisAssetManager extends AssetManager {
	public VisAssetManager () {
	}

	public VisAssetManager (FileHandleResolver resolver) {
		super(resolver);
	}

	public void setVisLoaders () {
		setVisLoaders(null);
	}

	public void setVisLoaders (FontProvider freeTypeFontProvider) {
		SceneLoader sceneLoader = new SceneLoader();

		if (freeTypeFontProvider != null) sceneLoader.enableFreeType(this, freeTypeFontProvider);

		setLoader(Scene.class, sceneLoader);
		setLoader(ShaderProgram.class, new ShaderLoader());
	}

	public Scene loadSceneNow (String scenePath) {
		load(scenePath, Scene.class);
		finishLoading();
		return get(scenePath, Scene.class);
	}
}
