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

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.data.SceneEntityData;
import com.kotcrab.vis.runtime.data.SceneSpriteData;

public class SceneLoader extends AsynchronousAssetLoader<Scene, SceneLoader.SceneParameter> {
	private SceneData data;
	private Scene scene;


	public SceneLoader () {
		super(new InternalFileHandleResolver());
	}

	public SceneLoader (FileHandleResolver resolver) {
		super(resolver);
	}

	public static Json getJson () {
		Json json = new Json();
		json.addClassTag("SceneSpriteData", SceneSpriteData.class);
		return json;
	}

	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		Json json = getJson();
		data = json.fromJson(SceneData.class, file);

		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();


		for(SceneEntityData entityData : data.entities)
		{
			if(entityData instanceof SceneSpriteData)
			{
				SceneSpriteData spriteData = (SceneSpriteData) entityData;

				deps.add(new AssetDescriptor(spriteData.textureAtlas, TextureAtlas.class));

			}
		}

		return deps;
	}

	@Override
	public void loadAsync (AssetManager manager, String fileName, FileHandle file, SceneLoader.SceneParameter parameter) {
		scene = new Scene(data.viewport, data.width, data.height);

		Array<Sprite> sprites = new Array<Sprite>();
		Array<TextureAtlas> atlases = new Array<TextureAtlas>();
		scene.setSprites(sprites);
		scene.setTextureAtlases(atlases);

		for (SceneEntityData entity : data.entities) {
			if (entity instanceof SceneSpriteData) {
				SceneSpriteData spriteData = (SceneSpriteData) entity;

				TextureAtlas atlas = manager.get(spriteData.textureAtlas, TextureAtlas.class);
				if (atlases.contains(atlas, true) == false) atlases.add(atlas);

				Sprite newSprite = new Sprite(atlas.findRegion(spriteData.textureRegion));

				spriteData.loadTo(newSprite);
				sprites.add(newSprite);
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
