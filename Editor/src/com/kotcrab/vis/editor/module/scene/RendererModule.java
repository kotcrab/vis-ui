/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.Log;

public class RendererModule extends SceneModule {
	private CameraModule camera;
	private ShapeRenderer shapeRenderer;
	private ShaderProgram fontShader;

	@Override
	public void added () {
		shapeRenderer = new ShapeRenderer();

		fontShader = new ShaderProgram(Gdx.files.internal("shader/bmp-font-df.vert"), Gdx.files.internal("shader/bmp-font-df.frag"));
		if (!fontShader.isCompiled()) {
			Log.fatal("Renderer", "FontShader compilation failed:\n" + fontShader.getLog());
			throw new IllegalStateException("Shader compilation failed");
		}
	}

	@Override
	public void init () {
		camera = sceneContainer.get(CameraModule.class);
	}

	@Override
	public void render (Batch batch) {
		boolean useShader;

		for (EditorEntity entity : scene.entities) {
			useShader = false;

			if (entity instanceof TextObject) {
				TextObject obj = (TextObject) entity;
				if (obj.isDistanceFieldShaderEnabled()) useShader = true;
			}

			if (useShader) batch.setShader(fontShader);
			entity.render(batch);
			if (useShader) batch.setShader(null);
		}

		batch.end();
		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
		shapeRenderer.setColor(Color.WHITE);
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
