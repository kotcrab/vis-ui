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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * @author Kotcrab
 */
public class RendererModule extends SceneModule {
	private CameraModule camera;

	private ShapeRenderer shapeRenderer;

	@Override
	public void added () {
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		batch.end();
		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(0, 0, scene.width, scene.height);
		shapeRenderer.end();
		batch.begin();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}

	public ShapeRenderer getShapeRenderer () {
		return shapeRenderer;
	}
}
