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

package com.kotcrab.vis.editor.module.physicseditor.input;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.ModuleInput;
import com.kotcrab.vis.editor.module.physicseditor.PCameraModule;
import com.kotcrab.vis.editor.module.physicseditor.PRigidBodiesScreen;
import com.kotcrab.vis.editor.module.physicseditor.models.RigidBodyModel;

/** @author Aurelien Ribon, Kotcrab */
public class TestInputProcessor implements ModuleInput {
	private PCameraModule cameraModule;
	private PRigidBodiesScreen screen;

	private boolean touchDown = false;

	public TestInputProcessor (PCameraModule cameraModule, PRigidBodiesScreen screen) {
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
