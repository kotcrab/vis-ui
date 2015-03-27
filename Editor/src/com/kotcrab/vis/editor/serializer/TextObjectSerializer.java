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

		obj.onDeserialize(fontCache.get(fileAccess.getAssetsFolder().child(obj.getRelativeFontPath())));
		return obj;
	}

	@Override
	public TextObject copy (Kryo kryo, TextObject original) {
		return new TextObject(original);
	}
}
