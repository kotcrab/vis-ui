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

package com.kotcrab.vis.editor.module.project;

import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.ui.tab.ProjectInfoTab;

/**
 * When project is loaded it creates and displays {@link ProjectInfoTab}
 * @author Kotcrab
 */
public class ProjectInfoTabModule extends ProjectModule {
	@InjectModule private TabsModule tabsModule;

	private ProjectInfoTab tab;

	@Override
	public void init () {
		tab = new ProjectInfoTab(project);
		tabsModule.addTab(tab);
	}

	@Override
	public void dispose () {
		tabsModule.removeTab(tab);
	}
}
