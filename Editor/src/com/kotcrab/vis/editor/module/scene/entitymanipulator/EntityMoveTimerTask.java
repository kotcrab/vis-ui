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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.gdx.ImmutableArray;

public class EntityMoveTimerTask extends Task {
	private final EditorScene scene;
	private final ImmutableArray<EntityProxy> selectedEntities;

	public enum Direction {UP, DOWN, LEFT, RIGHT}

	private Direction dir;
	private int delta;

	public EntityMoveTimerTask (EditorScene scene, ImmutableArray<EntityProxy> selectedEntities) {
		this.scene = scene;
		this.selectedEntities = selectedEntities;
	}

	@Override
	public void run () {
		if (scene.getActiveECSLayer().locked) return;

		for (EntityProxy entity : selectedEntities) {
			switch (dir) {
				case UP:
					entity.setY(entity.getY() + delta);
					break;
				case DOWN:
					entity.setY(entity.getY() + delta * -1);
					break;
				case LEFT:
					entity.setX(entity.getX() + delta * -1);
					break;
				case RIGHT:
					entity.setX(entity.getX() + delta);
					break;
			}
		}
	}

	public void set (Direction dir, int delta) {
		this.dir = dir;
		this.delta = delta;
	}
}
