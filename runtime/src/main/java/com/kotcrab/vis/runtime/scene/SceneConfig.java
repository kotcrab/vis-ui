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

package com.kotcrab.vis.runtime.scene;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;

/**
 * Allows to configure what features (default systems) are enabled for newly loaded scene. Allows to add new user defined
 * systems.
 * @author Kotcrab
 */
public class SceneConfig {
	private Array<ConfigElement> elements = new Array<ConfigElement>();

	public SceneConfig () {
		registerFeatureGroup(SceneFeatureGroup.ESSENTIAL, Priority.VIS_ESSENTIAL);
		registerFeatureGroup(SceneFeatureGroup.INFLATER, Priority.VIS_INFLATER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS, Priority.VIS_PHYSICS);
		registerFeatureGroup(SceneFeatureGroup.RENDERER, Priority.VIS_RENDERER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS_DEBUG, Priority.VIS_OTHER);

		registerFeature(SceneFeature.DIRTY_CLEANER_SYSTEM, Priority.VIS_LOW);

		disable(SceneFeature.GROUP_ID_MANAGER);
		disable(SceneFeature.BOX2D_DEBUG_RENDER_SYSTEM);
	}

	public SceneConfig addSystem (Class<? extends BaseSystem> systemClass) {
		return addSystem(new ReflectionSystemProvider(systemClass));
	}

	public SceneConfig addSystem (Class<? extends BaseSystem> systemClass, Priority priority) {
		return addSystem(new ReflectionSystemProvider(systemClass), priority.toIntValue());
	}

	public SceneConfig addSystem (Class<? extends BaseSystem> systemClass, int priority) {
		return addSystem(new ReflectionSystemProvider(systemClass), priority);
	}

	public SceneConfig addSystem (SystemProvider provider) {
		elements.add(new ConfigElement(provider, Priority.NORMAL.toIntValue()));
		return this;
	}

	public SceneConfig addSystem (SystemProvider provider, Priority priority) {
		elements.add(new ConfigElement(provider, priority.toIntValue()));
		return this;
	}

	public SceneConfig addSystem (SystemProvider provider, int priority) {
		elements.add(new ConfigElement(provider, priority));
		return this;
	}

	public SceneConfig disable (SceneFeature feature) {
		for (ConfigElement element : elements) {
			if (element.feature == feature) {
				element.disabled = true;
				return this;
			}
		}

		return this;
	}

	public SceneConfig disable (SceneFeatureGroup featureGroup) {
		for (SceneFeature feature : featureGroup.features) {
			disable(feature);
		}

		return this;
	}

	public SceneConfig enable (SceneFeature feature) {
		for (ConfigElement element : elements) {
			if (element.feature == feature) {
				element.disabled = false;
				return this;
			}
		}

		return this;
	}

	public SceneConfig enable (SceneFeatureGroup featureGroup) {
		for (SceneFeature feature : featureGroup.features) {
			enable(feature);
		}

		return this;
	}

	public SceneConfig replace (SceneFeature feature, SystemProvider newProvider) {
		for (ConfigElement element : elements) {
			if (element.feature == feature) {
				element.provider = newProvider;
				return this;
			}
		}

		return this;
	}

	private void registerFeature (SceneFeature feature, Priority priority) {
		elements.add(new ConfigElement(feature, feature.defaultProvider, priority.toIntValue()));
	}

	private void registerFeatureGroup (SceneFeatureGroup group, Priority priority) {
		for (SceneFeature feature : group.features) {
			registerFeature(feature, priority);
		}
	}

	void sort () {
		elements.sort();
	}

	Array<ConfigElement> getConfigElements () {
		return elements;
	}

	static class ConfigElement implements Comparable<ConfigElement> {
		SceneFeature feature;
		boolean disabled; //disabled can be only set if feature is set

		SystemProvider provider;
		int priority;

		public ConfigElement (SceneFeature feature, SystemProvider provider, int priority) {
			this.feature = feature;
			this.provider = provider;
			this.priority = priority;
		}

		public ConfigElement (SystemProvider provider, int priority) {
			this.provider = provider;
			this.priority = priority;
		}

		@Override
		public int compareTo (ConfigElement other) {
			return -compare(priority, other.priority);
		}

		private int compare (int x, int y) {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	}

	/**
	 * Defines priorities for entity engine systems. Systems with high priorities will be processed before systems with
	 * low priorities.
	 */
	public enum Priority {
		LOWEST(Integer.MIN_VALUE),
		LOW(-100000),
		NORMAL(0),
		HIGH(100000),
		HIGHEST(Integer.MAX_VALUE),

		VIS_ESSENTIAL(20000),
		VIS_INFLATER(19000),
		VIS_RELOADER(18500),
		VIS_PHYSICS(18000),
		VIS_RENDERER(17000),
		VIS_OTHER(16000),
		VIS_LOW(-200000);

		private final int intValue;

		Priority (int intValue) {
			this.intValue = intValue;
		}

		public int toIntValue () {
			return intValue;
		}

		public int after () {
			return toIntValue() - 1;
		}

		public int before () {
			return toIntValue() + 1;
		}
	}
}
