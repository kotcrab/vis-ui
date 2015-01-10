/**
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

package pl.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import pl.kotcrab.vis.editor.module.project.EditorFileType;
import pl.kotcrab.vis.editor.module.project.FileAccessModule;
import pl.kotcrab.vis.editor.module.project.ProjectModule;
import pl.kotcrab.vis.runtime.scene.SceneViewport;

@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	private Json json;

	private FileAccessModule fileAccessModule;

	private FileHandle visFolder;

	@Override
	public void init () {
		fileAccessModule = projectContainter.get(FileAccessModule.class);

		visFolder = fileAccessModule.getVisFolder();

		json = new Json();
	}

	public EditorScene load (FileHandle file) {
		return json.fromJson(EditorScene.class, file);
	}

	public void save (EditorScene scene) {
		json.toJson(scene, getFileHandleForScene(scene));
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport);
		fileAccessModule.addFileType(getFileHandleForScene(scene), EditorFileType.SCENE);
		save(scene);
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return visFolder.child(scene.path);
	}
}
