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

package com.kotcrab.vis.runtime.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Informs on what version code given feature was deprecated. Should be used especially when given feature was considered
 * a public API. This is NOT a replacement for {@link Deprecated} and should be used along with it.
 * @author Kotcrab
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE})
public @interface DeprecatedOn {
	/**
	 * Version code that this feature was depracted on, eg. if this feature still worked on version code 3 and was
	 * deprecated on version code 4, this should be set to 4. Version code are the version codes of VisEditor (defined in
	 * App class)
	 */
	int versionCode ();
}
