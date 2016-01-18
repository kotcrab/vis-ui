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

package com.kotcrab.vis.editor.module.scene.action;

import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.data.PhysicsSettings;

/** @author Kotcrab */
public class ChangePhysicsSettingsAction implements UndoableAction {
	private final EditorScene scene;

	private final PhysicsSettings oldPhysicsSettings;
	private final PhysicsSettings newPhysicsSettings;

	public ChangePhysicsSettingsAction (EditorScene scene, PhysicsSettings newPhysicsSettings) {
		this.scene = scene;
		this.newPhysicsSettings = newPhysicsSettings;

		oldPhysicsSettings = scene.physicsSettings;
	}

	@Override
	public void execute () {
		scene.physicsSettings = newPhysicsSettings;
	}

	@Override
	public void undo () {
		scene.physicsSettings = oldPhysicsSettings;
	}

	@Override
	public String getActionName () {
		return "Change Physics Settings";
	}
}
