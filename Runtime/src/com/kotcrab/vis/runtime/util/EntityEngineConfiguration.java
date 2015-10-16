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

package com.kotcrab.vis.runtime.util;

import com.artemis.BaseSystem;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.utils.Array;

/**
 * Similar to {@link WorldConfiguration} however it allows to get added systems and managers.
 * @author Kotcrab
 */
public class EntityEngineConfiguration {
	private boolean built;
	private Array<BaseSystem> systems = new Array<BaseSystem>();

	public void setSystem (BaseSystem system) {
		checkBeforeAdd();
		systems.add(system);
	}

	private void checkBeforeAdd () {
		if (built) throw new IllegalStateException("This configuration was already build and cannot be changed!");
	}

	@SuppressWarnings("unchecked")
	public <C extends BaseSystem> C getSystem (Class<C> clazz) {
		BaseSystem system = getOrNull(systems, (Class<BaseSystem>) clazz);
		if (system != null) return (C) system;

		throw new IllegalStateException("Failed to get system: '" + clazz + "', system not added!");
	}

	private <C> C getOrNull (Array<C> array, Class<C> clazz) {
		if (built)
			throw new IllegalStateException("This configuration was already build and it's contents cannot be accessed!");
		for (int i = 0; i < array.size; i++) {
			C m = array.get(i);
			if (m.getClass() == clazz) return m;
		}

		return null;
	}

	public WorldConfiguration build () {
		if (built) throw new IllegalStateException("Cannot built configuration twice!");
		built = true;
		WorldConfiguration config = new WorldConfiguration();

		for (BaseSystem system : systems) {
			config.setSystem(system);
		}

		return config;
	}
}
