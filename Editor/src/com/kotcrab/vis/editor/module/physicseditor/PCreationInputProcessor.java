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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;
import com.kotcrab.vis.editor.module.physicseditor.models.ShapeModel;

import java.util.List;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class PCreationInputProcessor implements ModuleInput {
	private PCameraModule cameraModule;
	private final PRigidBodiesScreen screen;
	private PhysicsEditorSettings settings;
	private boolean touchDown = false;

	public PCreationInputProcessor (PCameraModule cameraModule, PRigidBodiesScreen screen, PhysicsEditorSettings settings) {
		this.cameraModule = cameraModule;
		this.screen = screen;
		this.settings = settings;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = button == Buttons.LEFT;
		if (!touchDown) return true;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return true;

		List<ShapeModel> shapes = model.getShapes();
		ShapeModel lastShape = shapes.isEmpty() ? null : shapes.get(shapes.size() - 1);

		if (lastShape == null || lastShape.isClosed()) {
			ShapeModel.Type type = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? ShapeModel.Type.CIRCLE : ShapeModel.Type.POLYGON;
			lastShape = new ShapeModel(type);
			lastShape.getVertices().add(cameraModule.alignedScreenToWorld(x, y));
			shapes.add(lastShape);

		} else {
			List<Vector2> vs = lastShape.getVertices();
			Vector2 np = screen.nearestPoint;
			ShapeModel.Type type = lastShape.getType();

			if (type == ShapeModel.Type.POLYGON && vs.size() >= 3 && np == vs.get(0)) {
				lastShape.close();
				model.computePhysics(settings.polygonizer);
				screen.buildBody();
			} else if (type == ShapeModel.Type.CIRCLE) {
				vs.add(cameraModule.alignedScreenToWorld(x, y));
				lastShape.close();
				model.computePhysics(settings.polygonizer);
				screen.buildBody();
			} else {
				vs.add(cameraModule.alignedScreenToWorld(x, y));
			}
		}

		return true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = false;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (!touchDown) return;
		mouseMoved(event, x, y);
	}

	@Override
	public boolean mouseMoved (InputEvent event, float x, float y) {
		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return false;

		// Nearest point computation

		screen.nearestPoint = null;
		Vector2 p = cameraModule.screenToWorld(x, y);

		List<ShapeModel> shapes = model.getShapes();
		ShapeModel lastShape = shapes.isEmpty() ? null : shapes.get(shapes.size() - 1);

		if (lastShape != null) {
			List<Vector2> vs = lastShape.getVertices();
			float zoom = cameraModule.getCamera().zoom;

			if (!lastShape.isClosed() && vs.size() >= 3)
				if (vs.get(0).dst(p) < 0.025f * zoom)
					screen.nearestPoint = vs.get(0);
		}

		// Next point assignment

		screen.nextPoint = cameraModule.alignedScreenToWorld(x, y);
		return false;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.ESCAPE:
				RigidBodyModel model = screen.getSelectedModel();
				if (model == null) break;
				if (model.getShapes().isEmpty()) break;
				if (model.getShapes().get(model.getShapes().size() - 1).isClosed()) break;
				model.getShapes().remove(model.getShapes().size() - 1);
				break;
		}
		return false;
	}
}
