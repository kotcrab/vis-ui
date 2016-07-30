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

import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.component.Variables;
import com.kotcrab.vis.runtime.data.LayerData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneConfig.ConfigElement;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.system.CameraManager;
import com.kotcrab.vis.runtime.util.AfterSceneInit;
import com.kotcrab.vis.runtime.util.BootstrapInvocationStrategy;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * Base class of VisRuntime scene system. Scene are typically constructed using {@link VisAssetManager} with {@link SceneLoader}.
 * @author Kotcrab
 */
public class Scene implements Disposable {
	private CameraManager cameraManager;
	private EntityEngine engine;

	private Variables variables;
	private Array<LayerData> layerData;
	private float pixelsPerUnit;
	private float width;
	private float height;

	/** Used by framework, not indented for external use */
	public Scene (RuntimeContext context, SceneData data, SceneParameter parameter) {
		layerData = data.layers;
		variables = data.variables;
		pixelsPerUnit = data.pixelsPerUnit;
		width = data.width;
		height = data.height;

		EntityEngineConfiguration engineConfig = new EntityEngineConfiguration();

		if (parameter == null) parameter = new SceneParameter();
		SceneConfig config = parameter.config;
		config.sort();

		if (parameter.respectScenePhysicsSettings) {
			if (data.physicsSettings.physicsEnabled) {
				config.enable(SceneFeatureGroup.PHYSICS);
			} else {
				config.disable(SceneFeatureGroup.PHYSICS);
			}
		}

		for (EntitySupport support : context.supports) {
			support.registerSceneSystems(config);
		}

		for (ConfigElement element : config.getConfigElements()) {
			if (element.disabled) continue;
			engineConfig.setSystem(element.provider.create(engineConfig, context, data));
		}

		cameraManager = engineConfig.getSystem(CameraManager.class);

		engine = new EntityEngine(engineConfig);
	}

	/**
	 * Finishes loading scene and inflate all entities. This must be called manually if scene wasn't loaded
	 * using {@link VisAssetManager} loadSceneNow methods.
	 */
	public void init () {
		engine.setInvocationStrategy(new BootstrapInvocationStrategy());
		engine.process();
		engine.setInvocationStrategy(new InvocationStrategy());

		for (BaseSystem system : engine.getSystems()) {
			if (system instanceof AfterSceneInit) {
				((AfterSceneInit) system).afterSceneInit();
			}
		}
	}

	/** Updates and renders entire scene. Typically called from {@link ApplicationListener#render()} */
	public void render () {
		engine.setDelta(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		engine.process();
	}

	/** Must by called when screen was resized. Typically called from {@link ApplicationListener#resize(int, int)} */
	public void resize (int width, int height) {
		cameraManager.resize(width, height);
	}

	public Array<LayerData> getLayerData () {
		return layerData;
	}

	public Variables getSceneVariables () {
		return variables;
	}

	public LayerData getLayerDataByName (String name) {
		for (LayerData data : layerData) {
			if (data.name.equals(name)) return data;
		}

		return null;
	}

	public float getPixelsPerUnit () {
		return pixelsPerUnit;
	}

	public float getSceneWidth () {
		return width;
	}

	public float getSceneHeight () {
		return height;
	}

	public EntityEngine getEntityEngine () {
		return engine;
	}

	@Override
	public void dispose () {
		engine.dispose();
	}
}
