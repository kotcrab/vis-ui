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
import com.kotcrab.vis.editor.module.EditorSettingsModule;
import com.kotcrab.vis.editor.util.FieldUtils;
import com.kotcrab.vis.editor.util.Validators;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class GridRendererModule extends SceneModule {
	private CameraModule camera;
	private RendererModule renderer;

	private ShapeRenderer shapeRenderer;

	private GridSettingsModule config;

	@Override
	public void init () {
		renderer = sceneContainer.get(RendererModule.class);
		camera = sceneContainer.get(CameraModule.class);

		config = container.get(GridSettingsModule.class);

		shapeRenderer = renderer.getShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		if (config.drawGrid) {
			batch.end();

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(0.32f, 0.32f, 0.32f, 1f));

			drawVerticalLines();
			drawHorizontalLines();

			shapeRenderer.end();

			batch.begin();
		}
	}

	private void drawVerticalLines () {
		int gridSize = config.gridSize;
		float xStart = camera.getX() - camera.getWidth() / 2;
		float xEnd = xStart + camera.getWidth();

		int leftDownY = (int) (camera.getY() - camera.getHeight() / 2);
		int linesToDraw = (int) (camera.getHeight() / gridSize) + 1;

		int drawingPointStart = leftDownY / gridSize;
		int drawingPointEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPointEnd; i++)
			shapeRenderer.line(xStart, i * gridSize, xEnd, i * gridSize);
	}

	private void drawHorizontalLines () {
		int gridSize = config.gridSize;
		float yStart = camera.getY() - camera.getHeight() / 2;
		float yEnd = yStart + camera.getHeight();

		int leftDownX = (int) (camera.getX() - camera.getWidth() / 2);
		int linesToDraw = (int) (camera.getWidth() / gridSize) + 1;

		int drawingPointStart = leftDownX / gridSize;
		int drawingPointEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPointEnd; i++)
			shapeRenderer.line(i * gridSize, yStart, i * gridSize, yEnd);
	}

	public static class GridSettingsModule extends EditorSettingsModule {
		public boolean drawGrid = true;
		public int gridSize = 256;

		private VisCheckBox drawGridCheck;
		private VisValidableTextField gridSizeField;

		@Override
		protected void rebuildSettingsTable () {
			VisTable sizeTable = new VisTable(true);

			sizeTable.add(new VisLabel("Grid size: "));
			sizeTable.add(gridSizeField = new VisValidableTextField(Validators.integers));

			settingsTable.clear();
			settingsTable.left().top();
			settingsTable.defaults().expandX().left();
			settingsTable.add(drawGridCheck = new VisCheckBox("Draw grid", drawGrid)).left();
			settingsTable.row();
			settingsTable.add(sizeTable);

			drawGridCheck.setChecked(true);
			gridSizeField.setTextFieldFilter(new DigitsOnlyFilter());
			gridSizeField.addValidator(new Validators.GreaterThanValidator(0));
			gridSizeField.setText(String.valueOf(gridSize));
		}

		@Override
		public void apply () {
			drawGrid = drawGridCheck.isChecked();
			gridSize = FieldUtils.getInt(gridSizeField, 0);
		}

		@Override
		public boolean changed () {
			return gridSizeField.isInputValid();
		}

		@Override
		public String getSettingsName () {
			return "Grid";
		}
	}
}
