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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.module.scene.EditorScene;
import com.kotcrab.vis.editor.module.scene.Object2d;
import com.kotcrab.vis.editor.module.scene.SceneObject;
import com.kotcrab.vis.runtime.scene.SceneViewport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	private Kryo kryo;

	private TextureCacheModule cacheModule;
	private FileAccessModule fileAccessModule;

	private FileHandle visFolder;

	@Override
	public void init () {
		cacheModule = projectContainter.get(TextureCacheModule.class);
		fileAccessModule = projectContainter.get(FileAccessModule.class);

		visFolder = fileAccessModule.getVisFolder();

		kryo = new Kryo();
	}

	public EditorScene load (FileHandle file) {
		try {
			Input input = new Input(new FileInputStream(file.file()));
			EditorScene scene = kryo.readObject(input, EditorScene.class);
			input.close();

			prepareScene(scene);

			return scene;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void prepareScene (EditorScene scene) {
		for (SceneObject object : scene.objects) {
			if (object instanceof Object2d) {
				Object2d object2d = (Object2d) object;
				object2d.region = cacheModule.getRegion(object2d.regionRelativePath);
			}
		}
	}

	public boolean save (EditorScene scene) {
		try {
			Output output = new Output(new FileOutputStream(getFileHandleForScene(scene).file()));
			kryo.writeObject(output, scene);
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport);
		save(scene);
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return visFolder.child(scene.path);
	}
}
