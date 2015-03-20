/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.project;

import com.kotcrab.vis.editor.module.editor.EditorModuleContainer;
import com.kotcrab.vis.editor.module.BaseModuleContainer;

public class ProjectModuleContainer extends BaseModuleContainer<ProjectModule> {
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
		if (getModuleCounter() > 0) throw new IllegalStateException("Project can't be changed while modules are loaded!");
		this.project = project;
	}

	public Project getProject () {
		return project;
	}

	public EditorModuleContainer getEditorContainer () {
		return editorMC;
	}
}
