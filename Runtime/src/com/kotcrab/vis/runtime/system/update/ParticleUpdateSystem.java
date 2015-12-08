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
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisParticle;
import com.kotcrab.vis.runtime.component.VisParticleChanged;

/** @author Kotcrab */
public class ParticleUpdateSystem extends IteratingSystem {
	private ComponentMapper<VisParticle> particleCm;
	private ComponentMapper<VisParticleChanged> changedCm;
	private ComponentMapper<Transform> transformCm;

	public ParticleUpdateSystem () {
		super(Aspect.all(VisParticle.class, VisParticleChanged.class));
	}

	@Override
	protected void process (int entityId) {
		VisParticle particle = particleCm.get(entityId);
		VisParticleChanged changed = changedCm.get(entityId);
		Transform transform = transformCm.get(entityId);

		particle.updateValues(transform.x, transform.y);

		if (changed.persistent == false) changedCm.remove(entityId);
	}
}
