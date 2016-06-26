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
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class ScaleTool extends AbstractGizmoTool {
	public static final String TOOL_ID = App.PACKAGE + ".tools.ScaleTool";

	private static final Vector3 tmpV3 = new Vector3();

	private Stage stage;

	private Color xRect = Color.GREEN;
	private Color xRectOver = Color.FOREST;
	private Color yRect = Color.RED;
	private Color yRectOver = new Color(0.662f, 0, 0.100f, 1f);

	private float startingVirtualMouseX;
	private float startingVirtualMouseY;

	private Rectangle xScaleRect = new Rectangle();
	private Rectangle yScaleRect = new Rectangle();
	private boolean mouseInsideRectX;
	private boolean mouseInsideRectY;

	@Override
	public void render (ShapeRenderer shapeRenderer) {
		super.render(shapeRenderer);

		if (totalSelectionBounds != null) {
			float centerX = totalSelectionBounds.x + totalSelectionBounds.width / 2;
			float centerY = totalSelectionBounds.y + totalSelectionBounds.height / 2;

			float centerRectSize = 0.1f * camera.getZoom() * 100f / scene.pixelsPerUnit;
			float lineLengthX = 1 * camera.getZoom() * 100f / scene.pixelsPerUnit;
			float lineLengthY = 1 * camera.getZoom() * 100f / scene.pixelsPerUnit;
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				if (mouseLooping.isOnVirtualScreen() == false) {
					if (mouseInsideRectX) lineLengthX = camera.getInputX() - centerX;
					if (mouseInsideRectY) lineLengthY = camera.getInputY() - centerY;
				} else {
					//if mouse is on virtual screen, method above won't work because line would be flipped
					//but also on virtual screen line end won't be visible anyways so we are just drawing very long line
					if (mouseInsideRectX) lineLengthX = 100000;
					if (mouseInsideRectY) lineLengthY = 100000;
				}
			}

			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.line(centerX, centerY, centerX + lineLengthX, centerY);
			shapeRenderer.end();

			shapeRenderer.setColor(mouseInsideRectX ? xRectOver : xRect);
			shapeRenderer.begin(ShapeType.Filled);
			rect(shapeRenderer, xScaleRect.set(centerX + lineLengthX - centerRectSize / 2, centerY - centerRectSize / 2, centerRectSize, centerRectSize));
			shapeRenderer.end();

			shapeRenderer.setColor(Color.RED);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.line(centerX, centerY, centerX, centerY + lineLengthY);
			shapeRenderer.end();

			shapeRenderer.setColor(mouseInsideRectY ? yRectOver : yRect);
			shapeRenderer.begin(ShapeType.Filled);
			rect(shapeRenderer, yScaleRect.set(centerX - centerRectSize / 2, centerY + lineLengthY - centerRectSize / 2, centerRectSize, centerRectSize));
			shapeRenderer.end();
		}
	}

	private static void rect (ShapeRenderer shapeRenderer, Rectangle rect) {
		shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		boolean result = super.touchDown(event, x, y, pointer, button);
		if (isMouseInsideScaleArea()) mouseLooping.loopCursor();
		unprojectVirtualMouseCords(tmpV3);
		startingVirtualMouseX = tmpV3.x;
		startingVirtualMouseY = tmpV3.y;
		return result;
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		super.mouseMoved(event, x, y);

		x = camera.getInputX();
		y = camera.getInputY();

		mouseInsideRectX = false;
		mouseInsideRectY = false;

		if (xScaleRect.contains(x, y)) mouseInsideRectX = true;
		if (yScaleRect.contains(x, y)) mouseInsideRectY = true;

		return true;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (isMouseInsideScaleArea()) dragging = false;

		super.touchDragged(event, x, y, pointer);

		if (isMouseInsideScaleArea() && totalSelectionBounds != null) {
			unprojectVirtualMouseCords(tmpV3);
			float scaleDeltaX = startingVirtualMouseX - tmpV3.x;
			float scaleDeltaY = startingVirtualMouseY - tmpV3.y;

			ImmutableArray<EntityProxy> entities = entityManipulator.getSelectedEntities();
			for (int i = 0; i < entities.size(); i++) {
				EntityProxy entity = entities.get(i);
				EntityTransform startingProps = startingEntityProps.get(i);

				if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
					float ratio = startingProps.scaleX / startingProps.scaleY;
					if (mouseInsideRectX) scaleDeltaY = scaleDeltaX / ratio * 0.05f;
					if (mouseInsideRectY) scaleDeltaX = scaleDeltaY / ratio * 0.05f;
					entity.setScale(startingProps.scaleX - scaleDeltaX, startingProps.scaleY - scaleDeltaY);
				} else {
					entity.setScale(
							mouseInsideRectX ? startingProps.scaleX - scaleDeltaX * 0.05f : entity.getScaleX(),
							mouseInsideRectY ? startingProps.scaleY - scaleDeltaY * 0.05f : entity.getScaleY());
				}

				dragged = true; //touchUp in parent class will save undo action
			}

			entityManipulator.markSceneDirty();
			entityManipulator.selectedEntitiesValuesChanged();
		}
	}

	private void unprojectVirtualMouseCords (Vector3 v3) {
		stage.getCamera().unproject(v3.set(mouseLooping.getVirtualMouseX(), mouseLooping.getVirtualMouseY(), 0));
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		ImmutableArray<EntityProxy> entities = entityManipulator.getSelectedEntities();
		for (int i = 0; i < entities.size(); i++) {
			EntityProxy entity = entities.get(i);
			EntityTransform startingProps = startingEntityProps.get(i);

			if (entity.getScaleX() < 0 || entity.getScaleY() < 0) {
				entity.setScale(startingProps.scaleX, startingProps.scaleY);
				entityManipulator.selectedEntitiesValuesChanged();
			}
		}

		super.touchUp(event, x, y, pointer, button);
	}

	@Override
	protected void resetAfterTouchUp () {
		super.resetAfterTouchUp();
		startingVirtualMouseX = 0;
		startingVirtualMouseY = 0;
	}

	@Override
	protected boolean isMouseInsideSelectedEntities (float x, float y) {
		if (isMouseInsideScaleArea()) return true;
		return super.isMouseInsideSelectedEntities(x, y);
	}

	private boolean isMouseInsideScaleArea () {
		return mouseInsideRectX || mouseInsideRectY;
	}

	@Override
	public String getToolId () {
		return TOOL_ID;
	}
}
