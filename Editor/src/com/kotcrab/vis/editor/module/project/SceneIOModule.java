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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.MusicObject;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.serializer.ArraySerializer;
import com.kotcrab.vis.editor.serializer.ColorSerializer;
import com.kotcrab.vis.editor.serializer.MusicObjectSerializer;
import com.kotcrab.vis.editor.serializer.ParticleObjectSerializer;
import com.kotcrab.vis.editor.serializer.SpriteObjectSerializer;
import com.kotcrab.vis.editor.serializer.TextObjectSerializer;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.runtime.scene.SceneViewport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@SuppressWarnings("rawtypes")
public class SceneIOModule extends ProjectModule {
	private Kryo kryo;

	private FileAccessModule fileAccessModule;

	private FileHandle assetsFolder;

	@Override
	public void init () {
		fileAccessModule = projectContainer.get(FileAccessModule.class);
		assetsFolder = fileAccessModule.getAssetsFolder();

		TextureCacheModule textureCache = projectContainer.get(TextureCacheModule.class);
		FontCacheModule fontCache = projectContainer.get(FontCacheModule.class);
		ParticleCacheModule particleCache = projectContainer.get(ParticleCacheModule.class);

		kryo = new Kryo();
		kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
		kryo.register(Array.class, new ArraySerializer(), 10);
		kryo.register(Rectangle.class, 11);
		kryo.register(TextBounds.class, 12);
		kryo.register(Matrix4.class, 13);
		kryo.register(Color.class, new ColorSerializer(), 14);

		kryo.register(SpriteObject.class, new SpriteObjectSerializer(kryo, textureCache), 30);
		kryo.register(MusicObject.class, new MusicObjectSerializer(kryo, fileAccessModule), 31);
		kryo.register(ParticleObject.class, new ParticleObjectSerializer(kryo, fileAccessModule, particleCache), 32);
		kryo.register(TextObject.class, new TextObjectSerializer(kryo, fileAccessModule, fontCache), 33);
	}

	public Kryo getKryo () {
		return kryo;
	}

	public EditorScene load (FileHandle fullPathFile) {
		try {
			Input input = new Input(new FileInputStream(fullPathFile.file()));
			EditorScene scene = kryo.readObject(input, EditorScene.class);
			scene.path = fileAccessModule.relativizeToAssetsFolder(fullPathFile);
			input.close();

			return scene;
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("There was an error during scene deserializing");
	}

	public boolean save (EditorScene scene) {
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
		return assetsFolder.child(scene.path);
	}
}
