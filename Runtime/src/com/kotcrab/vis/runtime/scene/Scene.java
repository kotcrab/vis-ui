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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.system.*;
import com.kotcrab.vis.runtime.util.ArtemisUtils;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Base class of VisRuntime scene system. Scene are typically constructed using {@link VisAssetManager} with {@link SceneLoader}
 * @author Kotcrab
 */
public class Scene {

	private Viewport viewport;

	private EntityEngine engine;
	private final RuntimeContext context;

	/** Used by framework, not indented for external use */
	public Scene (RuntimeContext context, SceneViewport viewportType, int width, int height) {
		this.context = context;
		AssetManager assetsManager = context.assetsManager;
		RuntimeConfiguration configuration = context.configuration;

		OrthographicCamera camera = new OrthographicCamera(width, height);
		camera.position.x = width / 2;
		camera.position.y = height / 2;
		camera.update();

		switch (viewportType) {
			case STRETCH:
				viewport = new StretchViewport(width, height, camera);
				break;
			case FIT:
				viewport = new FitViewport(width, height, camera);
				break;
			case FILL:
				viewport = new FillViewport(width, height, camera);
				break;
			case SCREEN:
				viewport = new ScreenViewport(camera);
				break;
			case EXTEND:
				viewport = new ExtendViewport(width, height, camera);
				break;
		}

		ShaderProgram distanceFieldShader = null;
		if (assetsManager.isLoaded(SceneLoader.DISTANCE_FIELD_SHADER)) {
			distanceFieldShader = assetsManager.get(SceneLoader.DISTANCE_FIELD_SHADER, ShaderProgram.class);
		}

		engine = new EntityEngine();
		engine.setManager(new CameraManager(viewportType, width, height));
		engine.setSystem(new SpriteInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new SoundInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new MusicInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new ParticleInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new TextInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);

		ArtemisUtils.createCommonSystems(engine, context.batch, distanceFieldShader, true);
		RenderBatchingSystem renderBatchingSystem = engine.getSystem(RenderBatchingSystem.class);
		engine.setSystem(new ParticleRenderSystem(renderBatchingSystem, false), true);

		for (EntitySupport support : context.supports)
			support.registerSystems(engine);

		engine.initialize();
	}

	/** Renders entire scene. Typically called from {@link ApplicationListener#render()} */
	public void render (SpriteBatch batch) {
		engine.setDelta(Gdx.graphics.getDeltaTime());
		engine.process();
	}

	/** Must by called when screen was resized. Typically called from {@link ApplicationListener#resize(int, int)} */
	public void resize (int width, int height) {
		viewport.update(width, height);
		engine.getManager(CameraManager.class).resize(width, height);
	}

	public EntityEngine getEntityEngine () {
		return engine;
	}
}
