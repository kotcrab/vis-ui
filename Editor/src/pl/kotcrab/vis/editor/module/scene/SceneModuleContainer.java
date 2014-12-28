/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.module.scene;

import pl.kotcrab.vis.editor.module.BaseModuleContainer;
import pl.kotcrab.vis.editor.module.EditorModuleContainer;
import pl.kotcrab.vis.editor.module.project.Project;
import pl.kotcrab.vis.editor.module.project.ProjectModuleContainer;

public class SceneModuleContainer extends BaseModuleContainer<SceneModule> {
	private Project project;
	private EditorModuleContainer editorModuleContainer;
	private ProjectModuleContainer projectModuleContainer;

	public SceneModuleContainer (ProjectModuleContainer projectModuleContainer) {
		this.editorModuleContainer = projectModuleContainer.getEditorContainer();
		this.projectModuleContainer = projectModuleContainer;
	}

	@Override
	public void add (SceneModule module) {
		if (project == null) throw new IllegalStateException("Module cannot be added before project has been set!");

		module.setProject(project);
		module.setProjectModuleContainter(projectModuleContainer);
		module.setContainer(editorModuleContainer);
		module.setSceneModuleContainer(this);

		super.add(module);
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0) throw new IllegalStateException("Project can't be changed while modules are loaded!");
		this.project = project;
	}

	public Project getProject () {
		return project;
	}
}
