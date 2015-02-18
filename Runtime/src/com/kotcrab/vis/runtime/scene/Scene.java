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

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class Scene {
	private OrthographicCamera camera;
	private Array<TextureAtlas> textureAtlases;
	private Array<Sprite> sprites;

	public Scene () {
		camera = new OrthographicCamera(1280, 720);
		camera.position.x = 1280 / 2;
		camera.position.y = 720 / 2;
		camera.update();
	}

	public Array<TextureAtlas> getTextureAtlases () {
		return textureAtlases;
	}

	public void setTextureAtlases (Array<TextureAtlas> textureAtlases) {
		this.textureAtlases = textureAtlases;
	}

	public Array<Sprite> getSprites () {
		return sprites;
	}

	public void setSprites (Array<Sprite> sprites) {
		this.sprites = sprites;
	}

	public void render (SpriteBatch batch) {
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		for (Sprite s : sprites)
			s.draw(batch);

		batch.end();
	}
}
