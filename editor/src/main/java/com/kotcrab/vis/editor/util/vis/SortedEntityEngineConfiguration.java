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

package com.kotcrab.vis.editor.util.vis;

import com.artemis.BaseSystem;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.scene.SceneConfig.Priority;

/** @author Kotcrab */
public class SortedEntityEngineConfiguration {
	private boolean built;
	private Array<ConfigElement> elements = new Array<>();

	public void setSystem (BaseSystem system, Priority priority) {
		setSystem(system, priority.toIntValue());
	}

	public void setSystem (BaseSystem system, int priority) {
		checkBeforeAdd();
		elements.add(new ConfigElement(system, priority));
	}

	private void checkBeforeAdd () {
		if (built) throw new IllegalStateException("This configuration was already build and cannot be changed!");
	}

	public <C extends BaseSystem> C getSystem (Class<C> clazz) {
		if (built)
			throw new IllegalStateException("This configuration was already build and it's contents cannot be accessed!");

		for (int i = 0; i < elements.size; i++) {
			BaseSystem system = elements.get(i).system;
			if (system.getClass() == clazz) return clazz.cast(system);
		}

		throw new IllegalStateException("Failed to get system: '" + clazz + "', system not added!");
	}

	public WorldConfiguration build () {
		if (built) throw new IllegalStateException("Cannot built configuration twice!");
		built = true;
		elements.sort();
		WorldConfiguration config = new WorldConfiguration();

		for (ConfigElement element : elements) {
			config.setSystem(element.system);
		}

		return config;
	}

	private static class ConfigElement implements Comparable<ConfigElement> {
		public BaseSystem system;
		public int priority;

		public ConfigElement (BaseSystem system, int priority) {
			this.system = system;
			this.priority = priority;
		}

		@Override
		public int compareTo (ConfigElement other) {
			return -Integer.compare(priority, other.priority);
		}
	}
}
