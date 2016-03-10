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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;

public class EntityMoveTimerTask extends Task {
	public static final int UP = 0x0001;
	public static final int DOWN = 0x0002;
	public static final int LEFT = 0x0004;
	public static final int RIGHT = 0x0008;

	private final EditorScene scene;
	private final EntityManipulatorModule entityManipulator;

	private int direction;
	private float delta;

	public EntityMoveTimerTask (EditorScene scene, EntityManipulatorModule entityManipulator) {
		this.scene = scene;
		this.entityManipulator = entityManipulator;
	}

	@Override
	public void run () {
		if (scene.getActiveLayer().locked) return;

		for (EntityProxy entity : entityManipulator.getSelectedEntities()) {
			if ((direction & UP) != 0) entity.setY(entity.getY() + delta);
			if ((direction & DOWN) != 0) entity.setY(entity.getY() + delta * -1);
			if ((direction & LEFT) != 0) entity.setX(entity.getX() + delta * -1);
			if ((direction & RIGHT) != 0) entity.setX(entity.getX() + delta);
		}

		entityManipulator.selectedEntitiesChanged();
	}

	public void set (int direction, float delta) {
		this.direction = direction;
		this.delta = delta;
	}
}
