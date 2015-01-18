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

public class ObjectSelectorModule extends SceneModule {
	private ObjectSelectorListener listener;

	private CameraModule camera;
	private RendererModule renderer;
	private UndoModule undoModule;

	private ShapeRenderer shapeRenderer;

	private Array<SceneObject> objects;

	private Array<Object2d> selectedObjects;


	@Override
	public void added () {
		this.objects = scene.objects;
		camera = sceneContainer.get(CameraModule.class);
		renderer = sceneContainer.get(RendererModule.class);
		undoModule = sceneContainer.get(UndoModule.class);

		shapeRenderer = renderer.getShapeRenderer();

		selectedObjects = new Array<>();
	}

	@Override
	public void init () {
	}

	@Override
	public void dispose () {
	}

	@Override
	public void render (Batch batch) {
		if (selectedObjects.size > 0) {
			batch.end();

			Rectangle bounds = selectedObjects.get(0).sprite.getBoundingRectangle();

			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
			shapeRenderer.end();
			Gdx.gl.glLineWidth(1);

			batch.begin();
		}
	}


	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		if (button == Buttons.LEFT) {
			x = camera.getInputX();
			y = camera.getInputY();

			selectedObjects.clear();
			Object2d result = findObjectWithSmallestSurfaceArea(x, y);
			if (result != null) selectedObjects.add(result);
			return true;
		}

		return false;
	}


	private Object2d findObjectWithSmallestSurfaceArea (float x, float y) {
		Object2d matchingObject = null;
		float lastSurfaceArea = Float.MAX_VALUE;

		for (SceneObject object : objects) {

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

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.FORWARD_DEL) { //Delete
			undoModule.execute(new ObjectsRemoved(selectedObjects));
			selectedObjects.clear();

			return true;
		}

		return false;
	}

	public void setListener (ObjectSelectorListener listener) {
		this.listener = listener;
	}

	public interface ObjectSelectorListener {
		public void selected (Array<Object2d> selected);
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
