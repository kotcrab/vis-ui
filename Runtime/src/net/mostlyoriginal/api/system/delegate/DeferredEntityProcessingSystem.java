package net.mostlyoriginal.api.system.delegate;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;

/**
 * Actor for a principal system.
 *
 * Deferred entity systems are a drop in replacement for
 * EntityProcessingSystem, where you need to delegate order of
 * entity processing to an overarching system.
 *
 * One example would be an animation, font and map rendering
 * subsystem of a simplistic render pipeline principal,
 * sorting render calls by z-layer.
 *
 * Upon entity insertion/removal, the system registers the entity
 * and itself with the principal. The principal can then register
 * and act upon this information, and call begin/process(entity)/end
 * methods whenever desired.
 *
 * @author Daan van Yperen
 * @see net.mostlyoriginal.api.system.delegate.EntityProcessAgent
 * @see EntityProcessPrincipal
 */
public abstract class DeferredEntityProcessingSystem extends EntitySystem {

    private final Aspect.Builder aspect;
    private final EntityProcessPrincipal principal;

    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     *
     * @param aspect to match against entities
     * @param principal principal that will organize process calls to this system.
     */
    public DeferredEntityProcessingSystem(Aspect.Builder aspect, EntityProcessPrincipal principal) {
        super(aspect);
        this.aspect = aspect;
        this.principal = principal;
        setPassive(true);
    }

    /**
   	 * Process a entity this system is interested in.
   	 *
   	 * @param e
   	 *			the entity to process
   	 */
   	protected abstract void process(Entity e);

    @Override
    protected void removed(Entity e) {

        // inform delegation handler
        principal.unregisterAgent(e, localProcessingAgent);

        super.removed(e);
    }

    @Override
    protected void inserted(Entity e) {
        super.inserted(e);

        // warn delegation handler we've lost interest in this entity.
        principal.registerAgent(e, localProcessingAgent);
    }

	@Override
	protected void processSystem() {
	}

	/**
     * Simple processing agent that delegates to this system.
     * Workaround for naming collisions, so the all callers
     * can use the methods they are used to.
     */
    protected EntityProcessAgent localProcessingAgent = new EntityProcessAgent() {
        @Override
        public void begin() {
            DeferredEntityProcessingSystem.this.begin();
        }

        @Override
        public void end() {
            DeferredEntityProcessingSystem.this.end();
        }

        @Override
        public void process(Entity e) {
            DeferredEntityProcessingSystem.this.process(e);
        }
    };
}
