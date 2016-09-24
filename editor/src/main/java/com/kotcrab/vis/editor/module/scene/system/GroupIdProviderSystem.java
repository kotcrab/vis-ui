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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.runtime.component.VisGroup;

/**
 * This system should be passive.
 * @author Kotcrab
 */
public class GroupIdProviderSystem extends EntityProcessingSystem {
	private ComponentMapper<VisGroup> groupCm;

	private IntArray usedGids = new IntArray();
	private int freeId;

	public GroupIdProviderSystem () {
		super(Aspect.all(VisGroup.class));
	}

	@Override
	protected void begin () {
		usedGids.clear();
	}

	@Override
	protected void process (Entity e) {
		IntArray groupsIds = groupCm.get(e).groupIds;
		if (groupsIds.size == 0) return;
		usedGids.addAll(groupsIds);
	}

	@Override
	protected void end () {
		if (usedGids.size == 0) {
			freeId = 0;
		} else {
			usedGids.sort();
			freeId = usedGids.peek() + 1;
		}
	}

	public int getFreeGroupId () {
		process();
		return freeId;
	}
}
