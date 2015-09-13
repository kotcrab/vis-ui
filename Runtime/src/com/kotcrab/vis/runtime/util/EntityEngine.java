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

package com.kotcrab.vis.runtime.util;

import com.artemis.Entity;
import com.artemis.EntityManager;
import com.artemis.SystemInvocationStrategy;
import com.artemis.World;
import com.artemis.utils.Bag;

import java.lang.reflect.Field;

/**
 * Extends Artemis {@link World}. Does not provide any additional methods, exists because Artemis World duplicated box2d World class.
 * @author Kotcrab
 */
public class EntityEngine extends World {

	private Bag<Entity> entities;

	public EntityEngine (EntityEngineConfiguration configuration) {
		super(configuration.build());

		//FIXME: this must be removed ASAP when artemis is fixed
		//https://github.com/junkdog/artemis-odb/issues/347
		try {
			Field entitiesField = EntityManager.class.getDeclaredField("entities");
			entitiesField.setAccessible(true);
			entities = (Bag<Entity>) entitiesField.get(getEntityManager());
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Entity getEntity (int entityId) {
		Entity e = entities.safeGet(entityId);
		if(e != null) return e;
		return super.getEntity(entityId);
	}

	@Override
	public void setInvocationStrategy (SystemInvocationStrategy invocationStrategy) {
		super.setInvocationStrategy(invocationStrategy);
	}
}
