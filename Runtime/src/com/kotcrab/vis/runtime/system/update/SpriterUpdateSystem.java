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

package com.kotcrab.vis.runtime.system.update;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.kotcrab.vis.runtime.component.*;

/** @author Kotcrab */
public class SpriterUpdateSystem extends IteratingSystem {
	private ComponentMapper<VisSpriter> spriterCm;
	private ComponentMapper<VisSpriterChanged> changedCm;
	private ComponentMapper<Transform> transformCm;

	public SpriterUpdateSystem () {
		super(Aspect.all(VisSpriter.class, VisSpriterChanged.class));
	}

	@Override
	protected void process (int entityId) {
		VisSpriter spriter = spriterCm.get(entityId);
		VisSpriterChanged changed = changedCm.get(entityId);
		Transform transform = transformCm.get(entityId);

		spriter.updateValues(transform.x, transform.y, transform.rotation);

		if (changed.persistent == false) changedCm.remove(entityId);
	}
}
