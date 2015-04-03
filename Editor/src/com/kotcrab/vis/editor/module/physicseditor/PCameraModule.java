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

package com.kotcrab.vis.editor.module.physicseditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class PCameraModule extends PhysicsEditorModule {
	private OrthographicCamera camera;

	private Vector3 unprojectVec;

	private PhysicsEditorSettings settings;

	@Override
	public void added () {
		unprojectVec = new Vector3();
		camera = new OrthographicCamera();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		camera.viewportWidth = w / 400;
		camera.viewportHeight = w / 400 * h / w;
		camera.position.set(0.5f, 0.5f, 0);
		camera.update();
	}

	@Override
	public void init () {
		settings = physicsContainer.get(PSettingsModule.class).getSettings();
	}

	@Override
	public void resize () {
		Vector3 oldPos = camera.position.cpy();
		camera.position.set(oldPos);
	}

	@Override
	public void render (Batch batch) {
		camera.update();
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
		camera.position.x = camera.position.x - deltaX * camera.zoom / 400.0f;
		camera.position.y = camera.position.y + deltaY * camera.zoom / 400.0f;
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

	public OrthographicCamera getCamera () {
		return camera;
	}

	public Vector2 screenToWorld (float x, float y) {
		Vector3 v3 = new Vector3(x, y, 0);
		camera.unproject(v3);
		return new Vector2(v3.x, v3.y);
	}

	public Vector2 alignedScreenToWorld (float x, float y) {
		Vector2 p = screenToWorld(x, y);
		if (settings.isSnapToGridEnabled) {
			float gap = settings.gridGap;
			p.x = Math.round(p.x / gap) * gap;
			p.y = Math.round(p.y / gap) * gap;
		}
		return p;
	}
}
