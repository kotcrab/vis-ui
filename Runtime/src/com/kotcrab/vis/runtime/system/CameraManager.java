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

package com.kotcrab.vis.runtime.system;

import com.artemis.Manager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.runtime.scene.SceneViewport;

/** @author Kotcrab */
public class CameraManager extends Manager {
	private OrthographicCamera camera;
	private Viewport viewport;

	public CameraManager (SceneViewport viewportType, float width, float height, float pixelsPerUnit) {
		camera = new OrthographicCamera(width, height);
		camera.position.x = width / 2;
		camera.position.y = height / 2;
		camera.update();

		switch (viewportType) {
			case STRETCH:
				viewport = new StretchViewport(width, height, camera);
				break;
			case FIT:
				viewport = new FitViewport(width, height, camera);
				break;
			case FILL:
				viewport = new FillViewport(width, height, camera);
				break;
			case SCREEN:
				viewport = new ScreenViewport(camera);
				((ScreenViewport) viewport).setUnitsPerPixel(1f / pixelsPerUnit);
				break;
			case EXTEND:
				viewport = new ExtendViewport(width, height, camera);
				break;
		}
	}

	public Matrix4 getCombined () {
		return camera.combined;
	}

	public OrthographicCamera getCamera () {
		return camera;
	}

	public Viewport getViewport () {
		return viewport;
	}

	public void resize (int width, int height) {
		viewport.update(width, height);
	}
}
