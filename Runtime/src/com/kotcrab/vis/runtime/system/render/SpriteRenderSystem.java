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

package com.kotcrab.vis.runtime.system.render;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kotcrab.vis.runtime.component.InvisibleComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;
import com.kotcrab.vis.runtime.system.delegate.DeferredEntityProcessingSystem;
import com.kotcrab.vis.runtime.system.delegate.EntityProcessPrincipal;

/**
 * Renders entities with {@link SpriteComponent}
 * @author Kotcrab
 */
@Wire
public class SpriteRenderSystem extends DeferredEntityProcessingSystem {
	private ComponentMapper<SpriteComponent> spriteCm;

	private RenderBatchingSystem renderBatchingSystem;
	private Batch batch;

	public SpriteRenderSystem (EntityProcessPrincipal principal) {
		super(Aspect.all(SpriteComponent.class).exclude(InvisibleComponent.class), principal);
	}

	@Override
	protected void initialize () {
		batch = renderBatchingSystem.getBatch();
	}

	@Override
	protected void process (int entityId) {
		Sprite sprite = spriteCm.get(entityId).sprite;
		sprite.draw(batch);
	}
}
