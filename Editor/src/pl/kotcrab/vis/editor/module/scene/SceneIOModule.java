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

import pl.kotcrab.vis.editor.module.project.ProjectModule;

import com.badlogic.gdx.files.FileHandle;

public class SceneIOModule extends ProjectModule {

	private SceneTabsControllerModule tabsControllerModule;

	@Override
	public void init () {
		tabsControllerModule = projectContainter.get(SceneTabsControllerModule.class);
	}

	public EditorScene load (FileHandle file) {
		return null;
	}

	public void save (EditorScene file) {

	}

	// TODO this should not take visFolder, it should get it from FileAccessModule
	public void create (FileHandle visFolder, FileHandle relativeScenePath) {

	}
}
