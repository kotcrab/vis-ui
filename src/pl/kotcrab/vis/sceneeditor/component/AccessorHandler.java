
package pl.kotcrab.vis.sceneeditor.component;

import pl.kotcrab.vis.sceneeditor.accessor.SceneEditorAccessor;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

public class AccessorHandler<T extends SceneEditorAccessor<?>> {
	private ObjectMap<Class<?>, T> accessorMap = new ObjectMap<Class<?>, T>();

	/** Register accessor and allow object of provied class be added to scene */
	public void registerAccessor (T accessor) {
		accessorMap.put(accessor.getSupportedClass(), accessor);
	}

	/** Check if accessor for provied class is available
	 * 
	 * @param clazz class that will be checked
	 * @return true if accessor is avaiable. false otherwise */
	public boolean isAccessorForClassAvaiable (Class<?> clazz) {
		if (accessorMap.containsKey(clazz))
			return true;
		else {
			if (clazz.getSuperclass() == null)
				return false;
			else
				return isAccessorForClassAvaiable(clazz.getSuperclass());
		}
	}

	/** Returns accessor for provided class
	 * 
	 * @param clazz class that accessor will be return if available
	 * @return accessor if available, null otherwise */
	public T getAccessorForClass (Class<?> clazz) {
		if (accessorMap.containsKey(clazz))
			return accessorMap.get(clazz);
		else {
			if (clazz.getSuperclass() == null)
				return null;
			else
				return getAccessorForClass(clazz.getSuperclass());
		}
	}

	/** Returns accessor for provided object
	 * 
	 * @param obj object that accessor will be return if available
	 * @return accessor if available, null otherwise */
	public T getAccessorForObject (Object obj) {
		return getAccessorForClass(obj.getClass());
	}

	public T getAccessorForIdentifier (String identifier) {
		for (Entry<Class<?>, T> entry : accessorMap.entries()) {
			if (entry.value.getIdentifier().equals(identifier)) return entry.value;
		}

		return null;
	}
}
