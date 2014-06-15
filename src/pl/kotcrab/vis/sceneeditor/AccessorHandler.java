/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

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
