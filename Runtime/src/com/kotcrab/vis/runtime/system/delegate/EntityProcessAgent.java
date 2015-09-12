package com.kotcrab.vis.runtime.system.delegate;

import com.artemis.Entity;

/**
 * Delegated processing is achieved by implementing
 * the EntityProcessAgent interface.
 * @author Daan van Yperen
 */
public interface EntityProcessAgent {

	/** Prepare to receive a set of entities. */
	void begin ();

	/** Done receiving entities. */
	void end ();

	/** Process the entity. */
	void process (Entity e);

}
