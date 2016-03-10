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

import com.artemis.Entity;
import com.badlogic.gdx.utils.ObjectSet;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Allows to undo adding entity to {@link EntityEngine}. This is created after entity has been added to engine.
 * @author Kotcrab
 */
public class EntitiesAddedAction extends AbstractEntityLifecycleAction {
	public EntitiesAddedAction (SceneModuleContainer sceneMC, EntityEngine engine, ObjectSet<Entity> newEntities) {
		super(sceneMC, engine, newEntities);
	}

	public EntitiesAddedAction (SceneModuleContainer sceneMC, EntityEngine engine, Entity entity) {
		super(sceneMC, engine, entity);

	}

	@Override
	public void execute () {
		addEntitiesFromStoredSchemes();
	}

	@Override
	public void undo () {
		removeEntitiesFromEngine();
	}

	@Override
	public String getActionName () {
		return getSchemesCount() == 1 ? "Add Entity" : "Add Entities";
	}
}
