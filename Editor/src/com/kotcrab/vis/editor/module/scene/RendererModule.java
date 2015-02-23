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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class RendererModule extends SceneModule {
	private ShapeRenderer shapeRenderer;

	@Override
	public void added () {
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		for (EditorEntity obj : scene.entities) {
			SpriteObject obj2d = (SpriteObject) obj;
			obj2d.sprite.draw(batch);
		}

		batch.end();
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(0,0, scene.width, scene.height);
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
