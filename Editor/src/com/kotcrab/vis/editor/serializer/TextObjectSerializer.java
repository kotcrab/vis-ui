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
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.TextObject;

/**
 * Kryo serializer for {@link TextObject}
 * @author Kotcrab
 */
@Deprecated
public class TextObjectSerializer extends CompatibleFieldSerializer<TextObject> {
	private static final int VERSION_CODE = 1;

	private FileAccessModule fileAccess;
	private final FontCacheModule fontCache;

	public TextObjectSerializer (Kryo kryo, FileAccessModule fileAccess, FontCacheModule fontCache) {
		super(kryo, TextObject.class);
		this.fileAccess = fileAccess;
		this.fontCache = fontCache;
	}

	@Override
	public void write (Kryo kryo, Output output, TextObject textObject) {
		super.write(kryo, output, textObject);

		output.writeInt(VERSION_CODE);
	}

	@Override
	public TextObject read (Kryo kryo, Input input, Class<TextObject> type) {
		TextObject obj = super.read(kryo, input, type);

		input.readInt(); //version code

		obj.onDeserialize(fontCache.get(obj.getAssetDescriptor()));

		return obj;
	}

	@Override
	public TextObject copy (Kryo kryo, TextObject original) {
		return new TextObject(original);
	}
}
