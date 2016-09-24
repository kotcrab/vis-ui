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

package com.kotcrab.vis.runtime.system.delegate;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;

/**
 * Actor for a principal system.
 * <p>
 * Deferred entity systems are a drop in replacement for
 * EntityProcessingSystem, where you need to delegate order of
 * entity processing to an overarching system.
 * <p>
 * One example would be an animation, font and map rendering
 * subsystem of a simplistic render pipeline principal,
 * sorting render calls by z-layer.
 * <p>
 * Upon entity insertion/removal, the system registers the entity
 * and itself with the principal. The principal can then register
 * and basic upon this information, and call begin/process(entity)/end
 * methods whenever desired.
 * @author Daan van Yperen
 * @see EntityProcessAgent
 * @see EntityProcessPrincipal
 */
public abstract class DeferredEntityProcessingSystem extends BaseEntitySystem {

	private final Aspect.Builder aspect;
	private final EntityProcessPrincipal principal;

	/**
	 * Creates an entity system that uses the specified aspect as a matcher
	 * against entities.
	 * @param aspect to match against entities
	 * @param principal principal that will organize process calls to this system.
	 */
	public DeferredEntityProcessingSystem (Aspect.Builder aspect, EntityProcessPrincipal principal) {
		super(aspect);
		this.aspect = aspect;
		this.principal = principal;
	}

	@Override
	protected void initialize () {
		super.initialize();

		setEnabled(false);
	}

	/**
	 * Process a entity this system is interested in.
	 * @param e the entity to process
	 */
	protected abstract void process (int e);

	@Override
	protected void removed (int entityId) {

		// inform delegation handler
		principal.unregisterAgent(entityId, localProcessingAgent);

		super.removed(entityId);
	}

	@Override
	protected void inserted (int entityId) {
		super.inserted(entityId);

		// warn delegation handler we've lost interest in this entity.
		principal.registerAgent(entityId, localProcessingAgent);
	}

	@Override
	protected void processSystem () {
	}

	/**
	 * Simple processing agent that delegates to this system.
	 * Workaround for naming collisions, so the all callers
	 * can use the methods they are used to.
	 */
	protected EntityProcessAgent localProcessingAgent = new EntityProcessAgent() {
		@Override
		public void begin () {
			DeferredEntityProcessingSystem.this.begin();
		}

		@Override
		public void end () {
			DeferredEntityProcessingSystem.this.end();
		}

		@Override
		public void process (int e) {
			DeferredEntityProcessingSystem.this.process(e);
		}
	};
}
