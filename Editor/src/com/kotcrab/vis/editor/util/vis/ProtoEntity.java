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

package com.kotcrab.vis.editor.util.vis;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.Bag;
import com.artemis.utils.EntityBuilder;
import com.kotcrab.vis.editor.entity.UUIDComponent;
import com.kotcrab.vis.editor.module.project.SceneIOModule;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Entity blueprint that can be used to create clones of entity. Linked to specified {@link EntityEngine} and cannot
 * be used outside current scene. ProtoEntity instances should be obtained from {@link SceneIOModule#createProtoEntity(EntityEngine, Entity, boolean)}
 * @author Kotcrab
 */
public class ProtoEntity {
	private SceneIOModule sceneIOModule;
	private EntityEngine entityEngine;
	private boolean preserveUUID;

	private Bag<Component> components = new Bag<>();

	public ProtoEntity (SceneIOModule sceneIOModule, EntityEngine entityEngine, Entity entity, boolean preserveUUID) {
		this.sceneIOModule = sceneIOModule;
		this.entityEngine = entityEngine;
		this.preserveUUID = preserveUUID;

		entity.getComponents(components);
		components = sceneIOModule.cloneEntityComponents(components);
	}

	public Entity build () {
		EntityBuilder builder = new EntityBuilder(entityEngine);

		Bag<Component> components = sceneIOModule.cloneEntityComponents(this.components);

		components.forEach((component) -> {
			if (preserveUUID == false && component instanceof UUIDComponent) {
				builder.with(new UUIDComponent());
			} else {
				builder.with(component);
			}
		});

		return builder.build();
	}
}
