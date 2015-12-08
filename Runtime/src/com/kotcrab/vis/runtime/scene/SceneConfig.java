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

import java.util.Comparator;

/** @author Kotcrab */
public class SceneConfig {
	private Array<FeatureElement> defaultFeatures = new Array<FeatureElement>();
	private Array<ConfigElement> elements = new Array<ConfigElement>();

	public SceneConfig () {
		registerFeatureGroup(SceneFeatureGroup.ESSENTIAL);
		registerFeatureGroup(SceneFeatureGroup.INFLATER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS);
		registerFeatureGroup(SceneFeatureGroup.UPDATE);
		registerFeatureGroup(SceneFeatureGroup.RENDERER);
		registerFeatureGroup(SceneFeatureGroup.PHYSICS_DEBUG);

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
		for (FeatureElement element : defaultFeatures) {
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
		for (FeatureElement element : defaultFeatures) {
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
		for (FeatureElement element : defaultFeatures) {
			if (element.feature == feature) {
				element.provider = newProvider;
				return this;
			}
		}

		return this;
	}

	private void registerFeature (SceneFeature feature) {
		defaultFeatures.add(new FeatureElement(feature));
	}

	private void registerFeatureGroup (SceneFeatureGroup group) {
		for (SceneFeature feature : group.features) {
			registerFeature(feature);
		}
	}

	void sort () {
		elements.sort();
	}

	Array<FeatureElement> getDefaultFeatures () {
		return defaultFeatures;
	}

	Array<ConfigElement> getElements () {
		return elements;
	}

	static class ConfigElement implements Comparator<ConfigElement> {
		SystemProvider provider;
		int priority;

		public ConfigElement (SystemProvider provider, int priority) {
			this.priority = priority;
			this.provider = provider;
		}

		@Override
		public int compare (ConfigElement o1, ConfigElement o2) {
			return Integer.compare(o1.priority, o2.priority);
		}
	}

	static class FeatureElement {
		SceneFeature feature;
		SystemProvider provider;
		boolean disabled;

		public FeatureElement (SceneFeature feature) {
			this.feature = feature;
			this.provider = feature.defaultProvider;
		}

		public FeatureElement (SceneFeature feature, boolean disabled) {
			this.feature = feature;
			this.provider = feature.defaultProvider;
			this.disabled = disabled;
		}
	}

	public static class Priority {
		public static final int LOWEST = Integer.MIN_VALUE;
		public static final int LOW = -100000;
		public static final int NORMAL = 0;
		public static final int HIGH = 100000;
		public static final int HIGHEST = Integer.MAX_VALUE;
	}
}
