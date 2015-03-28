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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.scene.SceneViewport;

public class EditorScene {
	/** Scene file, path is relative to project vis folder */
	public String path;
	public int width;
	public int height;
	public SceneViewport viewport;

	public Array<EditorEntity> entities = new Array<EditorEntity>();

	public EditorScene (FileHandle file, SceneViewport viewport, int width, int height) {
		this.path = file.path();
		this.viewport = viewport;
		this.width = width;
		this.height = height;
	}

	public FileHandle getFile () {
		return Gdx.files.absolute(path);
	}
}
