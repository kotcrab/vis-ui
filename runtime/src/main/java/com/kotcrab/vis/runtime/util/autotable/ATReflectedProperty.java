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

package com.kotcrab.vis.runtime.util.autotable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used along with {@link ATProperty} but when reflection must be used on target object to change it's properties.
 * It allows to invoke getters and setter on objects, it supports the same types as ATProperty (float, int, boolean).
 * @author Kotcrab
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ATReflectedProperty {
	/** @return type that is used by getter and setter */
	Class<?> targetType ();

	/** @return getter name used to get value from this object */
	String getterName () default "";

	/** @return setter name used to set value for this object */
	String setterName () default "";
}
