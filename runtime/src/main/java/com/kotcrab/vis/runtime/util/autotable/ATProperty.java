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

import com.artemis.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on fields inside {@link Component}s to make them editable inside VisEditor. Target component
 * class must be registered using AutoComponentTable. See VisEditor source for more details.
 * <p>
 * This annotation supports float, int and boolean fields. It can be also used to invoke getters and setters for those
 * primitive types on other objects types. See {@link ATReflectedProperty}
 * Auto table will automatically create number input field or checkbox and update it when needed.
 * @see ATUseGetterSetter
 * @author Kotcrab
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ATProperty {
	/** @return human friendly field name that will be used in VisEditor UI. */
	String fieldName () default "";

	/** @return field max value, if target field is boolean this if ignored */
	float max () default Float.MAX_VALUE;

	/** @return field min value, if target field is boolean this if ignored */
	float min () default Float.MIN_VALUE;

	/** @return text that is used for this field tooltip, if text equals "" tooltip won't be created */
	String tooltip () default "";
}

