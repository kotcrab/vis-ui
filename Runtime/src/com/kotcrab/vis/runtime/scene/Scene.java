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

package com.kotcrab.vis.runtime.scene;

import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.LayerData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.system.*;
import com.kotcrab.vis.runtime.system.inflater.*;
import com.kotcrab.vis.runtime.system.physics.Box2dDebugRenderSystem;
import com.kotcrab.vis.runtime.system.physics.PhysicsBodyManager;
import com.kotcrab.vis.runtime.system.physics.PhysicsSpriteUpdateSystem;
import com.kotcrab.vis.runtime.system.physics.PhysicsSystem;
import com.kotcrab.vis.runtime.system.render.*;
import com.kotcrab.vis.runtime.util.*;

/**
 * Base class of VisRuntime scene system. Scene are typically constructed using {@link VisAssetManager} with {@link SceneLoader}
 * @author Kotcrab
 */
public class Scene {
	private CameraManager cameraManager;
	private EntityEngine engine;

	private Array<LayerData> layerData;

	/** Used by framework, not indented for external use */
	public Scene (RuntimeContext context, SceneData data, SceneParameter parameter) {
		layerData = data.layers;

		AssetManager assetsManager = context.assetsManager;
		RuntimeConfiguration runtimeConfig = context.configuration;

		ShaderProgram distanceFieldShader = null;
		if (assetsManager.isLoaded(SceneLoader.DISTANCE_FIELD_SHADER)) {
			distanceFieldShader = assetsManager.get(SceneLoader.DISTANCE_FIELD_SHADER, ShaderProgram.class);
		}

		EntityEngineConfiguration engineConfig = new EntityEngineConfiguration();

		engineConfig.setSystem(cameraManager = new CameraManager(data.viewport, data.width, data.height, data.pixelsPerUnit));
		engineConfig.setSystem(new VisIDManager());
		if (runtimeConfig.useVisGroupManager) engineConfig.setSystem(new VisGroupManager(data.groupIds));
		engineConfig.setSystem(new LayerManager(data.layers));

		engineConfig.setSystem(new VisSpriteInflater(runtimeConfig, assetsManager));
		engineConfig.setSystem(new SoundInflater(runtimeConfig, assetsManager));
		engineConfig.setSystem(new MusicInflater(runtimeConfig, assetsManager));
		engineConfig.setSystem(new ParticleInflater(runtimeConfig, assetsManager, data.pixelsPerUnit));
		engineConfig.setSystem(new TextInflater(runtimeConfig, assetsManager, data.pixelsPerUnit));
		engineConfig.setSystem(new ShaderInflater(assetsManager));
		engineConfig.setSystem(new SpriterInflater(assetsManager));

		if (parameter != null) {
			for (BaseSystem system : parameter.systems) {
				engineConfig.setSystem(system);
			}
		}

		if (data.physicsSettings.physicsEnabled) {
			engineConfig.setSystem(new PhysicsSystem(data.physicsSettings));
			engineConfig.setSystem(new PhysicsBodyManager(context.configuration));
			if (runtimeConfig.useBox2dSpriteUpdateSystem) engineConfig.setSystem(new PhysicsSpriteUpdateSystem());
		}

		engineConfig.setSystem(new TextUpdateSystem());

		RenderBatchingSystem batchingSystem = new RenderBatchingSystem(context.batch, false);
		engineConfig.setSystem(batchingSystem);

		//common render systems
		engineConfig.setSystem(new SpriteRenderSystem(batchingSystem));
		engineConfig.setSystem(new TextRenderSystem(batchingSystem, distanceFieldShader));
		engineConfig.setSystem(new SpriterRenderSystem(batchingSystem));

		engineConfig.setSystem(new ParticleRenderSystem(engineConfig.getSystem(RenderBatchingSystem.class), false));

		if (data.physicsSettings.physicsEnabled && runtimeConfig.useBox2dDebugRenderer) {
			engineConfig.setSystem(new Box2dDebugRenderSystem());
		}

		for (EntitySupport support : context.supports) {
			support.registerSystems(runtimeConfig, engineConfig, assetsManager);
		}

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

	public LayerData getLayerDataByName (String name) {
		for (LayerData data : layerData) {
			if (data.name.equals(name)) return data;
		}

		return null;
	}

	public EntityEngine getEntityEngine () {
		return engine;
	}
}
