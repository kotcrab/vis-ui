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
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.scene.MusicObject;

/**
 * Kryo serializer for {@link MusicObject}
 * @author Kotcrab
 */
public class MusicObjectSerializer extends CompatibleFieldSerializer<MusicObject> {
	private static final int VERSION_CODE = 1;

	@InjectModule private FileAccessModule fileAccess;

	public MusicObjectSerializer (Kryo kryo, ModuleInjector injector) {
		super(kryo, MusicObject.class);
		injector.injectModules(this);
	}

	@Override
	public void write (Kryo kryo, Output output, MusicObject musicObj) {
		super.write(kryo, output, musicObj);

		output.writeInt(VERSION_CODE);

		output.writeBoolean(musicObj.isLooping());
		output.writeFloat(musicObj.getVolume());
	}

	@Override
	public MusicObject read (Kryo kryo, Input input, Class<MusicObject> type) {
		MusicObject obj = super.read(kryo, input, type);

		input.readInt(); //version code

		obj.onDeserialize();
		obj.setLooping(input.readBoolean());
		obj.setVolume(input.readFloat());

		return obj;
	}

	@Override
	public MusicObject copy (Kryo kryo, MusicObject original) {
		return new MusicObject(original);
	}
}
