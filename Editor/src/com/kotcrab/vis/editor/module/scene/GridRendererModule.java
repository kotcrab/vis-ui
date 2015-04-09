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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.kotcrab.vis.editor.module.editor.EditorSettingsModule;
import com.kotcrab.vis.editor.util.gdx.FieldUtils;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter.DigitsOnlyFilter;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class GridRendererModule extends SceneModule {
	private CameraModule camera;
	private RendererModule renderer;

	private ShapeRenderer shapeRenderer;

	private GridSettingsModule settings;

	@Override
	public void init () {
		renderer = sceneContainer.get(RendererModule.class);
		camera = sceneContainer.get(CameraModule.class);

		settings = container.get(GridSettingsModule.class);

		shapeRenderer = renderer.getShapeRenderer();
	}

	@Override
	public void render (Batch batch) {
		if (settings.config.drawGrid) {
			batch.end();

			Gdx.gl.glLineWidth(1);

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
		int gridSize = settings.config.gridSize;
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
		int gridSize = settings.config.gridSize;
		float yStart = camera.getY() - camera.getHeight() / 2;
		float yEnd = yStart + camera.getHeight();

		int leftDownX = (int) (camera.getX() - camera.getWidth() / 2);
		int linesToDraw = (int) (camera.getWidth() / gridSize) + 1;

		int drawingPointStart = leftDownX / gridSize;
		int drawingPointEnd = drawingPointStart + linesToDraw;

		for (int i = drawingPointStart; i < drawingPointEnd; i++)
			shapeRenderer.line(i * gridSize, yStart, i * gridSize, yEnd);
	}

	public static class GridSettingsModule extends EditorSettingsModule<GridConfig> {
		private VisCheckBox drawGridCheck;
		private VisValidableTextField gridSizeField;

		public GridSettingsModule () {
			super("Grid", "gridSettings", GridConfig.class);
		}

		@Override
		public boolean settingsChanged () {
			return gridSizeField.isInputValid();
		}

		@Override
		public void buildTable () {
			VisTable sizeTable = new VisTable(true);

			sizeTable.add(new VisLabel("Grid size: "));
			sizeTable.add(gridSizeField = new VisValidableTextField(Validators.INTEGERS));

			prepareTable();
			settingsTable.add(drawGridCheck = new VisCheckBox("Draw grid", config.drawGrid)).left();
			settingsTable.row();
			settingsTable.add(sizeTable);

			gridSizeField.setTextFieldFilter(new DigitsOnlyFilter());
			gridSizeField.addValidator(new Validators.GreaterThanValidator(0));
			gridSizeField.setText(String.valueOf(config.gridSize));
		}

		@Override
		public void loadConfigToTable () {
			drawGridCheck.setChecked(config.drawGrid);
			gridSizeField.setText(String.valueOf(config.gridSize));
		}

		@Override
		public void settingsApply () {
			config.drawGrid = drawGridCheck.isChecked();
			config.gridSize = FieldUtils.getInt(gridSizeField, 0);
			settingsSave();
		}
	}

	public static class GridConfig {
		public boolean drawGrid = true;
		public int gridSize = 256;
	}
}
