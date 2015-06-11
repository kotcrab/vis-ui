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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.scene.EditorScene;

public class EditorSceneSerializer extends CompatibleFieldSerializer<EditorScene> {
	private static final int VERSION_CODE = 1;

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
		EditorScene obj = super.read(kryo, input, sceneClass);

		input.readInt(); //version code

		return obj;
	}
}
