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

import com.kotcrab.vis.editor.module.Module;
import com.kotcrab.vis.editor.module.ModuleContainer;
import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;

/**
 * Modules container for project scope modules
 * @author Kotcrab
 */
public class ProjectModuleContainer extends ModuleContainer<ProjectModule> {
	private EditorModuleContainer editorMC;
	private Project project;

	public ProjectModuleContainer (EditorModuleContainer editorMC) {
		this.editorMC = editorMC;
	}

	@Override
	public void add (ProjectModule module) {
		if (project == null) throw new IllegalStateException("Module cannot be added before project has been set!");

		module.setProject(project);
		module.setProjectModuleContainer(this);
		module.setContainer(editorMC);

		super.add(module);
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0)
			throw new IllegalStateException("Project can't be changed while modules are loaded!");
		this.project = project;
	}

	@Override
	public <C extends Module> C findInHierarchy (Class<C> moduleClass) {
		C module = getOrNull(moduleClass);
		if (module != null) return module;

		return editorMC.findInHierarchy(moduleClass);
	}

	public Project getProject () {
		return project;
	}

	public EditorModuleContainer getEditorContainer () {
		return editorMC;
	}
}
