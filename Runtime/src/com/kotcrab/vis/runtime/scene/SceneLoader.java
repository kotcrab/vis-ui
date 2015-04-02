/*
 * Copyright 2014-2015 Pawel Pastuszak
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
import com.kotcrab.vis.runtime.data.*;
import com.kotcrab.vis.runtime.entity.*;
import com.kotcrab.vis.runtime.font.BmpFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;
import com.kotcrab.vis.runtime.scene.SceneLoader.SceneParameter;

public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneParameter> {
	private static final FileHandle distanceFieldShader = Gdx.files.classpath("com/kotcrab/vis/runtime/bmp-font-df");

	private SceneData data;
	private Scene scene;

	private boolean distanceFieldShaderLoaded;
	private FontProvider bmpFontProvider;
	private FontProvider ttfFontProvider;

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
		return json;
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

		for (EntityData entityData : data.entities) {
			//NOTE: when using 'relative path' form data, path must have / as path separator, using \ is not supported and will cause "Assets not loaded" exception
			//slash replacing should be handled in EntityData

			if (entityData instanceof SpriteData) {
				SpriteData spriteData = (SpriteData) entityData;
				deps.add(new AssetDescriptor(spriteData.textureAtlas, TextureAtlas.class));
			}

			if (entityData instanceof TextData) {
				TextData textData = (TextData) entityData;

				if (textData.isTrueType)
					ttfFontProvider.load(deps, textData);
				else {
					checkShader(deps);
					bmpFontProvider.load(deps, textData);
				}
			}

			if (entityData instanceof MusicData) {
				MusicData musicData = (MusicData) entityData;
				deps.add(new AssetDescriptor(musicData.musicPath, Music.class));
			}

			if (entityData instanceof SoundData) {
				SoundData musicData = (SoundData) entityData;
				deps.add(new AssetDescriptor(musicData.soundPath, Sound.class));
			}
		}

		return deps;
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

		for (EntityData entityData : data.entities) {
			if (entityData instanceof SpriteData) {
				SpriteData spriteData = (SpriteData) entityData;

				TextureAtlas atlas = manager.get(spriteData.textureAtlas, TextureAtlas.class);
				if (atlases.contains(atlas, true) == false) atlases.add(atlas);

				Sprite newSprite = new Sprite(atlas.findRegion(spriteData.texturePath.substring(4, spriteData.texturePath.length() - 4)));

				SpriteEntity entity = new SpriteEntity(entityData.id, spriteData.texturePath, newSprite);
				spriteData.loadTo(entity);

				entities.add(entity);
			}

			if (entityData instanceof TextData) {
				TextData textData = (TextData) entityData;

				BitmapFont font;
				if (textData.isTrueType)
					font = manager.get(textData.arbitraryFontName, BitmapFont.class);
				else
					font = manager.get(textData.fontPath, BitmapFont.class);

				TextEntity entity = new TextEntity(textData.id, font, textData.fontPath, textData.text, textData.fontSize);
				textData.loadTo(entity);
				entities.add(entity);
			}

			if (entityData instanceof MusicData) {
				MusicData musicData = (MusicData) entityData;
				MusicEntity entity = new MusicEntity(musicData.id, musicData.musicPath, manager.get(musicData.musicPath, Music.class));
				musicData.loadTo(entity);
				entities.add(entity);
			}

			if (entityData instanceof SoundData) {
				SoundData soundData = (SoundData) entityData;
				SoundEntity entity = new SoundEntity(soundData.id, soundData.soundPath, manager.get(soundData.soundPath, Sound.class));
				soundData.loadTo(entity);
				entities.add(entity);
			}

		}

		if (distanceFieldShaderLoaded)
			scene.getDistanceFieldShaderFromManager(distanceFieldShader);
	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Scene scene = this.scene;
		this.scene = null;

		for (EntityData entityData : data.entities) {
			if (entityData instanceof ParticleEffectData) {
				ParticleEffectData particleData = (ParticleEffectData) entityData;

				FileHandle effectFile = resolve(particleData.relativePath);
				ParticleEffect emitter = new ParticleEffect();
				emitter.load(effectFile, effectFile.parent());

				ParticleEffectEntity entity = new ParticleEffectEntity(particleData.id, particleData.relativePath, emitter);
				particleData.loadTo(entity);
				scene.getEntities().add(entity);
			}
		}

		scene.onAfterLoad();

		return scene;
	}

	static public class SceneParameter extends AssetLoaderParameters<Scene> {
	}
}
