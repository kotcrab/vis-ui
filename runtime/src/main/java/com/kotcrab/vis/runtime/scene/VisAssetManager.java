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

package com.kotcrab.vis.runtime.scene;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.font.FreeTypeFontProvider;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.util.ShaderLoader;

/**
 * Simplified {@link AssetManager} for VisRuntime. Automatically sets AssetManger required loaders, and allows to
 * enable FreeType support or load custom entity supports in easier way.
 * @author Kotcrab
 */
public class VisAssetManager extends AssetManager {
	private SceneLoader sceneLoader;

	public VisAssetManager (Batch batch) {
		this(new InternalFileHandleResolver(), batch);
	}

	public VisAssetManager (FileHandleResolver resolver, Batch batch) {
		super(resolver);
		if (batch == null) throw new IllegalStateException("Batch cannot be null");
		sceneLoader = new SceneLoader(resolver, new RuntimeConfiguration());
		sceneLoader.setBatch(batch);
		setLoader(Scene.class, sceneLoader);
		setLoader(ShaderProgram.class, new ShaderLoader());
	}

	public SceneLoader getSceneLoader () {
		return sceneLoader;
	}

	/**
	 * Allows to enable FreeType support.
	 * @param freeTypeFontProvider must be instance of {@link FreeTypeFontProvider}. Note that this parameter is not checked!
	 */
	public void enableFreeType (FontProvider freeTypeFontProvider) {
		if (freeTypeFontProvider != null) sceneLoader.enableFreeType(this, freeTypeFontProvider);
	}

	/**
	 * Allows to register custom entity supports
	 * @param support instance of {@link EntitySupport} that will support your custom entity
	 */
	public void registerSupport (EntitySupport support) {
		sceneLoader.registerSupport(this, support);
	}

	/** Quickest and easiest way to load scene. This method will block until entire scene is loaded. */
	public Scene loadSceneNow (String scenePath) {
		return loadSceneNow(scenePath, null);
	}

	public Scene loadSceneNow (String scenePath, SceneParameter parameter) {
		load(scenePath, Scene.class, parameter);
		finishLoading();
		Scene scene = get(scenePath, Scene.class);
		scene.init();
		return scene;
	}

}
