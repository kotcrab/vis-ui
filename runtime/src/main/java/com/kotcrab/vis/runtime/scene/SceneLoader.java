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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.runtime.RuntimeConfiguration;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.assets.*;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.proto.ProtoShader;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.font.BitmapFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.json.LibgdxJsonTagRegistrar;
import com.kotcrab.vis.runtime.util.json.RuntimeJsonTags;

/**
 * Scene loader for {@link AssetManager}. Allow to load entire scene file with all required dependencies such as textures, sounds etc.
 * @author Kotcrab
 */
public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneParameter> {
	public static final String DISTANCE_FIELD_SHADER = "com/kotcrab/vis/runtime/bmp-font-df";

	private RuntimeConfiguration configuration;
	private SceneData data;

	private boolean distanceFieldShaderLoaded;
	private FontProvider bmpFontProvider;
	private FontProvider ttfFontProvider;

	private Array<EntitySupport> supports = new Array<EntitySupport>();

	private Batch batch;

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
		RuntimeJsonTags.registerTags(new LibgdxJsonTagRegistrar(json));

		json.setSerializer(IntMap.class, new IntMapJsonSerializer());

		return json;
	}

	public void setBatch (Batch batch) {
		this.batch = batch;
	}

	public void registerSupport (AssetManager manager, EntitySupport support) {
		supports.add(support);
		support.setLoaders(manager);
	}

	public void enableFreeType (AssetManager manager, FontProvider fontProvider) {
		this.ttfFontProvider = fontProvider;
		fontProvider.setLoaders(manager);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SceneParameter parameter) {
		if (batch == null) throw new IllegalStateException("Batch not set, see #setBatch(Batch)");

		Json json = getJson();
		data = json.fromJson(SceneData.class, file);

		Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
		loadDependencies(dependencies, data.entities);
		return dependencies;
	}

	private void loadDependencies (Array<AssetDescriptor> dependencies, Array<EntityData> entities) {
		for (EntityData entityData : entities) {
			for (Component component : entityData.components) {
				if (component instanceof AssetReference) {
					VisAssetDescriptor asset = ((AssetReference) component).asset;

					//TODO refactor
					if (asset instanceof TextureRegionAsset) {
						dependencies.add(new AssetDescriptor<TextureAtlas>(data.textureAtlasPath, TextureAtlas.class));

					} else if (asset instanceof AtlasRegionAsset) {
						AtlasRegionAsset regionAsset = (AtlasRegionAsset) asset;
						dependencies.add(new AssetDescriptor<TextureAtlas>(regionAsset.getPath(), TextureAtlas.class));

					} else if (asset instanceof BmpFontAsset) {
						checkShader(dependencies);
						bmpFontProvider.load(dependencies, asset);

					} else if (asset instanceof TtfFontAsset) {
						if (ttfFontProvider == null) {
							throw new IllegalStateException("TTF fonts are not enabled, ensure that gdx-freetype was " +
									"added to your project and call `manager.enableFreeType(new FreeTypeFontProvider())` " +
									"before scene loading!");
						}
						ttfFontProvider.load(dependencies, asset);

					} else if (asset instanceof ParticleAsset) {
						PathAsset particleAsset = (ParticleAsset) asset;
						dependencies.add(new AssetDescriptor<ParticleEffect>(particleAsset.getPath(), ParticleEffect.class));

					} else if (asset instanceof SoundAsset) {
						SoundAsset soundAsset = (SoundAsset) asset;
						dependencies.add(new AssetDescriptor<Sound>(soundAsset.getPath(), Sound.class));

					} else if (asset instanceof MusicAsset) {
						MusicAsset musicAsset = (MusicAsset) asset;
						dependencies.add(new AssetDescriptor<Music>(musicAsset.getPath(), Music.class));

					}
				}

				if (component instanceof ProtoShader) {
					ProtoShader shaderComponent = (ProtoShader) component;
					ShaderAsset asset = shaderComponent.asset;
					if (asset != null) {
						String path = asset.getFragPath().substring(0, asset.getFragPath().length() - 5);
						dependencies.add(new AssetDescriptor<ShaderProgram>(path, ShaderProgram.class));
					}
				}

				for (EntitySupport support : supports)
					support.resolveDependencies(dependencies, entityData, component);
			}
		}
	}

	private void checkShader (Array<AssetDescriptor> dependencies) {
		if (distanceFieldShaderLoaded == false)
			dependencies.add(new AssetDescriptor<ShaderProgram>(Gdx.files.classpath(DISTANCE_FIELD_SHADER), ShaderProgram.class));

		distanceFieldShaderLoaded = true;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SceneParameter parameter) {

	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		RuntimeContext context = new RuntimeContext(configuration, batch, manager, new ImmutableArray<EntitySupport>(supports));
		Scene scene = new Scene(context, data, parameter);
		EntityEngine engine = scene.getEntityEngine();
		for (EntityData entityData : data.entities)
			entityData.build(engine);
		return scene;
	}

	public void setRuntimeConfig (RuntimeConfiguration configuration) {
		this.configuration = configuration;
	}

	/** Allows to add additional system and managers into {@link EntityEngine} */
	static public class SceneParameter extends AssetLoaderParameters<Scene> {
		public SceneConfig config = new SceneConfig();
		/**
		 * If true (the default) scene data will be used to determinate whether physics systems needs
		 * to be enabled in {@link SceneConfig}. When this is set to false and you want to use physics you must manually
		 * enable it in config.
		 */
		public boolean respectScenePhysicsSettings = true;
	}
}
