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
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.runtime.RuntimeContext;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.util.EntityEngineConfiguration;

/**
 * System provider implementation that can construct new instances of systems using reflection. If you need to customize
 * how system is created implement {@link SystemProvider} directly.
 * @author Kotcrab
 */
public class ReflectionSystemProvider implements SystemProvider {
	private Class<? extends BaseSystem> systemClass;

	public ReflectionSystemProvider (Class<? extends BaseSystem> systemClass) {
		this.systemClass = systemClass;
	}

	@Override
	public BaseSystem create (EntityEngineConfiguration config, RuntimeContext context, SceneData data) {
		try {
			return ClassReflection.newInstance(systemClass);
		} catch (ReflectionException e) {
			throw new IllegalStateException("Failed to create system using reflection", e);
		}
	}
}
