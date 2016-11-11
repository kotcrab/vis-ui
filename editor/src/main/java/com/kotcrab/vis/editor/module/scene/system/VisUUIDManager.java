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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.entity.VisUUID;

import java.util.UUID;

public class VisUUIDManager extends Manager {
	private ComponentMapper<VisUUID> idCm;
	private AspectSubscriptionManager subscriptionManager;

	private EntitySubscription subscription;

	private ObjectMap<UUID, Entity> idStore = new ObjectMap<>();

	@Override
	protected void initialize () {
		subscription = subscriptionManager.get(Aspect.all(VisUUID.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (IntBag entities) {
				ObjectMap<UUID, Entity> tmpCache = new ObjectMap<>();

				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					int entityId = data[i];
					Entity entity = world.getEntity(entityId);
					tmpCache.put(idCm.get(entityId).getUUID(), entity);
				}

				idStore.putAll(tmpCache);
			}

			@Override
			public void removed (IntBag entities) {
				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					int entityId = data[i];
					Entity entity = world.getEntity(entityId);
					if (idCm.get(entity) == null) continue;
					idStore.remove(idCm.get(entity).getUUID());
				}
			}
		});
	}

	public Entity get (UUID uuid) {
		return idStore.get(uuid);
	}
}
