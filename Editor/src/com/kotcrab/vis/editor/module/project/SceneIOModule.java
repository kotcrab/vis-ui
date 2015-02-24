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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.ui.SpriteSerializer;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.SpriteUtils;
import com.kotcrab.vis.runtime.data.SpriteData;
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
		cacheModule = projectContainer.get(TextureCacheModule.class);
		fileAccessModule = projectContainer.get(FileAccessModule.class);

		visFolder = fileAccessModule.getVisFolder();

		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(SceneViewport.class);
		kryo.register(EditorScene.class);
		kryo.register(EditorEntity.class);
		kryo.register(SpriteObject.class);
		kryo.register(SpriteData.class);
		kryo.register(Sprite.class, new SpriteSerializer());
	}

	public EditorScene load (FileHandle file) {
		try {
			Input input = new Input(new FileInputStream(file.file()));
			EditorScene scene = kryo.readObject(input, EditorScene.class);
			scene.path = fileAccessModule.relativizeToVisFolder(file.path());
			input.close();

			prepareSceneAfterLoad(scene);

			return scene;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}
		return null;
	}

	private void prepareSceneAfterLoad (EditorScene scene) {
		for (EditorEntity entity : scene.entities) {
			if (entity instanceof SpriteObject) {
				SpriteObject spriteObject = (SpriteObject) entity;
				SpriteUtils.setRegion(spriteObject.sprite, cacheModule.getRegion(spriteObject.regionRelativePath));
			}
		}
	}

	public boolean save (EditorScene scene) {
		//if needed here prepare scene for save

		try {
			Output output = new Output(new FileOutputStream(getFileHandleForScene(scene).file()));
			kryo.writeObject(output, scene);
			output.close();
			return true;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		return false;
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport, int width, int height) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport, width, height);
		save(scene);
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return visFolder.child(scene.path);
	}
}
