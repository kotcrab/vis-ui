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

package pl.kotcrab.vis.editor.module.project;

import pl.kotcrab.vis.editor.module.Module;

public abstract class ProjectModule extends Module {
	protected Project project;
	protected ProjectModuleContainer projectContainter;

	void setProject (Project project) {
		this.project = project;
	}

	void setProjectModuleContainter (ProjectModuleContainer container) {
		this.projectContainter = container;
	}
}
