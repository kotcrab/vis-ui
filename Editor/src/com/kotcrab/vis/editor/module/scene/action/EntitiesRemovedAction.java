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

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.vis.ProtoEntity;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Performs removing entities from engine and allows to undo this operation.
 * @author Kotcrab
 */
public class EntitiesRemovedAction implements UndoableAction {
	@InjectModule private EntityManipulatorModule entityManipulator;
	@InjectModule private SceneIOModule sceneIO;

	private EntityEngine engine;
	private ObjectSet<Entity> entities;

	private Array<ProtoEntity> protoEntities = new Array<>();

	public EntitiesRemovedAction (ModuleInjector injector, EntityEngine engine, Entity entity) {
		injector.injectModules(this);
		this.engine = engine;
		this.entities = new ObjectSet<>();
		entities.add(entity);
	}

	public EntitiesRemovedAction (ModuleInjector injector, EntityEngine engine, ObjectSet<Entity> newEntities) {
		injector.injectModules(this);
		this.engine = engine;
		this.entities = new ObjectSet<>(newEntities);
	}

	@Override
	public void execute () {
		protoEntities.clear();

		entities.forEach((entity) -> {
			protoEntities.add(sceneIO.createProtoEntity(engine, entity, true));
			entity.deleteFromWorld();
		});

		entityManipulator.resetSelection();
		entityManipulator.markSceneDirty();
	}

	@Override
	public void undo () {
		entities.clear();

		protoEntities.forEach(protoEntity -> {
			entities.add(protoEntity.build()); //build will also add to entity engine
		});

		entityManipulator.resetSelection();
		entities.forEach(entityManipulator::selectAppend);
		entityManipulator.markSceneDirty();
	}
}
