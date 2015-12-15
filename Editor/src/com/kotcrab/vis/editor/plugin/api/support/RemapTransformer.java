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

package com.kotcrab.vis.editor.plugin.api.support;

import com.artemis.Component;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/** @author Kotcrab */
public class RemapTransformer extends ComponentTransformer<Component> {
	private Class<? extends Component> targetClass;

	public RemapTransformer (Class<? extends Component> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public void transform (Entity entity, Array<Component> components, Component source) {
		try {
			Constructor constructor = targetClass.getConstructor();
			Component target = (Component) constructor.newInstance();

			for (Field field : source.getClass().getDeclaredFields()) {
				Field targetField = targetClass.getDeclaredField(field.getName());
				targetField.setAccessible(true);
				field.setAccessible(true);
				targetField.set(target, field.get(source));
			}
			components.add(target);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
