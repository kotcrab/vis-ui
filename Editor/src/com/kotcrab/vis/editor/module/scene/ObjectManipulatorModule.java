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
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ColorPickerModule;
import com.kotcrab.vis.editor.ui.scene.ObjectProperties;

public class ObjectManipulatorModule extends SceneModule {
	private CameraModule camera;
	private UndoModule undoModule;

	private ShapeRenderer shapeRenderer;

	private ObjectProperties objectProperties;

	private Array<EditorSceneObject> objects;
	private Array<Object2d> selectedObjects = new Array<>();

	private float lastTouchX;
	private float lastTouchY;

	private boolean selected;
	private boolean dragging;
	private boolean dragged;

	@Override
	public void added () {
		this.objects = scene.objects;

		shapeRenderer = sceneContainer.get(RendererModule.class).getShapeRenderer();
		camera = sceneContainer.get(CameraModule.class);
		undoModule = sceneContainer.get(UndoModule.class);

		ColorPickerModule pickerModule = container.get(ColorPickerModule.class);
		objectProperties = new ObjectProperties(pickerModule.getPicker(), sceneTab);
	}

	@Override
	public void render (Batch batch) {
		if (selectedObjects.size > 0) {
			batch.end();

			shapeRenderer.setProjectionMatrix(camera.getCombinedMatrix());
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);

			for (Object2d object : selectedObjects) {
				Rectangle bounds = object.sprite.getBoundingRectangle();
				shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			}

			shapeRenderer.end();

			batch.begin();
		}
	}

	public ObjectProperties getObjectProperties () {
		return objectProperties;
	}

	private boolean isMouseInsideSelectedObjects (float x, float y) {
		for (Object2d object : selectedObjects)
			if (object.sprite.getBoundingRectangle().contains(x, y)) return true;

		return false;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (button == Buttons.LEFT) {
			dragging = true;
			lastTouchX = x;
			lastTouchY = y;

			if (isMouseInsideSelectedObjects(x, y) == false) {
				//multiple select made easy
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) == false) selectedObjects.clear();

				Object2d result = findObjectWithSmallestSurfaceArea(x, y);
				if (result != null && selectedObjects.contains(result, true) == false)
					selectedObjects.add(result);

				objectProperties.setValuesToFields(selectedObjects);

				selected = true;
				return true;
			}
		}
		return false;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (dragged == false && selected == false) {
			Object2d result = findObjectWithSmallestSurfaceArea(x, y);
			if (result != null)
				selectedObjects.removeValue(result, true);

			objectProperties.setValuesToFields(selectedObjects);
		}

		lastTouchX = 0;
		lastTouchY = 0;
		selected = false;
		dragging = false;
		dragged = false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		x = camera.getInputX();
		y = camera.getInputY();

		if (dragging) {
			dragged = true;
			float deltaX = (x - lastTouchX);
			float deltaY = (y - lastTouchY);

			for (Object2d object : selectedObjects)
				object.sprite.translate(deltaX, deltaY);

			lastTouchX = x;
			lastTouchY = y;

			sceneTab.setDirty(true);
			objectProperties.updateValues();
		}
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.FORWARD_DEL) { //Delete
			undoModule.execute(new ObjectsRemoved(selectedObjects));
			selectedObjects.clear();
			objectProperties.setValuesToFields(selectedObjects);

			return true;
		}

		return false;
	}

	/**
	 * Returns object with smallest surface area that contains point x,y.
	 * <p/>
	 * When selecting objects, and few of them are overlapping, selecting object with smallest
	 * area gives better results than just selecting first one.
	 */
	private Object2d findObjectWithSmallestSurfaceArea (float x, float y) {
		Object2d matchingObject = null;
		float lastSurfaceArea = Float.MAX_VALUE;

		for (EditorSceneObject object : objects) {

			if (object instanceof Object2d) {
				Object2d object2d = (Object2d) object;

				if (object2d.sprite.getBoundingRectangle().contains(x, y)) {
					float currentSurfaceArea = object2d.sprite.getWidth() * object2d.sprite.getHeight();

					if (currentSurfaceArea < lastSurfaceArea) {
						matchingObject = object2d;
						lastSurfaceArea = currentSurfaceArea;
					}
				}
			}

		}

		return matchingObject;
	}

	public Array<Object2d> getSelectedObjects () {
		return selectedObjects;
	}

	public void select (Object2d object) {
		selectedObjects.clear();
		selectedObjects.add(object);
		objectProperties.setValuesToFields(selectedObjects);
	}

	private class ObjectsRemoved implements UndoableAction {
		private Array<Object2d> objects;

		public ObjectsRemoved (Array<Object2d> selectedObjects) {
			objects = new Array<>(selectedObjects);
		}

		@Override
		public void execute () {
			scene.objects.removeAll(objects, true);
		}

		@Override
		public void undo () {
			scene.objects.addAll(objects);
		}
	}
}
