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
import com.artemis.Component;
import com.artemis.Manager;
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
import com.kotcrab.vis.runtime.component.*;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.font.BitmapFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.plugin.EntitySupport;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.runtime.util.SpriterData;

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
		json.addClassTag("SceneData", SceneData.class);
		json.addClassTag("SceneViewport", SceneViewport.class);
		json.addClassTag("LayerCordsSystem", LayerCordsSystem.class);

		json.addClassTag("PathAsset", PathAsset.class);
		json.addClassTag("BmpFontAsset", BmpFontAsset.class);
		json.addClassTag("TtfFontAsset", TtfFontAsset.class);
		json.addClassTag("AtlasRegionAsset", AtlasRegionAsset.class);
		json.addClassTag("TextureRegionAsset", TextureRegionAsset.class);
		json.addClassTag("ShaderAsset", ShaderAsset.class);
		json.addClassTag("SpriterAsset", SpriterAsset.class);

		json.addClassTag("AssetComponent", AssetComponent.class);
		json.addClassTag("GroupComponent", GroupComponent.class);
		json.addClassTag("IDComponent", IDComponent.class);
		json.addClassTag("InvisibleComponent", InvisibleComponent.class);
		json.addClassTag("LayerComponent", LayerComponent.class);
		json.addClassTag("RenderableComponent", RenderableComponent.class);
		json.addClassTag("VariablesComponent", VariablesComponent.class);
		json.addClassTag("PhysicsPropertiesComponent", PhysicsPropertiesComponent.class);
		json.addClassTag("PolygonComponent", PolygonComponent.class);

		json.addClassTag("SpriteProtoComponent", SpriteProtoComponent.class);
		json.addClassTag("MusicProtoComponent", MusicProtoComponent.class);
		json.addClassTag("SoundProtoComponent", SoundProtoComponent.class);
		json.addClassTag("ParticleProtoComponent", ParticleProtoComponent.class);
		json.addClassTag("TextProtoComponent", TextProtoComponent.class);
		json.addClassTag("ShaderProtoComponent", ShaderProtoComponent.class);
		json.addClassTag("SpriterProtoComponent", SpriterProtoComponent.class);

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
						if (path.startsWith("spriter/"))
							dependencies.add(new AssetDescriptor<SpriterData>(path, SpriterData.class));
					}
				}

				if (component instanceof ShaderProtoComponent) {
					ShaderProtoComponent shaderComponent = (ShaderProtoComponent) component;
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
		RuntimeContext context = new RuntimeContext(configuration, batch, manager, new ImmutableArray<EntitySupport>(supports));
		scene = new Scene(context, data, parameter);

		EntityEngine engine = scene.getEntityEngine();
		for (EntityData entityData : data.entities)
			entityData.build(engine);
	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Scene scene = this.scene;
		this.scene = null;
		return scene;
	}

	public void setRuntimeConfig (RuntimeConfiguration configuration) {
		this.configuration = configuration;
	}

	/** Allows to add additional system and managers into {@link EntityEngine} */
	static public class SceneParameter extends AssetLoaderParameters<Scene> {
		public Array<BaseSystem> systems = new Array<BaseSystem>();
		public Array<BaseSystem> passiveSystems = new Array<BaseSystem>();
		public Array<Manager> managers = new Array<Manager>();
	}
}
