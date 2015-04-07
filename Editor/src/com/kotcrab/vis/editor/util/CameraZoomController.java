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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.ModuleInput;

public class CameraZoomController {
	private Vector3 unprojectVec;
	private final OrthographicCamera camera;

	public CameraZoomController (OrthographicCamera camera, Vector3 unprojectVec) {
		this.camera = camera;
		this.unprojectVec = unprojectVec;
	}

	/**
	 * Zooms camera around given point
	 * @param x screen x
	 * @param y screen y
	 * @param amount from {@link ModuleInput#scrolled(InputEvent, float, float, int)}
	 */
	public boolean zoomAroundPoint (float x, float y, int amount) {
		float newZoom = 0;
		camera.unproject(unprojectVec.set(x, y, 0));
		float cursorX = unprojectVec.x;
		float cursorY = unprojectVec.y;

		if (amount == -1) { // zoom in
			if (camera.zoom <= 0.3f) return false;
			newZoom = camera.zoom - 0.1f * camera.zoom * 2;

		}

		if (amount == 1) { // zoom out
			if (camera.zoom >= 10f) return false;
			newZoom = camera.zoom + 0.1f * camera.zoom * 2;
		}

		// some complicated calculations, basically we want to zoom in/out where mouse pointer is
		camera.position.x = cursorX + (newZoom / camera.zoom) * (camera.position.x - cursorX);
		camera.position.y = cursorY + (newZoom / camera.zoom) * (camera.position.y - cursorY);
		camera.zoom = newZoom;

		return true;
	}
}
