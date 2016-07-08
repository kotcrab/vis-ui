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

package com.kotcrab.vis.runtime.data;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.util.EntityEngine;

/**
 * Stores entity components, used for serializing.
 * @author Kotcrab
 */
public class EntityData {
	public Array<Component> components;

	public EntityData () {
	}

	public EntityData (Array<Component> components) {
		this.components = components;
	}

	public Entity build (EntityEngine engine) {
		EntityBuilder builder = new EntityBuilder(engine);

		for (Component component : components)
			builder.with(component);

		return builder.build();
	}
}
