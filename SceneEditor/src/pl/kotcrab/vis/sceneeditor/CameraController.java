/*******************************************************************************
 * Copyright 2013-2014 Pawel Pastuszak
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

package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/** Class for caluclating propper touch cordinates
 * 
 * @author Pawel Pastuszak */
class CameraController {
	private OrthographicCamera camera;
	private OrthographicCamera orginalCamera;
	private Vector3 calcVector;

	/** Prepares class for use */
	public CameraController (OrthographicCamera camera) {
		this.camera = camera;

		orginalCamera = new OrthographicCamera(camera.viewportWidth, camera.viewportHeight);
		orginalCamera.position.x = camera.position.x;
		orginalCamera.position.y = camera.position.y;
		orginalCamera.zoom = camera.zoom;
		orginalCamera.update();

		calcVector = new Vector3(0, 0, 0);
	}

	/** Return camera */
	public OrthographicCamera getCamera () {
		return camera;
	}

	public void switchCameraProperties () {
		float x = camera.position.x;
		float y = camera.position.y;
		float zoom = camera.zoom;

		camera.position.x = orginalCamera.position.x;
		camera.position.y = orginalCamera.position.y;
		camera.zoom = orginalCamera.zoom;

		orginalCamera.position.x = x;
		orginalCamera.position.y = y;
		orginalCamera.zoom = zoom;
	}

	public void restoreOrginalCameraProperties () {
		camera.position.x = orginalCamera.position.x;
		camera.position.y = orginalCamera.position.y;
		camera.zoom = orginalCamera.zoom;
	}

// public void restoreCameraProperties()
// {
// orginalCamera.position.x = camera.position.x;
// orginalCamera.position.y = camera.position.y;
// orginalCamera.zoom = camera.zoom;
// }

	public boolean isCameraDirty () {
		return camera.position.x != orginalCamera.position.x || camera.position.y != orginalCamera.position.y
			|| camera.zoom != orginalCamera.zoom;
	}

	public Rectangle getOrginalCameraRectangle () {
		return new Rectangle(orginalCamera.position.x - orginalCamera.viewportWidth / 2, orginalCamera.position.y
			- orginalCamera.viewportHeight / 2, orginalCamera.viewportWidth, orginalCamera.viewportHeight);
	}

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param x form Gdx.input.getX() or event method */
	public float calcX (float x) {
		calcVector.x = x;
		camera.unproject(calcVector);
		return calcVector.x;
	}

	/** Return proper touch posistion using provided camera<br>
	 * 
	 * @param y form Gdx.input.getY() or event method */
	public float calcY (float y) {
		calcVector.y = y;
		camera.unproject(calcVector);
		return calcVector.y;
	}

	public float getX () {
		return calcX(Gdx.input.getX());
	}

	public float getY () {
		return calcY(Gdx.input.getY());
	}
}
