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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.entity.EntityScheme.UUIDPolicy;
import com.kotcrab.vis.editor.module.editor.ClonerModule;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.system.VisUUIDManager;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.util.EntityEngine;

/** @author Kotcrab */
public abstract class AbstractEntityLifecycleAction implements UndoableAction {
	private final SceneModuleContainer sceneMC;
	private EntityManipulatorModule entityManipulator;
	private SceneIOModule sceneIO;
	private ClonerModule cloner;

	private EntityEngine engine;
	private VisUUIDManager uuidManager;

	private Array<EntityScheme> schemes = new Array<>();

	public AbstractEntityLifecycleAction (SceneModuleContainer sceneMC, EntityEngine engine, Entity entity) {
		this.sceneMC = sceneMC;
		this.engine = engine;
		sceneMC.injectModules(this);

		schemes.add(EntityScheme.clonedOf(entity, cloner.getCloner()));
	}

	public AbstractEntityLifecycleAction (SceneModuleContainer sceneMC, EntityEngine engine, ObjectSet<Entity> newEntities) {
		this.sceneMC = sceneMC;
		this.engine = engine;
		sceneMC.injectModules(this);

		newEntities.forEach(entity -> schemes.add(EntityScheme.clonedOf(entity, cloner.getCloner())));
	}

	protected void removeEntitiesFromEngine () {
		schemes.forEach(scheme -> uuidManager.get(scheme.getSchemeUUID()).deleteFromWorld());

		sceneMC.updateEntitiesStates();

		entityManipulator.softSelectionReset();
		entityManipulator.markSceneDirty();
	}

	protected void addEntitiesFromStoredSchemes () {
		Array<Entity> entities = new Array<>();

		schemes.forEach(scheme -> {
			entities.add(scheme.build(engine, cloner.getCloner(), UUIDPolicy.PRESERVE)); //build will also add to entity engine
		});

		sceneMC.updateEntitiesStates();

		entityManipulator.softSelectionReset();
		entities.forEach(entityManipulator::selectAppend);
		entityManipulator.markSceneDirty();

		entities.clear();
	}

	protected int getSchemesCount () {
		return schemes.size;
	}
}
