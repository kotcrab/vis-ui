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

package com.kotcrab.vis.runtime.system;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.runtime.component.VisGroup;

/**
 * Allows to get entities by their string id that was set in VisEditor.
 * @author Kotcrab
 */
public class VisGroupManager extends Manager {
	private ComponentMapper<VisGroup> groupCm;
	private AspectSubscriptionManager subscriptionManager;

	private IntMap<String> groupsIds;
	private IntMap<Array<Entity>> groups = new IntMap<Array<Entity>>();

	public VisGroupManager (IntMap<String> groupsIds) {
		this.groupsIds = groupsIds;
	}

	@Override
	protected void initialize () {
		EntitySubscription subscription = subscriptionManager.get(Aspect.all(VisGroup.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (IntBag entities) {
				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					int entityId = data[i];

					IntArray groupIds = groupCm.get(entityId).groupIds;

					for (int j = 0; j < groupIds.size; j++) {
						int gid = groupIds.get(j);

						Array<Entity> groupList = groups.get(gid);

						if (groupList == null) {
							groupList = new Array<Entity>();
							groups.put(gid, groupList);
						}

						groupList.add(world.getEntity(entityId));
					}

				}
			}

			@Override
			public void removed (IntBag entities) {
				int[] data = entities.getData();
				for (int i = 0; i < entities.size(); i++) {
					int entityId = data[i];

					IntArray groupIds = groupCm.get(entityId).groupIds;

					for (int j = 0; j < groupIds.size; j++) {
						int gid = groupIds.get(j);

						Array<Entity> groupList = groups.get(gid);
						groupList.removeValue(world.getEntity(entityId), true);

						if (groupList.size == 0) {
							groups.remove(gid);
						}
					}
				}
			}
		});
	}

	public Array<Entity> get (int intId) {
		return groups.get(intId);
	}

	public Array<Entity> get (String stringId) {
		int gid = groupsIds.findKey(stringId, false, Integer.MIN_VALUE);
		if (gid == Integer.MIN_VALUE)
			throw new IllegalStateException("Group with ID: " + stringId + " does not exists!");

		return groups.get(gid);
	}
}
