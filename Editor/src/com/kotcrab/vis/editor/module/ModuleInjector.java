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

package com.kotcrab.vis.editor.module;

/**
 * Allows to injects modules in object from some {@link ModuleContainer}. Class fields must be annotated with {@link InjectModule}
 * annotation. For subclasses of {@link Module} module injection is done automatically when module is loaded into {@link ModuleContainer}.
 * For other classes you must pass them ModuleInjector and they must call {@link ModuleInjector#injectModules(Object)}
 * @author Kotcrab
 * @see InjectModule
 */
public interface ModuleInjector {
	/** Injects modules into given object */
	void injectModules (Object target);
}
