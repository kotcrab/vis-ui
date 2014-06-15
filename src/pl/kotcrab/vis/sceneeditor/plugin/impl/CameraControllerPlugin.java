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

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.ICameraController;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/** Class for caluclating propper touch cordinates and controlling camera
 * 
 * @author Pawel Pastuszak */
public class CameraControllerPlugin extends PluginState implements ICameraController {
	private IObjectManager objectManager;

	private OrthographicCamera camera;
	private OrthographicCamera orginalCamera;
	private Vector3 calcVector;

	private boolean cameraLocked;

	public CameraControllerPlugin (IObjectManager objectManager, OrthographicCamera camera) {
		this.objectManager = objectManager;
		this.camera = camera;

		orginalCamera = new OrthographicCamera(camera.viewportWidth, camera.viewportHeight);
		orginalCamera.position.x = camera.position.x;
		orginalCamera.position.y = camera.position.y;
		orginalCamera.zoom = camera.zoom;
		orginalCamera.update();

		calcVector = new Vector3(0, 0, 0);
	}

	@Override
	public void enable () {
		switchCameraProperties();
	}

	@Override
	public void disable () {
		switchCameraProperties();
	}

	@Override
	public boolean scrolled (int amount) {
		if (state.editing && cameraLocked == false) {
			float newZoom = 0;
			float camX = getX();
			float camY = getY();

			if (amount == 1) // out
			{
				if (camera.zoom >= SceneEditorConfig.CAMERA_MAX_ZOOM_OUT) return false;

				newZoom = camera.zoom + 0.1f * camera.zoom * 2;

				// some complicated callucations, basicly we want to zoom in/out where mouse pointer is
				camera.position.x = camX + (camera.zoom / newZoom) * (camera.position.x - camX);
				camera.position.y = camY + (camera.zoom / newZoom) * (camera.position.y - camY);

				camera.zoom = newZoom;
			}

			if (amount == -1) // in
			{
				if (camera.zoom <= SceneEditorConfig.CAMERA_MAX_ZOOM_IN) return false;

				newZoom = camera.zoom - 0.1f * camera.zoom * 2;

				camera.position.x = camX + (newZoom / camera.zoom) * (camera.position.x - camX);
				camera.position.y = camY + (newZoom / camera.zoom) * (camera.position.y - camY);

				camera.zoom = newZoom;
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged (int sceneX, int sceneY, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.LEFT) && state.editing && cameraLocked == false
			&& objectManager.getSelectedObjs().size == 0) {
			pan(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
			return true;
		}
		return false;
	}

	@Override
	public boolean keyDown (int keycode) {
		if (keycode == SceneEditorConfig.KEY_LOCK_CAMERA) cameraLocked = !cameraLocked;
		if (keycode == SceneEditorConfig.KEY_RESET_CAMERA) restoreOrginalCameraProperties();

		return false;
	}

	/** Returns camera */
	public OrthographicCamera getCamera () {
		return camera;
	}

	public void pan (float deltaX, float deltaY) {
		if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_PRECISION_MODE)) {
			deltaX /= SceneEditorConfig.PRECISION_DIVIDE_BY;
			deltaY /= SceneEditorConfig.PRECISION_DIVIDE_BY;
		}

		camera.position.x = camera.position.x - deltaX * camera.zoom;
		camera.position.y = camera.position.y + deltaY * camera.zoom;

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

	@Override
	public Matrix4 getCombinedMatrix () {
		return camera.combined;
	}

	@Override
	public boolean isCameraDirty () {
		return camera.position.x != orginalCamera.position.x || camera.position.y != orginalCamera.position.y
			|| camera.zoom != orginalCamera.zoom;
	}

	@Override
	public boolean isCameraLocked () {
		return cameraLocked;
	}

	@Override
	public Rectangle getOriginalCameraRectangle () {
		return new Rectangle(orginalCamera.position.x - orginalCamera.viewportWidth / 2, orginalCamera.position.y
			- orginalCamera.viewportHeight / 2, orginalCamera.viewportWidth, orginalCamera.viewportHeight);
	}

	@Override
	public float calcX (float x) {
		calcVector.x = x;
		camera.unproject(calcVector);
		return calcVector.x;
	}

	@Override
	public float calcY (float y) {
		calcVector.y = y;
		camera.unproject(calcVector);
		return calcVector.y;
	}

	@Override
	public int calcX (int x) {
		calcVector.x = x;
		camera.unproject(calcVector);
		return (int)calcVector.x;
	}

	@Override
	public int calcY (int y) {
		calcVector.y = y;
		camera.unproject(calcVector);
		return (int)calcVector.y;
	}

	public float getX () {
		return calcX(Gdx.input.getX());
	}

	public float getY () {
		return calcY(Gdx.input.getY());
	}
}
