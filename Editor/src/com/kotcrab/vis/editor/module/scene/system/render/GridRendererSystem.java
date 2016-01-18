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

package com.kotcrab.vis.editor.module.scene.system.render;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.editor.ColorSettingsModule;
import com.kotcrab.vis.editor.module.editor.EditorSettingsModule;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.editor.module.scene.RendererModule;
import com.kotcrab.vis.editor.util.NumberUtils;
import com.kotcrab.vis.editor.util.scene2d.FieldUtils;
import com.kotcrab.vis.ui.util.FloatDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * Renders scene grid
 * @author Kotcrab
 */
public class GridRendererSystem extends BaseSystem {
	private CameraModule camera;
	private RendererModule renderer;

	private ColorSettingsModule colorSettings;
	private GridSettingsModule settings;

	private Batch batch;
	private ModuleInjector sceneMC;

	private ShapeRenderer shapeRenderer;

	public GridRendererSystem (Batch batch, ModuleInjector sceneMC) {
		this.batch = batch;
		this.sceneMC = sceneMC;
	}

	@Override
	protected void initialize () {
		sceneMC.injectModules(this);
		shapeRenderer = renderer.getShapeRenderer();
	}

	@Override
	protected void processSystem () {
		if (settings.config.drawGrid) {
			batch.end();

			Gdx.gl.glLineWidth(1);

			camera.update();
			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(colorSettings.getGridColor());

			drawVerticalLines();
			drawHorizontalLines();

			shapeRenderer.end();

			batch.begin();
		}
	}

	private void drawVerticalLines () {
		float gridSize = settings.config.gridSize;
		float xStart = camera.getX() - camera.getWidth() / 2;
		float xEnd = xStart + camera.getWidth();

		float leftDownY = (camera.getY() - camera.getHeight() / 2);
		float linesToDraw = (camera.getHeight() / gridSize) + 10;

		float drawingPointStart = leftDownY / gridSize;
		float drawingPointEnd = drawingPointStart + linesToDraw;

		for (int i = MathUtils.round(drawingPointStart); i < MathUtils.round(drawingPointEnd); i++)
			shapeRenderer.line(xStart, i * gridSize, xEnd, i * gridSize);
	}

	private void drawHorizontalLines () {
		float gridSize = settings.config.gridSize;
		float yStart = camera.getY() - camera.getHeight() / 2;
		float yEnd = yStart + camera.getHeight();

		float leftDownX = (camera.getX() - camera.getWidth() / 2);
		float linesToDraw = (camera.getWidth() / gridSize) + 10;

		float drawingPointStart = leftDownX / gridSize;
		float drawingPointEnd = drawingPointStart + linesToDraw;

		for (int i = MathUtils.round(drawingPointStart); i < MathUtils.round(drawingPointEnd); i++)
			shapeRenderer.line(i * gridSize, yStart, i * gridSize, yEnd);
	}

	public static class GridSettingsModule extends EditorSettingsModule<GridConfig> {
		private VisCheckBox drawGridCheck;
		private VisValidatableTextField gridSizeField;

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
			sizeTable.add(gridSizeField = new VisValidatableTextField(Validators.FLOATS));

			prepareTable();
			settingsTable.add(drawGridCheck = new VisCheckBox("Draw grid", config.drawGrid)).left();
			settingsTable.row();
			settingsTable.add(sizeTable);

			gridSizeField.setTextFieldFilter(new FloatDigitsOnlyFilter(false));
			gridSizeField.addValidator(new Validators.GreaterThanValidator(0));
			gridSizeField.setText(NumberUtils.floatToString(config.gridSize));
		}

		@Override
		public void loadConfigToTable () {
			drawGridCheck.setChecked(config.drawGrid);
			gridSizeField.setText(NumberUtils.floatToString(config.gridSize));
		}

		@Override
		public void settingsApply () {
			config.drawGrid = drawGridCheck.isChecked();
			config.gridSize = FieldUtils.getFloat(gridSizeField, 0);
			settingsSave();
		}
	}

	public static class GridConfig {
		@Tag(0) public boolean drawGrid = true;
		@Tag(1) public float gridSize = 3;
	}
}
