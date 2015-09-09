package com.kotcrab.vis.runtime.system.delegate;

import com.artemis.Entity;

/**
 * Ability to act as a principal is achieved
 * by implementing the EntityProcessPrincipal interface.
 * @author Daan van Yperen
 */
public interface EntityProcessPrincipal {

	/**
	 * Declare entity relevant for agent.
	 * <p>
	 * After this is called, the principal can use the agent
	 * interface to begin/end and process the given entity.
	 * @param e entity to process
	 * @param agent interface to dispatch with.
	 */
	public void registerAgent (Entity e, EntityProcessAgent agent);

	/**
	 * Revoke relevancy of entity for agent.
	 * <p>
	 * After this is called, the principal should no longer
	 * attempt to process the entity with the agent.
	 * @param e entity to process
	 * @param agent interface to dispatch with.
	 */
	public void unregisterAgent (Entity e, EntityProcessAgent agent);
}
