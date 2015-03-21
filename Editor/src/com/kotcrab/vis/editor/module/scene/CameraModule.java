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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.project.SceneMetadataModule;

public class CameraModule extends SceneModule {
	private SceneMetadataModule metadataModule;

	private OrthographicCamera camera;

	private Vector3 unprojectVec;

	private SceneMetadata metadata;

	@Override
	public void added () {
		unprojectVec = new Vector3();
		camera = new OrthographicCamera();

		metadataModule = projectContainer.get(SceneMetadataModule.class);
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
	public void resize () {
		Vector3 oldPos = camera.position.cpy();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(oldPos);
	}

	@Override
	public void render (Batch batch) {
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
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

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		return true;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) pan(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
	}

	private void pan (float deltaX, float deltaY) {
		camera.position.x = camera.position.x - deltaX * camera.zoom;
		camera.position.y = camera.position.y + deltaY * camera.zoom;
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
		return camera.unproject(vector);
	}

	public float getInputX () {
		unprojectVec.x = Gdx.input.getX();
		camera.unproject(unprojectVec);
		return unprojectVec.x;
	}

	public float getInputY () {
		unprojectVec.y = Gdx.input.getY();
		camera.unproject(unprojectVec);
		return unprojectVec.y;
	}

	public void setPosition (float x, float y) {
		camera.position.set(x, y, 0);
	}
}
