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
import com.kotcrab.vis.editor.scene.SoundObject;

public class SoundObjectSerializer extends CompatibleFieldSerializer<SoundObject> {
	@InjectModule private FileAccessModule fileAccess;

	public SoundObjectSerializer (Kryo kryo, ModuleInjector injector) {
		super(kryo, SoundObject.class);
		injector.injectModules(this);
	}

	@Override
	public void write (Kryo kryo, Output output, SoundObject musicObj) {
		super.write(kryo, output, musicObj);
	}

	@Override
	public SoundObject read (Kryo kryo, Input input, Class<SoundObject> type) {
		SoundObject obj = super.read(kryo, input, type);
		obj.onDeserialize();
		return obj;
	}

	@Override
	public SoundObject copy (Kryo kryo, SoundObject original) {
		return new SoundObject(original);
	}
}
