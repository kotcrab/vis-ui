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

package com.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;

/**
 * Allows to injects modules or other objects into target object. For subclasses of {@link Module} module injection
 * is done automatically when module is loaded into {@link ModuleContainer}. For other classes you must pass them ModuleInjector
 * and they must call {@link ModuleInjector#injectModules(Object)}. All possible fields will be injected, injection can
 * be skipped with {@link SkipInject}. What other objects are injected depends on container implementation, eg. {@link EditorModuleContainer}.
 * will also inject main VisEditor {@link Stage}
 * @author Kotcrab
 */
public interface ModuleInjector {
	/** Injects modules into given object */
	void injectModules (Object target);
}
