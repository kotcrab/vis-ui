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

/**
 * Ability to basic as a principal is achieved
 * by implementing the EntityProcessPrincipal interface.
 * @author Daan van Yperen
 */
public interface EntityProcessPrincipal {

	/**
	 * Declare entity relevant for agent.
	 * <p>
	 * After this is called, the principal can use the agent
	 * interface to begin/end and process the given entity.
	 * @param entityId entity to process
	 * @param agent interface to dispatch with.
	 */
	void registerAgent (int entityId, EntityProcessAgent agent);

	/**
	 * Revoke relevancy of entity for agent.
	 * <p>
	 * After this is called, the principal should no longer
	 * attempt to process the entity with the agent.
	 * @param entityId entity to process
	 * @param agent interface to dispatch with.
	 */
	void unregisterAgent (int entityId, EntityProcessAgent agent);
}
