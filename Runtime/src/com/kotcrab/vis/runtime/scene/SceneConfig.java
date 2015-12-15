/*
 * Copyright 2014-2015 See AUTHORS file.
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

/** @author Kotcrab */
public class SceneConfig {
	private Array<ConfigElement> elements = new Array<ConfigElement>();

	public SceneConfig () {
		registerFeatureGroup(SceneFeatureGroup.ESSENTIAL, Priority.VIS_ESSENTIAL);
		registerFeatureGroup(SceneFeatureGroup.INFLATER, Priority.VIS_INFLATER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS, Priority.VHS_PHYSICS);
		registerFeatureGroup(SceneFeatureGroup.RENDERER, Priority.VIS_RENDERER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS_DEBUG, Priority.VIS_OTHER);

		registerFeature(SceneFeature.DIRTY_CLEANER_SYSTEM, Priority.VIS_OTHER);

		disable(SceneFeature.GROUP_ID_MANAGER);
		disable(SceneFeature.BOX2D_DEBUG_RENDER_SYSTEM);
	}

	public SceneConfig addSystem (BaseSystem system) {
		return addSystem(new SimpleSystemProvider(system));
	}

	public SceneConfig addSystem (BaseSystem system, int priority) {
		return addSystem(new SimpleSystemProvider(system), priority);
	}

	public SceneConfig addSystem (SystemProvider provider, int priority) {
		elements.add(new ConfigElement(provider, priority));
		return this;
	}

	public SceneConfig addSystem (SystemProvider provider) {
		elements.add(new ConfigElement(provider, Priority.NORMAL));
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

	private void registerFeature (SceneFeature feature, int priority) {
		elements.add(new ConfigElement(feature, feature.defaultProvider, priority));
	}

	private void registerFeatureGroup (SceneFeatureGroup group, int priority) {
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
			return Integer.compare(priority, other.priority);
		}
	}

	public static class Priority {
		public static final int LOWEST = Integer.MIN_VALUE;
		public static final int LOW = -100000;
		public static final int VIS_ESSENTIAL = -10000;
		public static final int VIS_INFLATER = -9000;
		public static final int VHS_PHYSICS = -8000;
		public static final int VIS_RENDERER = -7000;
		public static final int VIS_OTHER = -5000;
		public static final int NORMAL = 0;
		public static final int HIGH = 100000;
		public static final int HIGHEST = Integer.MAX_VALUE;
	}
}
