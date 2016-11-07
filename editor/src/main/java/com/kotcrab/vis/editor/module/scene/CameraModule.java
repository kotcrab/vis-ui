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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.editor.module.project.SceneMetadataModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.util.gdx.CameraZoomController;
import com.kotcrab.vis.editor.util.gdx.RepeatableTimedKey;
import com.kotcrab.vis.editor.util.gdx.RepeatableTimedMove;
import com.kotcrab.vis.runtime.system.CameraManager;
import kotlin.Unit;

/**
 * Manages scene camera
 * @author Kotcrab
 */
public class CameraModule extends SceneModule {
	private EntityManipulatorModule entityManipulator;
	private SceneMetadataModule metadataModule;

	private CameraManager manager;

	private SceneMetadata metadata;

	private Stage stage;
	private OrthographicCamera camera;
	private Viewport viewport;
	private CameraZoomController zoomController;

	private Vector3 unprojectVec = new Vector3();

	private RepeatableTimedMove moveCameraTask;
	private RepeatableTimedKey zoomInKeyTask;
	private RepeatableTimedKey zoomOutKeyTask;

	@Override
	public void init () {
		camera = manager.getCamera();
		viewport = manager.getViewport();
		zoomController = new CameraZoomController(camera, unprojectVec);
		moveCameraTask = new RepeatableTimedMove(stage, scene.pixelsPerUnit,
				() -> entityManipulator.getSelectedEntities().size() != 0,
				(deltaX, deltaY) -> {
					pan(-deltaX, deltaY);
					return Unit.INSTANCE;
				});
		zoomInKeyTask = new RepeatableTimedKey(stage, Keys.PERIOD, () -> zoomController.zoomIn());
		zoomOutKeyTask = new RepeatableTimedKey(stage, Keys.COMMA, () -> zoomController.zoomOut());

		metadata = metadataModule.getMap().get(scene.path);

		if (metadata == null) {
			reset();
			metadata = new SceneMetadata(camera.position.x, camera.position.y, camera.zoom);
			metadataModule.getMap().put(scene.path, metadata);
		} else {
			camera.position.x = metadata.lastCameraX;
			camera.position.y = metadata.lastCameraY;
			camera.zoom = metadata.lastCameraZoom;
		}
	}

	public void reset () {
		camera.position.x = scene.width / 2;
		camera.position.y = scene.height / 2;
		camera.zoom = 1;
	}

	public void resetZoom () {
		camera.zoom = 1;
	}

	@Override
	public void save () {
		saveToMetadata();
	}

	@Override
	public void dispose () {
		saveToMetadata();
		metadataModule.save();
	}

	private void saveToMetadata () {
		metadata.lastCameraX = camera.position.x;
		metadata.lastCameraY = camera.position.y;
		metadata.lastCameraZoom = camera.zoom;
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return zoomController.zoomAroundPoint(x, y, amount != 1);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			pan();
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return true;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) pan();
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (button == Buttons.MIDDLE) {
			resetZoom();
			camera.position.x = getInputX();
			camera.position.y = getInputY();
		}
	}

	private void pan () {
		pan(Gdx.input.getDeltaX() / scene.pixelsPerUnit, Gdx.input.getDeltaY() / scene.pixelsPerUnit);
	}

	private void pan (float deltaX, float deltaY) {
		camera.position.x = camera.position.x - deltaX * camera.zoom;
		camera.position.y = camera.position.y + deltaY * camera.zoom;
	}

	@Override
	public void render (Batch batch) {
		super.render(batch);
		moveCameraTask.update();
		zoomInKeyTask.update();
		zoomOutKeyTask.update();
	}

	public Matrix4 getCombinedMatrix () {
		return camera.combined;
	}

	public float getX () {
		return camera.position.x;
	}

	public float getY () {
		return camera.position.y;
	}

	public float getHeight () {
		return camera.viewportHeight * camera.zoom;
	}

	public float getWidth () {
		return camera.viewportWidth * camera.zoom;
	}

	public float getZoom () {
		return camera.zoom;
	}

	public Vector3 unproject (Vector3 vector) {
		return viewport.unproject(vector);
	}

	public Vector3 project (Vector3 vector3) {
		return viewport.project(vector3);
	}

	public float getInputX () {
		unprojectVec.x = Gdx.input.getX();
		viewport.unproject(unprojectVec);
		return unprojectVec.x;
	}

	public float getInputY () {
		unprojectVec.y = Gdx.input.getY();
		viewport.unproject(unprojectVec);
		return unprojectVec.y;
	}

	public void setPosition (float x, float y) {
		camera.position.set(x, y, 0);
	}

	public void update () {
		camera.update();
	}
}
