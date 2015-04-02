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

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class PTestInputProcessor implements ModuleInput {
	private PCameraModule cameraModule;

	private final PRigidBodiesScreen screen;
	private boolean touchDown = false;

	public PTestInputProcessor (PCameraModule cameraModule, PRigidBodiesScreen screen) {
		this.cameraModule = cameraModule;
		this.screen = screen;
	}

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		touchDown = button == Buttons.LEFT;
		if (!touchDown) return false;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return false;

		Vector2 p = cameraModule.screenToWorld(x, y);
		screen.ballThrowP1 = p;
		screen.ballThrowP2 = p;
		return true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (!touchDown) return;
		touchDown = false;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return;

		Vector2 p1 = screen.ballThrowP1;
		Vector2 p2 = screen.ballThrowP2;
		Vector2 delta = new Vector2(p2).sub(p1);
		screen.fireBall(p1, delta.scl(3));

		screen.ballThrowP1 = null;
		screen.ballThrowP2 = null;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		if (!touchDown) return;

		RigidBodyModel model = screen.getSelectedModel();
		if (model == null) return;

		Vector2 p = cameraModule.screenToWorld(x, y);
		screen.ballThrowP2 = p;
	}
}
