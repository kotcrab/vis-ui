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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Scene {
	private OrthographicCamera camera;
	private Viewport viewport;

	private Array<TextureAtlas> textureAtlases;
	private Array<Sprite> sprites;

	public Scene (SceneViewport viewportType, int width, int height) {
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
				viewport = new ScreenViewport();
				break;
			case EXTEND:
				viewport = new ExtendViewport(width, height, camera);
				break;
		}
	}

	Array<TextureAtlas> getTextureAtlases () {
		return textureAtlases;
	}

	void setTextureAtlases (Array<TextureAtlas> textureAtlases) {
		this.textureAtlases = textureAtlases;
	}

	Array<Sprite> getSprites () {
		return sprites;
	}

	void setSprites (Array<Sprite> sprites) {
		this.sprites = sprites;
	}

	public void render (SpriteBatch batch) {
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		for (Sprite s : sprites)
			s.draw(batch);

		batch.end();
	}

	public void resize (int width, int height) {
		viewport.update(width, height);
	}
}
