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

package com.kotcrab.vis.editor.module.scene;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.entity.UUIDComponent;

import java.util.UUID;

@Wire
public class VisUUIDManager extends Manager {
	private ComponentMapper<UUIDComponent> idCm;
	private AspectSubscriptionManager subscriptionManager;

	private EntitySubscription subscription;

	private ObjectMap<UUID, Entity> idStore = new ObjectMap<>();

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(UUIDComponent.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (ImmutableBag<Entity> entities) {
				ObjectMap<UUID, Entity> tmpCache = new ObjectMap<>();
				entities.forEach(entity -> tmpCache.put(idCm.get(entity).getUuid(), entity));
				idStore.putAll(tmpCache);
			}

			@Override
			public void removed (ImmutableBag<Entity> entities) {
				entities.forEach(entity -> idStore.remove(idCm.get(entity).getUuid()));
			}
		});
	}

	public Entity get (UUID uuid) {
		return idStore.get(uuid);
	}
}
