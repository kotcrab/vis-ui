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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.data.*;
import com.kotcrab.vis.runtime.entity.*;
import com.kotcrab.vis.runtime.font.BmpFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Scene loader for {@link AssetManager}. Allow to load entire scene file with all required dependencies such as textures, sounds etc.
 * @author Kotcrab
 */
public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneParameter> {
	private static final FileHandle distanceFieldShader = Gdx.files.classpath("com/kotcrab/vis/runtime/bmp-font-df");

	private SceneData data;
	private Scene scene;

	private boolean distanceFieldShaderLoaded;
	private FontProvider bmpFontProvider;
	private FontProvider ttfFontProvider;

	private ObjectMap<Class, EntitySupport> supportMap = new ObjectMap<Class, EntitySupport>();

	public SceneLoader () {
		this(new InternalFileHandleResolver());
	}

	public SceneLoader (FileHandleResolver resolver) {
		super(resolver);
		bmpFontProvider = new BmpFontProvider();
	}

	public static Json getJson () {
		Json json = new Json();
		json.addClassTag("SceneData", SceneData.class);
		json.addClassTag("SpriteData", SpriteData.class);
		json.addClassTag("TextData", TextData.class);
		json.addClassTag("ParticleEffectData", ParticleEffectData.class);
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

		for (LayerData layer : data.layers) {
			loadDepsForEntities(deps, layer.entities);
		}

		return deps;
	}

	private void loadDepsForEntities (Array<AssetDescriptor> deps, Array<EntityData> entities) {
		for (EntityData entityData : entities) {
			if (entityData instanceof EntityGroupData) {
				EntityGroupData groupData = (EntityGroupData) entityData;
				loadDepsForEntities(deps, groupData.entities);
				continue;
			}

			if (entityData instanceof SpriteData) {
				SpriteData spriteData = (SpriteData) entityData;
				VisAssetDescriptor asset = spriteData.assetDescriptor;

				if (asset instanceof TextureRegionAsset) {
					TextureRegionAsset regionAsset = (TextureRegionAsset) asset;
					deps.add(new AssetDescriptor("gfx/textures.atlas", TextureAtlas.class));

				} else if (asset instanceof AtlasRegionAsset) {
					AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
					deps.add(new AssetDescriptor(((PathAsset) spriteData.assetDescriptor).getPath(), TextureAtlas.class));

				} else {
					throw new UnsupportedAssetDescriptorException(asset);
				}

				continue;
			}

			if (entityData instanceof TextData) {
				TextData textData = (TextData) entityData;

				if (textData.isTrueType)
					ttfFontProvider.load(deps, textData);
				else {
					checkShader(deps);
					bmpFontProvider.load(deps, textData);
				}

				continue;
			}

			if (entityData instanceof MusicData) {
				MusicData musicData = (MusicData) entityData;
				PathAsset path = (PathAsset) musicData.assetDescriptor;
				deps.add(new AssetDescriptor(path.getPath(), Music.class));
				continue;
			}

			if (entityData instanceof SoundData) {
				SoundData musicData = (SoundData) entityData;
				PathAsset path = (PathAsset) musicData.assetDescriptor;
				deps.add(new AssetDescriptor(path.getPath(), Sound.class));
				continue;
			}

			if (entityData instanceof ParticleEffectData)
				continue;

			EntitySupport support = supportMap.get(entityData.getClass());

			if (support == null)
				throw new IllegalStateException("Missing support for entity class: " + entityData.getClass());

			support.resolveDependencies(deps, entityData);
		}
	}

	private void checkShader (Array<AssetDescriptor> deps) {
		if (distanceFieldShaderLoaded == false)
			deps.add(new AssetDescriptor(distanceFieldShader, ShaderProgram.class));

		distanceFieldShaderLoaded = true;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SceneParameter parameter) {
		Array<Entity> entities = new Array<Entity>();
		Array<TextureAtlas> atlases = new Array<TextureAtlas>();

		scene = new Scene(entities, atlases, manager, data.viewport, data.width, data.height);

		for (LayerData layer : data.layers) {
			loadEntitiesFromData(manager, atlases, layer.entities, entities);
		}

		if (distanceFieldShaderLoaded)
			scene.getDistanceFieldShaderFromManager(distanceFieldShader);
	}

	private void loadEntitiesFromData (AssetManager manager, Array<TextureAtlas> atlases, Array<EntityData> datas, Array<Entity> entities) {
		for (EntityData entityData : datas) {
			if (entityData instanceof EntityGroupData) {
				EntityGroupData groupData = (EntityGroupData) entityData;

				EntityGroup group = new EntityGroup(groupData.id);
				loadEntitiesFromData(manager, atlases, groupData.entities, group.getEntities());
				entities.add(group);
				continue;
			}

			if (entityData instanceof SpriteData) {
				SpriteData spriteData = (SpriteData) entityData;

				VisAssetDescriptor asset = spriteData.assetDescriptor;

				String atlasPath;
				String atlasRegion;

				if (asset instanceof TextureRegionAsset) {
					TextureRegionAsset regionAsset = (TextureRegionAsset) asset;
					atlasPath = "gfx/textures.atlas";
					atlasRegion = regionAsset.getPath().substring(4, regionAsset.getPath().length() - 4); //remove gfx/ and file extension

				} else if (asset instanceof AtlasRegionAsset) {
					AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
					atlasPath = regionAsset.getPath();
					atlasRegion = regionAsset.getRegionName();

				} else {
					throw new UnsupportedAssetDescriptorException(asset);
				}

				TextureAtlas atlas = manager.get(atlasPath, TextureAtlas.class);
				if (atlases.contains(atlas, true) == false) atlases.add(atlas);

				SpriteEntity entity = new SpriteEntity(entityData.id, new Sprite(atlas.findRegion(atlasRegion)));
				spriteData.loadTo(entity);

				entities.add(entity);
				continue;
			}

			if (entityData instanceof TextData) {
				TextData textData = (TextData) entityData;

				BitmapFont font;

				if (textData.isTrueType)
					font = manager.get(textData.arbitraryFontName, BitmapFont.class);
				else {
					font = resolveAsset(manager, textData.assetDescriptor, BitmapFont.class);
				}

				TextEntity entity = new TextEntity(textData.id, font, textData.text, textData.fontSize);
				textData.loadTo(entity);
				entities.add(entity);
				continue;
			}

			if (entityData instanceof MusicData) {
				MusicData musicData = (MusicData) entityData;
				MusicEntity entity = new MusicEntity(musicData.id, resolveAsset(manager, musicData.assetDescriptor, Music.class));
				musicData.loadTo(entity);
				entities.add(entity);
				continue;
			}

			if (entityData instanceof SoundData) {
				SoundData soundData = (SoundData) entityData;
				SoundEntity entity = new SoundEntity(soundData.id, resolveAsset(manager, soundData.assetDescriptor, Sound.class));
				soundData.loadTo(entity);
				entities.add(entity);
				continue;
			}

			EntitySupport support = supportMap.get(entityData.getClass());
			if (support != null)
				entities.add(supportMap.get(entityData.getClass()).getInstanceFromData(manager, entityData));
		}
	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Scene scene = this.scene;
		this.scene = null;

		for (LayerData layer : data.layers) {
			for (EntityData entityData : layer.entities) {
				if (entityData instanceof ParticleEffectData) {
					ParticleEffectData particleData = (ParticleEffectData) entityData;
					PathAsset path = (PathAsset) particleData.assetDescriptor;

					FileHandle effectFile = resolve(path.getPath());
					ParticleEffect emitter = new ParticleEffect();
					emitter.load(effectFile, effectFile.parent());

					ParticleEffectEntity entity = new ParticleEffectEntity(particleData.id, emitter);
					particleData.loadTo(entity);
					scene.getEntities().add(entity);
				}
			}
		}

		scene.onAfterLoad();

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
