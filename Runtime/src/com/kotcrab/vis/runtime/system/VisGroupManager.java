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

package com.kotcrab.vis.runtime.system;

import com.artemis.*;
import com.artemis.EntitySubscription.SubscriptionListener;
import com.artemis.annotations.Wire;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.kotcrab.vis.runtime.component.GroupComponent;

/**
 * Allows to get entities by their string id that was set in VisEditor
 * @author Kotcrab
 */
@Wire
public class VisGroupManager extends Manager {
	private ComponentMapper<GroupComponent> groupCm;
	private AspectSubscriptionManager subscriptionManager;

	private IntMap<String> groupsIds;
	private IntMap<Array<Entity>> groups = new IntMap<Array<Entity>>();

	public VisGroupManager (IntMap<String> groupsIds) {
		this.groupsIds = groupsIds;
	}

	@Override
	protected void initialize () {
		EntitySubscription subscription = subscriptionManager.get(Aspect.all(GroupComponent.class));

		subscription.addSubscriptionListener(new SubscriptionListener() {
			@Override
			public void inserted (ImmutableBag<Entity> entities) {
				for (Entity entity : entities) {
					IntArray groupIds = groupCm.get(entity).groupIds;

					for (int i = 0; i < groupIds.size; i++) {
						int gid = groupIds.get(i);

						Array<Entity> groupList = groups.get(gid);

						if (groupList == null) {
							groupList = new Array<Entity>();
							groups.put(gid, groupList);
						}

						groupList.add(entity);
					}
				}
			}

			@Override
			public void removed (ImmutableBag<Entity> entities) {
				for (Entity entity : entities) {
					IntArray groupIds = groupCm.get(entity).groupIds;

					for (int i = 0; i < groupIds.size; i++) {
						int gid = groupIds.get(i);

						Array<Entity> groupList = groups.get(gid);
						groupList.removeValue(entity, true);

						if (groupList.size == 0) {
							groups.remove(gid);
						}
					}
				}
			}
		});
	}

	public Array<Entity> get (String stringId) {
		int gid = groupsIds.findKey(stringId, false, Integer.MIN_VALUE);
		if (gid == Integer.MIN_VALUE)
			throw new IllegalStateException("Group with ID: " + stringId + " does not exists!");

		return groups.get(gid);
	}
}
