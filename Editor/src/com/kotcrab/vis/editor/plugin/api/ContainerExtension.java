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

package com.kotcrab.vis.editor.plugin.api;

import com.kotcrab.vis.editor.module.editor.EditorModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.module.scene.SceneModule;
import com.kotcrab.vis.editor.util.PublicApi;

/**
 * Interface allowing to inject custom modules into VisEditor modules containers. Classes implementing this interface
 * must extend correct class depending of their scope returned by {@link #getScope()}, see {@link ExtensionScope} enum.
 * @author Kotcrab
 */
@PublicApi
public interface ContainerExtension {
	enum ExtensionScope {
		/** Modules using this scope must extend {@link EditorModule} */
		EDITOR(EditorModule.class),
		/** Modules using this scope must extend {@link ProjectModule} */
		PROJECT(ProjectModule.class),
		/** Modules using this scope must extend {@link SceneModule} */
		SCENE(SceneModule.class);

		private final Class<?> expectedClass;

		ExtensionScope (Class<?> expectedClass) {
			this.expectedClass = expectedClass;
		}

		public void verify (Class<?> clazz) {
			if (expectedClass.isAssignableFrom(clazz)) return;
			throw new IllegalStateException("Module " + clazz.getSimpleName() + " extends invalid class, should extend " + expectedClass.getSimpleName());
		}
	}

	/** @return scope of this extension. */
	ExtensionScope getScope ();
}
