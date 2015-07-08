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

package com.kotcrab.vis.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/** @author Kotcrab */
@Deprecated
public class Test implements ApplicationListener {
	private OrthographicCamera camera;
	private StretchViewport stretchViewport;

	int ppu = 76;
	private Sprite sprite;

	private SpriteBatch batch;

	public static void main (String[] args) {
		new LwjglApplication(new Test(), "B", 76 * 10, 76 * 5);

	}

	@Override
	public void create () {

		stretchViewport = new StretchViewport(10, 5);
		camera = (OrthographicCamera) stretchViewport.getCamera();
		camera.position.x = 5;
		camera.position.y = 2.5f;

		batch = new SpriteBatch();

		TextureAtlas atlas = new TextureAtlas("gfx/icons.atlas");
		sprite = atlas.createSprite("file-question-big");

		sprite.setPosition(0, 0);
		sprite.setSize(1, 1);
		System.out.println(sprite.getBoundingRectangle());
	}

	@Override
	public void resize (int width, int height) {
		stretchViewport.update(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		sprite.draw(batch);

		batch.end();
	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}

	@Override
	public void dispose () {

	}
}
