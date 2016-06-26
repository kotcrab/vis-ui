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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupport;
import com.kotcrab.vis.editor.plugin.api.EditorEntitySupportProvider;

/** @author Kotcrab */
public class ProjectExtensionStorageModule extends ProjectModule {
	private ExtensionStorageModule extensionStorage;

	private Array<EditorEntitySupport> supports = new Array<>();

	@Override
	public void init () {
		for (EditorEntitySupportProvider provider : extensionStorage.getEntitySupportProviders()) {
			EditorEntitySupport support = provider.provide();
			projectContainer.injectModules(support);
			support.init();
			supports.add(support);
		}
	}

	public Array<EditorEntitySupport> getEntitySupports () {
		return supports;
	}
}
