package net.mostlyoriginal.api.system.delegate;

import com.artemis.Entity;

/**
 * Delegated processing is achieved by implementing
 * the EntityProcessAgent interface.
 *
 * @author Daan van Yperen
 */
public interface EntityProcessAgent {

    /** Prepare to receive a set of entities. */
    public void begin();

    /** Done receiving entities. */
    public void end();

    /** Process the entity. */
    public void process(Entity e);

}
