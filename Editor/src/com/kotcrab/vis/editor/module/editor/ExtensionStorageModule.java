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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.plugin.ContainerExtension;
import com.kotcrab.vis.editor.plugin.ContainerExtension.ExtensionScope;
import com.kotcrab.vis.editor.plugin.ObjectSupport;

/**
 * Holds plugins loaded by {@link PluginLoaderModule}. Others modules (even from different containers like 'project' or 'scene') then can access them.
 * @author Kotcrab
 */
public class ExtensionStorageModule extends EditorModule {
	private Array<ObjectSupport> objectSupports = new Array<>();
	private Array<ContainerExtension<?>> containerExtensions = new Array<>();

	public void addObjectSupport (ObjectSupport support) {
		objectSupports.add(support);
	}

	public void addContainerExtension (ContainerExtension extension) {
		containerExtensions.add(extension);
	}

	public Array<ObjectSupport> getObjectSupports () {
		return objectSupports;
	}

	public <T extends Module> Array<T> getContainersExtensions (Class<T> baseModuleType, ExtensionScope scope) {
		Array<T> modules = new Array<>();

		for (ContainerExtension extension : containerExtensions)
			if (extension.getScope() == scope) modules.add((T) extension);

		return modules;
	}
}
