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

package com.kotcrab.vis.editor.util;

import java.lang.annotation.*;

/**
 * Types annotated by this annotation are considered as part of VisEditor public API and may be used in plugins. Those
 * types should also have complete Javadoc documentation.
 * <p>
 * All changes to those types must be published in API_CHANGES file. Any changes that break public API should be avoided.
 * Method and field deprecation should be preferred over removing them as that would create incompatible changes.
 * <p>
 * This annotation will be used to generate VisEditor Javadoc containing only public API classes. If you believe that some
 * class should be part of public API but doesn't have this annotation contact Kotcrab.
 * <p>
 * Phrases such as: "MUST", "MUST NOT", "SHOULD", "SHOULD NOT" must be interpreted as defined in RFC2119.
 * @author Kotcrab
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@PublicApi
public @interface PublicApi {
}
