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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.proxy.GroupEntityProxy;
import com.kotcrab.vis.runtime.component.GroupComponent;

/**
 * This system should be passive.
 * @author Kotcrab
 */
@Wire
public class GroupProxyProviderSystem extends EntityProcessingSystem {
	private EntityProxyCache proxyCache;

	private ComponentMapper<GroupComponent> groupCm;

	private int findGid;
	private Array<EntityProxy> result = new Array<>();

	public GroupProxyProviderSystem () {
		super(Aspect.all(GroupComponent.class));
		setEnabled(false);
	}

	@Override
	protected void begin () {
		result.clear();
	}

	public GroupEntityProxy getGroupEntityProxy (int gid) {
		findGid = gid;
		process();

		return new GroupEntityProxy(result, gid);
	}

	@Override
	protected void process (Entity e) {
		IntArray groupsIds = groupCm.get(e).groupIds;

		if (groupsIds.contains(findGid))
			result.add(proxyCache.get(e));
	}
}
