
package pl.kotcrab.vis.editor.module;

import pl.kotcrab.vis.editor.Project;

public class ProjectModuleContainer extends ModuleContainer {
	private Project project;

	@Override
	public void add (Module module) {
		if (module instanceof ProjectModule == false)
			throw new IllegalStateException("Module must be instance of Project Module!");
		if (project == null) throw new IllegalStateException("Module cannot be added before project has been set!");

		((ProjectModule)module).setProject(project);
		super.add(module);
	}

	public void setProject (Project project) {
		if (getModuleCounter() > 0) throw new IllegalStateException("Project can't be changed while modules are loaded!");
	}
}
