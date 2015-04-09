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
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.scene.TextObject;

public class TextObjectSerializer extends Serializer<TextObject> {
	private Serializer defaultSerializer;
	private FileAccessModule fileAccess;
	private final FontCacheModule fontCache;

	public TextObjectSerializer (Kryo kryo, FileAccessModule fileAccess, FontCacheModule fontCache) {
		this.fileAccess = fileAccess;
		this.fontCache = fontCache;
		defaultSerializer = kryo.getSerializer(TextObject.class);
	}

	@Override
	public void write (Kryo kryo, Output output, TextObject textObject) {
		kryo.setReferences(false);
		kryo.writeObject(output, textObject, defaultSerializer);
		kryo.setReferences(true);
	}

	@Override
	public TextObject read (Kryo kryo, Input input, Class<TextObject> type) {
		kryo.setReferences(false);
		TextObject obj = kryo.readObject(input, TextObject.class, defaultSerializer);
		kryo.setReferences(true);

		obj.onDeserialize(fontCache.get(fileAccess.getAssetsFolder().child(obj.getAssetPath())));
		return obj;
	}

	@Override
	public TextObject copy (Kryo kryo, TextObject original) {
		return new TextObject(original);
	}
}
