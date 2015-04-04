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

package com.kotcrab.vis.editor.module.physicseditor.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.PCameraModule;
import com.kotcrab.vis.editor.module.physicseditor.PRigidBodiesScreen;
import com.kotcrab.vis.editor.module.physicseditor.PhysicsEditorSettings;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @author Aurelien Ribon, Kotcrab */
public class EditionInputProcessor implements ModuleInput {
	private PCameraModule cameraModule;
	private PRigidBodiesScreen screen;
	private PhysicsEditorSettings settings;

	private boolean touchDown = false;
	private Vector2 draggedPoint;

	public EditionInputProcessor (PCameraModule cameraModule, PRigidBodiesScreen screen, PhysicsEditorSettings settings) {
		this.cameraModule = cameraModule;
		this.screen = screen;
		this.settings = settings;
	}

	private List<Vector2> getPointsInSelection () {
		RigidBodyModel model = screen.getSelectedModel();
		List<Vector2> points = new ArrayList<Vector2>();
		Vector2 p1 = screen.mouseSelectionP1;
		Vector2 p2 = screen.mouseSelectionP2;

		if (p1 != null && p2 != null) {
			Rectangle rect = new Rectangle(
					Math.min(p1.x, p2.x),
					Math.min(p1.y, p2.y),
					Math.abs(p2.x - p1.x),
					Math.abs(p2.y - p1.y)
			);

			for (Vector2 p : getAllPoints()) {
				if (p == model.getOrigin()) continue;
				if (rect.contains(p.x, p.y)) points.add(p);
			}
		}

		return Collections.unmodifiableList(points);
	}

	private Array<Vector2> getAllPoints () {
		Array<Vector2> points = new Array<>();
		RigidBodyModel model = screen.getSelectedModel();

		for (ShapeModel shape : model.getShapes()) {
			points.addAll(shape.getVertices());
		}

		points.add(model.getOrigin());
		return new Array<>(points);
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = button == Buttons.LEFT;
		if (!touchDown) return true;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return true;

		draggedPoint = screen.nearestPoint;

		if (draggedPoint == null) {
			screen.mouseSelectionP1 = cameraModule.screenToWorld(x, y);

		} else {
			if (draggedPoint == model.getOrigin()) {
				screen.selectedPoints.clear();
			} else if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
				if (screen.selectedPoints.contains(draggedPoint)) screen.selectedPoints.remove(draggedPoint);
				else screen.selectedPoints.add(draggedPoint);
			} else if (!screen.selectedPoints.contains(draggedPoint)) {
				screen.selectedPoints.replaceBy(draggedPoint);
			}
		}

		return true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (!touchDown) return;
		touchDown = false;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return;

		if (draggedPoint != null) {
			draggedPoint = null;
			model.computePhysics(settings.polygonizer);
			screen.buildBody();

		} else if (screen.mouseSelectionP2 != null) {
			if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
				for (Vector2 p : getPointsInSelection()) {
					if (screen.selectedPoints.contains(p)) screen.selectedPoints.remove(p);
					else screen.selectedPoints.add(p);
				}
			} else {
				screen.selectedPoints.replaceBy(getPointsInSelection());
			}

		} else {
			screen.selectedPoints.clear();
		}

		screen.mouseSelectionP1 = null;
		screen.mouseSelectionP2 = null;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (!touchDown) return;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return;

		if (draggedPoint != null) {
			Vector2 p = cameraModule.alignedScreenToWorld(x, y);
			model.clearPhysics();

			float dx = p.x - draggedPoint.x;
			float dy = p.y - draggedPoint.y;
			draggedPoint.add(dx, dy);

			for (int i = 0; i < screen.selectedPoints.size(); i++) {
				Vector2 sp = screen.selectedPoints.get(i);
				if (sp != draggedPoint) sp.add(dx, dy);
			}

		} else {
			screen.mouseSelectionP2 = cameraModule.screenToWorld(x, y);
		}
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return false;

		// Nearest point computation

		Vector2 p = cameraModule.screenToWorld(x, y);
		screen.nearestPoint = null;
		float dist = 0.025f * cameraModule.getCamera().zoom;

		for (Vector2 v : getAllPoints()) {
			if (v.dst(p) < dist) screen.nearestPoint = v;
		}

		return false;
	}

	@Override
	public boolean scrolled (InputEvent event, float x, float y, int amount) {
		return false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		switch (keycode) {
			case Keys.ENTER:
				screen.insertPointsBetweenSelected();
				break;
			case Keys.BACKSPACE:
				screen.removeSelectedPoints();
				break;
		}

		return false;
	}

}
