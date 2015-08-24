/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.serializer;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.scene.PhysicsSettings;
import com.kotcrab.vis.runtime.scene.LayerCordsSystem;

import java.lang.reflect.Field;

/**
 * Kryo serializer for {@link EditorScene}
 * @author Kotcrab
 */
public class EditorSceneSerializer extends VisTaggedFieldSerializer<EditorScene> {
	private static final int VERSION_CODE = 4;

	public EditorSceneSerializer (Kryo kryo) {
		super(kryo, EditorScene.class);
	}

	@Override
	public void write (Kryo kryo, Output output, EditorScene scene) {
		super.write(kryo, output, scene);

		output.writeInt(VERSION_CODE);
	}

	@Override
	public EditorScene read (Kryo kryo, Input input, Class<EditorScene> sceneClass) {
		EditorScene scene = super.read(kryo, input, sceneClass);

		scene.onDeserialize();
		int versionCode = input.readInt(); //version code

		if (versionCode == 1) {
			//groupIds was added in 0.2.1, need manual creation set if loading old scene version
			try {
				Field field = EditorScene.class.getDeclaredField("groupIds");
				field.setAccessible(true);
				field.set(scene, new IntMap<String>());
			} catch (ReflectiveOperationException e) {
				Log.exception(e);
			}

			versionCode = 2;
			Log.info("Updating EditorScene to version code 2");
		}

		if (versionCode == 2) {
			//coordinates system was added in 0.2.1, requires manual default cords set
			for (Layer layer : scene.getLayers()) {
				layer.cordsSystem = LayerCordsSystem.WORLD;
			}

			versionCode = 3;
			Log.info("Updating EditorScene to version code 3");
		}

		if (versionCode == 3) {
			//physics settings was added in 0.2.1, requires manual creation of PhysicsSettings object

			scene.physicsSettings = new PhysicsSettings();

			versionCode = 4;
			Log.info("Updating EditorScene to version code 4");
		}

		return scene;
	}
}
