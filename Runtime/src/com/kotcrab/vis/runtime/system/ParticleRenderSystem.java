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

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.ParticleComponent;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/**
 * Renders entities with {@link ParticleComponent}
 * @author Kotcrab
 */
@Wire
public class ParticleRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<ParticleComponent> particleCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;
	private final boolean ignoreActive;

	public ParticleRenderSystem (EntityProcessPrincipal principal, boolean ignoreParticleActiveState) {
		super(Aspect.all(ParticleComponent.class).exclude(InvisibleComponent.class), principal);
		this.ignoreActive = ignoreParticleActiveState;
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (final Entity entity) {
		ParticleComponent particle = particleCm.get(entity);

		if (ignoreActive || particle.active)
			particle.effect.update(Gdx.graphics.getDeltaTime());

		particle.effect.draw(batch);

		if (particle.effect.isComplete())
			particle.effect.reset();
	}
}
