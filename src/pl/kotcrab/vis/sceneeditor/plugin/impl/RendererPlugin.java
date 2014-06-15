/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.Utils;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.ICameraController;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class RendererPlugin extends PluginState {
	private ICameraController cameraController;
	private ObjectManagerPlugin objectManager;
	// private RectangularSelectionPlugin rectangularSelectionPlugin;

	private ShapeRenderer shapeRenderer;

	private boolean hideOutlines;

	public RendererPlugin (ICameraController cameraController, ObjectManagerPlugin objectManagerPlugin) {
		this.cameraController = cameraController;
		this.objectManager = objectManagerPlugin;

		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render () {
		if (state.editing && hideOutlines == false) {
			shapeRenderer.setProjectionMatrix(cameraController.getCombinedMatrix());
			shapeRenderer.begin(ShapeType.Line);

			for (ObjectRepresentation orep : objectManager.getObjectRepresenationList()) {
				if (orep.isMovingSupported())
					shapeRenderer.setColor(Color.WHITE);
				else
					shapeRenderer.setColor(Color.GRAY);

				renderRectangle(orep.getBoundingRectangle());

				if (orep.isScallingSupported()) {
					if (orep.isPointerInsideScaleArea())
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectScaleBox(orep);
				}
			}

			for (ObjectRepresentation orep : objectManager.getSelectedObjs()) {
				shapeRenderer.setColor(Color.RED);

				renderObjectOutline(orep);

				if (orep.isScallingSupported()) {
					if (orep.isPointerInsideScaleArea())
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectScaleBox(orep);
				}

				if (orep.isRotatingSupported()) {
					if (orep.isPointerInsideRotateArea())
						shapeRenderer.setColor(Color.RED);
					else
						shapeRenderer.setColor(Color.WHITE);

					renderObjectRotateCricle(orep);
				}

			}

			if (cameraController.isCameraDirty()) {
				if (cameraController.isCameraLocked())
					shapeRenderer.setColor(Color.RED);
				else
					shapeRenderer.setColor(Color.GREEN);
				renderRectangle(cameraController.getOriginalCameraRectangle());
			}

			shapeRenderer.end();
			// rectangularSelection.render(shapeRenderer);
		}
	}

	@Override
	public boolean keyDown (int keycode) {
		if (keycode == SceneEditorConfig.KEY_HIDE_OUTLINES) {
			hideOutlines = !hideOutlines;
			return true;
		}

		return false;
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}

	private void renderObjectOutline (ObjectRepresentation orep) {
		renderRectangle(orep.getBoundingRectangle());
	}

	private void renderObjectScaleBox (ObjectRepresentation orep) {
		renderRectangle(Utils.buildRectangeForScaleArea(orep));
	}

	private void renderObjectRotateCricle (ObjectRepresentation orep) {
		renderCircle(Utils.buildCirlcleForRotateArea(orep));
	}

	private void renderRectangle (Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}

	private void renderCircle (Circle cir) {
		shapeRenderer.circle(cir.x, cir.y, cir.radius);
	}
}
