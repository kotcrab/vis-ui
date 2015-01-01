/**
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

package pl.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GridRendererModule extends SceneModule {
	private CameraModule camera;
	private RendererModule renderer;

	private ShapeRenderer shapeRenderer;

	private int cellSize = 256;

	@Override
	public void init () {
		renderer = containter.get(RendererModule.class);
		camera = containter.get(CameraModule.class);

		shapeRenderer = renderer.getShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		batch.end();

		shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(new Color(0.32f, 0.32f, 0.32f, 1f));

		drawVerticalLines();
		drawHorizontalLines();

		shapeRenderer.end();

		batch.begin();
	}

	private void drawVerticalLines () {
		float xStart = camera.getX() - camera.getWidth() / 2;
		float xEnd = xStart + camera.getWidth();

		int leftDownY = (int)(camera.getY() - camera.getHeight() / 2);
		int linesToDraw = (int)(camera.getHeight() / cellSize) + 1;

		int drawingPointStart = leftDownY / cellSize;
		int drawingPonintEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPonintEnd; i++)
			shapeRenderer.line(xStart, i * cellSize, xEnd, i * cellSize);
	}

	private void drawHorizontalLines () {
		float yStart = camera.getY() - camera.getHeight() / 2;
		float yEnd = yStart + camera.getHeight();

		int leftDownX = (int)(camera.getX() - camera.getWidth() / 2);
		int linesToDraw = (int)(camera.getWidth() / cellSize) + 1;

		int drawingPointStart = leftDownX / cellSize;
		int drawingPonintEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPonintEnd; i++)
			shapeRenderer.line(i * cellSize, yStart, i * cellSize, yEnd);
	}
}
