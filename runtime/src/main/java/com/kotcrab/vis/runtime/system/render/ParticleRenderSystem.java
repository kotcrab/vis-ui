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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.runtime.component.Invisible;
import com.kotcrab.vis.runtime.component.Transform;
import com.kotcrab.vis.runtime.component.VisParticle;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/**
 * Renders entities with {@link VisParticle}.
 * @author Kotcrab
 */
public class ParticleRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<VisParticle> particleCm;
	private ComponentMapper<Transform> transformCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;
	private final boolean ignoreActive;

	public ParticleRenderSystem (EntityProcessPrincipal principal, boolean ignoreParticleActiveState) {
		super(Aspect.all(VisParticle.class).exclude(Invisible.class), principal);
		this.ignoreActive = ignoreParticleActiveState;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		VisParticle particle = particleCm.get(entityId);
		Transform transform = transformCm.get(entityId);

		ParticleEffect effect = particle.getEffect();

		if (transform.isDirty()) {
			particle.updateValues(transform.getX(), transform.getY());
		}

		if (ignoreActive || particle.isActiveOnStart())
			effect.update(world.delta);

		effect.draw(batch);

		if (effect.isComplete())
			effect.reset();
	}
}
