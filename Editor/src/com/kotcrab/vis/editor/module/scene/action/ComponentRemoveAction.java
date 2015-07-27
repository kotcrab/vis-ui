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

package com.kotcrab.vis.editor.module.scene.action;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.entity.UUIDComponent;
import com.kotcrab.vis.editor.module.scene.VisComponentManipulator;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

import java.util.UUID;

/** @author Kotcrab */
public class ComponentRemoveAction implements UndoableAction {
	private VisComponentManipulator componentManipulator;
	private final EntityProxy target;
	private final Class<? extends Component> componentClazz;

	private ObjectMap<UUID, Component> componentMap = new ObjectMap<>();

	public ComponentRemoveAction (VisComponentManipulator componentManipulator, EntityProxy target, Class<? extends Component> componentClazz) {
		this.componentManipulator = componentManipulator;
		this.target = target;
		this.componentClazz = componentClazz;
	}

	@Override
	public void execute () {
		target.reload();

		for (Entity entity : target.getEntities()) {
			UUIDComponent uuid = entity.getComponent(UUIDComponent.class);
			Component component = entity.getComponent(componentClazz);

			componentMap.put(uuid.getUuid(), component);

			componentManipulator.addJob(entity, component, false);
		}

	}

	@Override
	public void undo () {
		target.reload();

		for (Entity entity : target.getEntities()) {
			UUIDComponent uuid = entity.getComponent(UUIDComponent.class);
			componentManipulator.addJob(entity, componentMap.get(uuid.getUuid()), true);
		}
	}
}
