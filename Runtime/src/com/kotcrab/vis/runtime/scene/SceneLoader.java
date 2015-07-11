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

import com.artemis.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.data.ECSEntityData;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.font.BitmapFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Scene loader for {@link AssetManager}. Allow to load entire scene file with all required dependencies such as textures, sounds etc.
 * @author Kotcrab
 */
public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneParameter> {
	public static final String DISTANCE_FIELD_SHADER = "com/kotcrab/vis/runtime/bmp-font-df";

	private RuntimeConfiguration configuration;
	private SceneData data;
	private Scene scene;

	private boolean distanceFieldShaderLoaded;
	private FontProvider bmpFontProvider;
	private FontProvider ttfFontProvider;

	private ObjectMap<Class, EntitySupport> supportMap = new ObjectMap<Class, EntitySupport>();

	private Batch batch = new SpriteBatch();

	public SceneLoader () {
		this(new InternalFileHandleResolver(), new RuntimeConfiguration());
	}

	public SceneLoader (RuntimeConfiguration configuration) {
		this(new InternalFileHandleResolver(), configuration);
	}

	public SceneLoader (FileHandleResolver resolver, RuntimeConfiguration configuration) {
		super(resolver);
		this.configuration = configuration;
		bmpFontProvider = new BitmapFontProvider();
	}

	public static Json getJson () {
		Json json = new Json();
		json.addClassTag("SceneData", SceneData.class);
		json.addClassTag("AtlasRegionAsset", AtlasRegionAsset.class);
		json.addClassTag("TextureRegionAsset", TextureRegionAsset.class);
		return json;
	}

	public void registerSupport (AssetManager manager, EntitySupport support) {
		supportMap.put(support.getEntityDataClass(), support);
		support.setLoaders(manager);
	}

	public void enableFreeType (AssetManager manager, FontProvider fontProvider) {
		this.ttfFontProvider = fontProvider;
		fontProvider.setLoaders(manager);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SceneParameter parameter) {
		Json json = getJson();
		data = json.fromJson(SceneData.class, file);

		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();

		loadDependencies(deps, data.entities);

		return deps;
	}

	private void loadDependencies (Array<AssetDescriptor> dependencies, Array<ECSEntityData> entities) {
		for (ECSEntityData entityData : entities) {
			for (Component component : entityData.components) {
				if (component instanceof AssetComponent) {
					VisAssetDescriptor asset = ((AssetComponent) component).asset;

					if (asset instanceof TextureRegionAsset) {
						dependencies.add(new AssetDescriptor<TextureAtlas>("gfx/textures.atlas", TextureAtlas.class));

					} else if (asset instanceof AtlasRegionAsset) {
						AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
						dependencies.add(new AssetDescriptor<TextureAtlas>(regionAsset.getPath(), TextureAtlas.class));

					} else if (asset instanceof BmpFontAsset) {
						checkShader(dependencies);
						bmpFontProvider.load(dependencies, asset);
					} else if (asset instanceof TtfFontAsset) {
						ttfFontProvider.load(dependencies, asset);
					} else if (asset instanceof PathAsset) {
						PathAsset pathAsset = (PathAsset) asset;
						String path = pathAsset.getPath();

						if (path.startsWith("sound/")) dependencies.add(new AssetDescriptor<Sound>(path, Sound.class));
						if (path.startsWith("music/")) dependencies.add(new AssetDescriptor<Music>(path, Music.class));
						if (path.startsWith("particle/"))
							dependencies.add(new AssetDescriptor<ParticleEffect>(path, ParticleEffect.class));

					} else {
						throw new UnsupportedAssetDescriptorException(asset);
					}
				}
			}
		}
	}

	@Deprecated
	private void loadDepsForEntities (Array<AssetDescriptor> deps, Array<EntityData> entities) {
		for (EntityData entityData : entities) {

//			if (entityData instanceof TextData) {
//				TextData textData = (TextData) entityData;
//
//				if (textData.isTrueType)
//					ttfFontProvider.load(deps, textData);
//				else {
//					checkShader(deps);
//					bmpFontProvider.load(deps, textData);
//				}
//
//				continue;
//			}

			EntitySupport support = supportMap.get(entityData.getClass());

			if (support == null)
				throw new IllegalStateException("Missing support for entity class: " + entityData.getClass());

			support.resolveDependencies(deps, entityData);
		}
	}

	private void checkShader (Array<AssetDescriptor> dependencies) {
		if (distanceFieldShaderLoaded == false)
			dependencies.add(new AssetDescriptor<ShaderProgram>(Gdx.files.classpath(DISTANCE_FIELD_SHADER), ShaderProgram.class));

		distanceFieldShaderLoaded = true;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SceneParameter parameter) {
		Array<Entity> entities = new Array<Entity>();
		Array<TextureAtlas> atlases = new Array<TextureAtlas>();

		scene = new Scene(entities, atlases, batch, configuration, manager, data.viewport, data.width, data.height);

		EntityEngine engine = scene.getEntityEngine();
		for (ECSEntityData entityData : data.entities)
			entityData.build(engine);

//		if (distanceFieldShaderLoaded)
//			scene.getDistanceFieldShaderFromManager(distanceFieldShader);
	}

	@Deprecated
	private void loadEntitiesFromData (AssetManager manager, Array<TextureAtlas> atlases, Array<EntityData> datas, Array<Entity> entities) {
		for (EntityData entityData : datas) {
//			if (entityData instanceof TextData) {
//				TextData textData = (TextData) entityData;
//
//				BitmapFont font;
//
//				if (textData.isTrueType)
//					font = manager.get(textData.arbitraryFontName, BitmapFont.class);
//				else {
//					font = resolveAsset(manager, textData.assetDescriptor, BitmapFont.class);
//				}
//
//				TextEntity entity = new TextEntity(textData.id, font, textData.text, textData.fontSize);
//				textData.loadTo(entity);
//				entities.add(entity);
//				continue;
//			}

			EntitySupport support = supportMap.get(entityData.getClass());
			if (support != null)
				entities.add(supportMap.get(entityData.getClass()).getInstanceFromData(manager, entityData));
		}
	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Scene scene = this.scene;
		this.scene = null;

//		for (LayerData layer : data.layers) {
//			for (EntityData entityData : layer.entities) {
//				if (entityData instanceof ParticleEffectData) {
//					ParticleEffectData particleData = (ParticleEffectData) entityData;
//					PathAsset path = (PathAsset) particleData.assetDescriptor;
//
//					FileHandle effectFile = resolve(path.getPath());
//					ParticleEffect emitter = new ParticleEffect();
//					emitter.load(effectFile, effectFile.parent());
//
//					ParticleEffectEntity entity = new ParticleEffectEntity(particleData.id, emitter);
//					particleData.loadTo(entity);
//					scene.getEntities().add(entity);
//				}
//			}
//		}

		return scene;
	}

	private <T> T resolveAsset (AssetManager manager, VisAssetDescriptor assetDescriptor, Class<? extends T> clazz) {
		if (assetDescriptor instanceof PathAsset == false)
			throw new UnsupportedOperationException("Cannot resolve path for asset descriptor: " + assetDescriptor);

		PathAsset path = (PathAsset) assetDescriptor;
		return manager.get(path.getPath(), clazz);
	}

	static public class SceneParameter extends AssetLoaderParameters<Scene> {
	}
}
