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

import com.kotcrab.vis.runtime.scene.SceneViewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class EditorScene {
	/** Scene file, path is relative to project vis folder */
	public String path;
	public SceneViewport viewport;
	public Array<SceneObject> objects = new Array<SceneObject>();

	public EditorScene () {
	}

	public EditorScene (FileHandle file, SceneViewport viewport) {
		this.path = file.path();
		this.viewport = viewport;
	}

	public FileHandle getFile () {
		return Gdx.files.absolute(path);
	}
}
