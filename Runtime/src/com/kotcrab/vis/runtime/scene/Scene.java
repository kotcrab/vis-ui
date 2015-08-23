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
import com.artemis.Manager;
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

		engineConfig.setManager(cameraManager = new CameraManager(data.viewport, data.width, data.height, data.pixelsPerUnit));
		engineConfig.setManager(new VisIDManager());

		if (runtimeConfig.useVisGroupManager) engineConfig.setManager(new VisGroupManager(data.groupIds));

		engineConfig.setManager(new LayerManager(data.layers));

		engineConfig.setManager(new SpriteInflater(runtimeConfig, assetsManager));
		engineConfig.setManager(new SoundInflater(runtimeConfig, assetsManager));
		engineConfig.setManager(new MusicInflater(runtimeConfig, assetsManager));
		engineConfig.setManager(new ParticleInflater(runtimeConfig, assetsManager, data.pixelsPerUnit));
		engineConfig.setManager(new TextInflater(runtimeConfig, assetsManager, data.pixelsPerUnit));
		engineConfig.setManager(new ShaderInflater(assetsManager));

		if (parameter != null) {
			for (BaseSystem system : parameter.systems)
				engineConfig.setSystem(system);

			for (BaseSystem system : parameter.passiveSystems)
				engineConfig.setSystem(system, true);

			for (Manager manager : parameter.managers)
				engineConfig.setManager(manager);
		}

		if (data.physicsSettings.physicsEnabled) {
			engineConfig.setSystem(new PhysicsSystem(data.physicsSettings));
			engineConfig.setManager(new PhysicsBodyManager());
			if (runtimeConfig.useBox2dSpriteUpdateSystem) engineConfig.setSystem(new PhysicsSpriteUpdateSystem());
		}

		ArtemisUtils.createCommonSystems(engineConfig, context.batch, distanceFieldShader, false);
		engineConfig.setSystem(new ParticleRenderSystem(engineConfig.getSystem(RenderBatchingSystem.class), false), true);

		if (data.physicsSettings.physicsEnabled && runtimeConfig.useBox2dDebugRenderer)
			engineConfig.setSystem(new Box2dDebugRenderSystem());

		for (EntitySupport support : context.supports) {
			support.registerSystems(runtimeConfig, engineConfig, assetsManager);
		}

		engine = new EntityEngine(engineConfig);
	}

	/** Called by framework right after loading scene to finish loading scene and inflate all entities */
	public void init () {
		engine.setInvocationStrategy(new BootstrapInvocationStrategy());
		engine.process();
		engine.setInvocationStrategy(new InvocationStrategy());

		for (BaseSystem system : engine.getSystems()) {
			if (system instanceof AfterSceneInit) {
				((AfterSceneInit) system).afterSceneInit();
			}
		}

		for (Manager manager : engine.getManagers()) {
			if (manager instanceof AfterSceneInit) {
				((AfterSceneInit) manager).afterSceneInit();
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
