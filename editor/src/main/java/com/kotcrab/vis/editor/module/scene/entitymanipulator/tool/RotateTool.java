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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.gdx.ShapeRendererUtils;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class RotateTool extends AbstractGizmoTool {
	public static final String TOOL_ID = App.PACKAGE + ".tools.RotateTool";

	private static final Vector3 tmpV3 = new Vector3();

	private Stage stage;

	private float circleCenterX;
	private float circleCenterY;
	private float innerRadius;
	private float outerRadius;

	private boolean insideRotateArea;

	private float startingRotationValue;

	@Override
	public void render (ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);

		if (totalSelectionBounds != null) {
			innerRadius = camera.getZoom() * 0.6f * 100f / scene.pixelsPerUnit;
			outerRadius = camera.getZoom() * 0.7f * 100f / scene.pixelsPerUnit;

			circleCenterX = totalSelectionBounds.x + totalSelectionBounds.width / 2;
			circleCenterY = totalSelectionBounds.y + totalSelectionBounds.height / 2;

			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.circle(circleCenterX, circleCenterY, innerRadius, 36);
			shapeRenderer.circle(circleCenterX, circleCenterY, outerRadius, 36);

			float rotation = entityManipulator.getSelectedEntities().peek().getRotation();
			float startX = circleCenterX;
			float startY = circleCenterY - innerRadius;
			float endX = circleCenterX;
			float endY = circleCenterY + innerRadius;

			shapeRenderer.setColor(Color.RED);
			ShapeRendererUtils.lineRotatedAroundPoint(shapeRenderer, startX, startY, endX, endY, rotation, circleCenterX, circleCenterY);
			shapeRenderer.setColor(Color.GREEN);
			ShapeRendererUtils.lineRotatedAroundPoint(shapeRenderer, startX, startY, endX, endY, rotation + 90, circleCenterX, circleCenterY);

			shapeRenderer.end();
		}
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		super.mouseMoved(event, x, y);
		x = camera.getInputX();
		y = camera.getInputY();

		boolean outsideInnerCircle = Math.pow(x - circleCenterX, 2) + Math.pow(y - circleCenterY, 2) > Math.pow(innerRadius, 2);
		boolean insideOuterCircle = Math.pow(x - circleCenterX, 2) + Math.pow(y - circleCenterY, 2) <= Math.pow(outerRadius, 2);

		insideRotateArea = outsideInnerCircle && insideOuterCircle;
		return true;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		boolean result = super.touchDown(event, x, y, pointer, button);
		startingRotationValue = getRotationFromVirtualMouse();
		if (insideRotateArea) mouseLooping.loopCursor();
		return result;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (insideRotateArea) dragging = false;

		super.touchDragged(event, x, y, pointer);

		if (insideRotateArea && totalSelectionBounds != null) {
			float rotationDelta = startingRotationValue - getRotationFromVirtualMouse();
			ImmutableArray<EntityProxy> entities = entityManipulator.getSelectedEntities();
			for (int i = 0; i < entities.size(); i++) {
				EntityProxy entity = entities.get(i);
				EntityTransform startingProps = startingEntityProps.get(i);

				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
					//TODO: snap value should be customizable
					rotationDelta = Math.round(rotationDelta / 30) * 30;
					entity.setRotation(-rotationDelta);
				} else
					entity.setRotation(startingProps.rotation - rotationDelta);

				dragged = true; //touchUp in parent class will save undo action
			}

			entityManipulator.markSceneDirty();
			entityManipulator.selectedEntitiesValuesChanged();
		}
	}

	@Override
	protected void resetAfterTouchUp () {
		super.resetAfterTouchUp();
		startingRotationValue = 0;
	}

	private float getRotationFromVirtualMouse () {
		stage.getCamera().unproject(tmpV3.set(mouseLooping.getVirtualMouseX(), mouseLooping.getVirtualMouseY(), 0));
		float virtualX = tmpV3.x;
		float virtualY = tmpV3.y;

		float deltaX = virtualX - dragStartX;
		float deltaY = virtualY - dragStartY;

		return deltaX + deltaY;
	}

	@Override
	protected boolean isMouseInsideSelectedEntities (float x, float y) {
		if (insideRotateArea) return true;
		return super.isMouseInsideSelectedEntities(x, y);
	}

	@Override
	public String getToolId () {
		return TOOL_ID;
	}
}
