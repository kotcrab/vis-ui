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

package com.kotcrab.vis.runtime.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

/**
 * Entity group contains multiple entities.
 * @author Kotcrab
 */
@Deprecated
public class EntityGroup extends Entity {
	protected Array<Entity> entities = new Array<Entity>();

	public EntityGroup (String id) {
		super(id);
	}

	@Override
	public void render (Batch batch) {
		for (Entity entity : entities)
			entity.render(batch);
	}

	public void addEntity (Entity entity) {
		entities.add(entity);
	}

	public void removeEntity (Entity entity) {
		entities.removeValue(entity, true);
	}

	public Array<Entity> getEntities () {
		return entities;
	}
}
