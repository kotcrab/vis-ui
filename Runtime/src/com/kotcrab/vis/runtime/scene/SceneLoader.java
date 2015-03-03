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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.runtime.data.EntityData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.data.SceneSpriteData;
import com.kotcrab.vis.runtime.data.TextData;
import com.kotcrab.vis.runtime.entity.Entity;
import com.kotcrab.vis.runtime.entity.SpriteEntity;
import com.kotcrab.vis.runtime.entity.TextEntity;
import com.kotcrab.vis.runtime.font.BmpFontProvider;
import com.kotcrab.vis.runtime.font.FontProvider;

public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneLoader.SceneParameter> {
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
		json.addClassTag("SceneSpriteData", SceneSpriteData.class);
		json.addClassTag("TextData", TextData.class);
		return json;
	}

	public void enableFreeType (AssetManager manager, FontProvider fontProvider) {
		this.ttfFontProvider = fontProvider;
		fontProvider.setLoaders(manager);
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Json json = getJson();
		data = json.fromJson(SceneData.class, file);

		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();

		for (EntityData entityData : data.entities) {
			if (entityData instanceof SceneSpriteData) {
				SceneSpriteData spriteData = (SceneSpriteData) entityData;
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
		}

		return deps;
	}

	private void checkShader (Array<AssetDescriptor> deps) {
		if(distanceFieldShaderLoaded == false)
			deps.add(new AssetDescriptor(Gdx.files.classpath("com/kotcrab/vis/runtime/bmp-font-df"), ShaderProgram.class));

		distanceFieldShaderLoaded = true;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		scene = new Scene(data.viewport, data.width, data.height);

		Array<TextureAtlas> atlases = new Array<TextureAtlas>();
		Array<Entity> entities = new Array<Entity>();

		scene.setTextureAtlases(atlases);
		scene.setEntities(entities);

		for (EntityData entityData : data.entities) {
			if (entityData instanceof SceneSpriteData) {
				SceneSpriteData spriteData = (SceneSpriteData) entityData;

				TextureAtlas atlas = manager.get(spriteData.textureAtlas, TextureAtlas.class);
				if (atlases.contains(atlas, true) == false) atlases.add(atlas);

				Sprite newSprite = new Sprite(atlas.findRegion(spriteData.textureRegion));

				spriteData.loadTo(newSprite);
				SpriteEntity entity = new SpriteEntity(entityData.id, newSprite);
				entities.add(entity);
			}

			if (entityData instanceof TextData) {
				TextData textData = (TextData) entityData;

				BitmapFont font;
				if (textData.isTrueType)
					font = manager.get(textData.arbitraryFontName, BitmapFont.class);
				else
					font = manager.get(textData.relativeFontPath, BitmapFont.class);

				TextEntity entity = new TextEntity(font, textData.id, textData.relativeFontPath, textData.text, textData.fontSize);
				textData.loadTo(entity);
				entities.add(entity);
			}
		}

	}

	@Override
	public Scene loadSync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Scene scene = this.scene;
		this.scene = null;
		return scene;
	}

	static public class SceneParameter extends AssetLoaderParameters<Scene> {
	}
}
