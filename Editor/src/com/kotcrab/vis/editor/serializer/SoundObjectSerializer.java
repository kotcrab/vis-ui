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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.scene.SoundObject;

public class SoundObjectSerializer extends Serializer<SoundObject> {
	private Serializer defaultSerializer;
	private FileAccessModule fileAccess;

	public SoundObjectSerializer (Kryo kryo, FileAccessModule fileAccess) {
		this.fileAccess = fileAccess;
		defaultSerializer = kryo.getSerializer(SoundObject.class);
	}

	@Override
	public void write (Kryo kryo, Output output, SoundObject musicObj) {
		kryo.setReferences(false);
		kryo.writeObject(output, musicObj, defaultSerializer);
		kryo.setReferences(true);
	}

	@Override
	public SoundObject read (Kryo kryo, Input input, Class<SoundObject> type) {
		kryo.setReferences(false);
		SoundObject obj = kryo.readObject(input, SoundObject.class, defaultSerializer);
		obj.onDeserialize(getNewSoundInstance(obj));
		kryo.setReferences(true);
		return obj;
	}

	@Override
	public SoundObject copy (Kryo kryo, SoundObject original) {
		return new SoundObject(original, getNewSoundInstance(original));
	}

	private Sound getNewSoundInstance (SoundObject object) {
		return Gdx.audio.newSound(fileAccess.getAssetsFolder().child(object.getAssetPath()));
	}
}
