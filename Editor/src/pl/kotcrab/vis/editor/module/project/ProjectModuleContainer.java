
package pl.kotcrab.vis.editor.module.project;

import pl.kotcrab.vis.editor.module.BaseModuleContainer;
import pl.kotcrab.vis.editor.module.EditorModuleContainer;

public class ProjectModuleContainer extends BaseModuleContainer<ProjectModule> {
	private Project project;
	private EditorModuleContainer editorModuleContainer;

	public ProjectModuleContainer (EditorModuleContainer editorModuleContainter) {
		this.editorModuleContainer = editorModuleContainter;
	}

	@Override
	public void add (ProjectModule module) {
		if (project == null) throw new IllegalStateException("Module cannot be added before project has been set!");

		module.setProject(project);
		module.setProjectModuleContainter(this);
		module.setContainer(editorModuleContainer);
		
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
