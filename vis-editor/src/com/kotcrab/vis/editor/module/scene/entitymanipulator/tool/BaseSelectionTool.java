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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.RectangularSelection;
import com.kotcrab.vis.editor.proxy.EntityProxy;

/**
 * Most basic tool, allows only to select entities and use rectangular selection. May be used as common class for other
 * tools.
 * @author Kotcrab
 * @see SelectionTool
 */
public abstract class BaseSelectionTool extends Tool {
	protected float lastTouchX, lastTouchY;
	protected float dragStartX, dragStartY;

	private boolean mouseInsideSelected;
	protected boolean cameraDragged;
	protected boolean dragging;
	protected boolean dragged;

	private RectangularSelection rectangularSelection;
	protected boolean rectSelectionTouchDraggedResult;

	@Override
	public void init () {
		rectangularSelection = new RectangularSelection(scene, entityManipulator, entityProxyCache);
	}

	@Override
	public void render (ShapeRenderer shapeRenderer) {
		rectangularSelection.render(shapeRenderer);
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (button == Buttons.LEFT) {
			x = camera.getInputX();
			y = camera.getInputY();

			dragging = true;

			lastTouchX = x;
			lastTouchY = y;

			dragStartX = x;
			dragStartY = y;

			if (isMouseInsideSelectedEntities(x, y) == false) {
				mouseInsideSelected = true;

				//multiple select made easy
				if (UIUtils.ctrl() == false) entityManipulator.softSelectionReset();

				EntityProxy result = findEntityWithSmallestSurfaceArea(x, y);
				if (result != null && entityManipulator.isSelected(result) == false)
					entityManipulator.selectAppend(result);
				else
					return rectangularSelection.touchDown(x, y, button);

				return true;
			}

		}

		return false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			x = camera.getInputX();
			y = camera.getInputY();

			rectSelectionTouchDraggedResult = rectangularSelection.touchDragged(x, y);
		}

		if (Gdx.input.isButtonPressed(Buttons.RIGHT))
			cameraDragged = true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (button == Buttons.RIGHT && cameraDragged == false) {
			if (isMouseInsideSelectedEntities(x, y) == false)
				if (UIUtils.ctrl() == false) entityManipulator.softSelectionReset();

			EntityProxy result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null && entityManipulator.isSelected(result) == false)
				entityManipulator.selectAppend(result);

			mouseInsideSelected = true;
		}

		if (button == Buttons.LEFT && dragged == false && mouseInsideSelected == false) {
			EntityProxy result = findEntityWithSmallestSurfaceArea(x, y);
			if (result != null)
				entityManipulator.deselect(result);
		}

		rectangularSelection.touchUp();

		resetAfterTouchUp();
	}

	protected void resetAfterTouchUp () {
		lastTouchX = 0;
		lastTouchY = 0;
		dragStartX = 0;
		dragStartY = 0;
		mouseInsideSelected = false;
		dragging = false;
		dragged = false;
		cameraDragged = false;
	}

	/**
	 * Returns entity with smallest surface area that contains point x,y.
	 * <p>
	 * When selecting entities, and few of them are overlapping, selecting entity with smallest
	 * area gives better results than just selecting first one.
	 */
	protected EntityProxy findEntityWithSmallestSurfaceArea (float x, float y) {
		EntityProxy matchingEntity = null;
		float lastSurfaceArea = Float.MAX_VALUE;

		for (EntityProxy entity : entityProxyCache.getCache().values()) {
			Rectangle entityBoundingRectangle = entity.getBoundingRectangle();
			if (entityBoundingRectangle.contains(x, y)) {

				float currentSurfaceArea = entityBoundingRectangle.width * entityBoundingRectangle.height;

				if (currentSurfaceArea < lastSurfaceArea) {
					if (scene.getLayerById(entity.getLayerID()).locked)
						continue;

					matchingEntity = entity;
					lastSurfaceArea = currentSurfaceArea;
				}
			}
		}

		return matchingEntity;
	}

	protected boolean isMouseInsideSelectedEntities (float x, float y) {
		for (EntityProxy entity : entityManipulator.getSelectedEntities()) {
			if (entity.getBoundingRectangle().contains(x, y)) {
				EntityProxy result = findEntityWithSmallestSurfaceArea(x, y);
				if (result == entity) return true;
			}
		}

		return false;
	}
}
