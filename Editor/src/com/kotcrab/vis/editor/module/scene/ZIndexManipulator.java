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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;

public class ZIndexManipulator extends SceneModule {
	private ObjectManipulatorModule objectManipulator;

	@Override
	public void init () {
		objectManipulator = sceneContainer.get(ObjectManipulatorModule.class);
	}

	private void moveSelectedObjects (boolean up) {
		Array<Object2d> selectedObjects = objectManipulator.getSelectedObjects();

		for (Object2d object : selectedObjects) {
			moveObject(object, getOverlappingObjects(object, up), up);
		}
	}

	private void moveObject (Object2d object, Array<Object2d> overlappingObjects, boolean up) {

		if (overlappingObjects.size > 0) {
			int currentIndex = scene.objects.indexOf(object, true);
			int targetIndex = scene.objects.indexOf(overlappingObjects.first(), true);

			for (Object2d obj : overlappingObjects) {
				int sceneIndex = scene.objects.indexOf(obj, true);
				if (up ? sceneIndex < targetIndex : sceneIndex > targetIndex)
					targetIndex = sceneIndex;
			}

			scene.objects.removeIndex(currentIndex);
			scene.objects.insert(targetIndex, object);
		}
	}

	private Array<Object2d> getOverlappingObjects (Object2d object, boolean up) {
		Array<Object2d> overlapping = new Array<>();
		int objectIndex = scene.objects.indexOf(object, true);

		for (SceneObject o : scene.objects) {
			if (o instanceof Object2d) {
				Object2d sceneObject = (Object2d) o;
				int sceneObjectIndex = scene.objects.indexOf(sceneObject, true);

				if (object != sceneObject &&
						object.sprite.getBoundingRectangle().overlaps(sceneObject.sprite.getBoundingRectangle())) {

					if (up ? (objectIndex < sceneObjectIndex) : (objectIndex > sceneObjectIndex))
						overlapping.add(sceneObject);

				}
			}
		}

		return overlapping;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.PAGE_UP) {
			moveSelectedObjects(true);
			return true;
		}

		if (keycode == Keys.PAGE_DOWN) {
			moveSelectedObjects(false);
			return true;
		}

		return false;
	}
}
