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
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.entity.TextEntity;
import com.kotcrab.vis.runtime.system.CameraManager;
import com.kotcrab.vis.runtime.system.MusicInflaterSystem;
import com.kotcrab.vis.runtime.system.SoundInflaterSystem;
import com.kotcrab.vis.runtime.system.SpriteInflaterSystem;
import com.kotcrab.vis.runtime.util.ArtemisUtils;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Base class of VisRuntime scene system. Scene are typically constructed using {@link VisAssetManager} with {@link SceneLoader}
 * @author Kotcrab
 */
public class Scene implements Disposable {
	private AssetManager assetManager;
	private ShaderProgram distanceFieldShader;

	private OrthographicCamera camera;
	private Viewport viewport;

	@Deprecated
	private Array<Entity> entities;
	@Deprecated
	private Array<TextureAtlas> textureAtlases;

	private EntityEngine engine;

	/** Used by framework, not indented for external use */
	public Scene (Array<Entity> entities, Array<TextureAtlas> textureAtlases, Batch batch, RuntimeConfiguration configuration, AssetManager assetsManager, SceneViewport viewportType, int width, int height) {
		this.entities = entities;
		this.textureAtlases = textureAtlases;
		this.assetManager = assetsManager;

		camera = new OrthographicCamera(width, height);
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

		engine = new EntityEngine();
		engine.setManager(new CameraManager(viewportType, width, height));
		engine.setSystem(new SpriteInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new SoundInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		engine.setSystem(new MusicInflaterSystem(configuration, assetsManager), configuration.passiveInflaters);
		ArtemisUtils.createCommonSystems(engine, batch, true);
		engine.initialize();
	}

	/** Renders entire scene. Typically called from {@link ApplicationListener#render()} */
	public void render (SpriteBatch batch) {
		engine.setDelta(Gdx.graphics.getDeltaTime());
		engine.process();

		batch.setProjectionMatrix(camera.combined);

		boolean shader = false;

		batch.begin();

		for (Entity e : entities) {
			if (e instanceof TextEntity && ((TextEntity) e).isDistanceFieldShaderEnabled()) {
				shader = true;
				batch.setShader(distanceFieldShader);
			}

			e.render(batch);

			if (shader) batch.setShader(null);
		}

		batch.end();
	}

	/** Must by called when screen was resized. Typically called from {@link ApplicationListener#resize(int, int)} */
	public void resize (int width, int height) {
		viewport.update(width, height);
		engine.getManager(CameraManager.class).resize(width, height);
	}

	void getDistanceFieldShaderFromManager (FileHandle shader) {
		distanceFieldShader = (ShaderProgram) assetManager.get(new AssetDescriptor(shader, ShaderProgram.class));
	}

	@Override
	public void dispose () {
		for (Entity entity : entities) {
			if (entity instanceof Disposable) {
				Disposable disposable = (Disposable) entity;
				disposable.dispose();
			}
		}
	}

	public EntityEngine getEntityEngine () {
		return engine;
	}
}
