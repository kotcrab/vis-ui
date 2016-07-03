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

package com.kotcrab.vis.editor.ui.scene.entityproperties.autotable.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** @author Kotcrab */
public class GetterSetterFieldAccessor implements FieldAccessor {
	private Method getter;
	private Method setter;

	public GetterSetterFieldAccessor (Field field) throws NoSuchMethodException {
		String fieldName = field.getName();
		fieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
		getter = field.getDeclaringClass().getMethod("get" + fieldName);
		setter = field.getDeclaringClass().getMethod("set" + fieldName, field.getType());
	}

	@Override
	public Object get (Object obj) throws ReflectiveOperationException {
		return getter.invoke(obj);
	}

	@Override
	public void set (Object obj, Object value) throws ReflectiveOperationException {
		setter.invoke(obj, value);
	}
}
