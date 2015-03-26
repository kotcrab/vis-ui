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
import com.badlogic.gdx.audio.Music;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.scene.MusicObject;

public class MusicObjectSerializer extends Serializer<MusicObject> {
	private Serializer defaultSerializer;
	private FileAccessModule fileAccess;

	public MusicObjectSerializer (Kryo kryo, FileAccessModule fileAccess) {
		this.fileAccess = fileAccess;
		defaultSerializer = kryo.getSerializer(MusicObject.class);
	}

	@Override
	public void write (Kryo kryo, Output output, MusicObject musicObj) {
		kryo.setReferences(false);

		kryo.writeObject(output, musicObj, defaultSerializer);
		output.writeBoolean(musicObj.isLooping());
		output.writeFloat(musicObj.getVolume());

		kryo.setReferences(true);
	}

	@Override
	public MusicObject read (Kryo kryo, Input input, Class<MusicObject> type) {
		kryo.setReferences(false);

		MusicObject obj = kryo.readObject(input, MusicObject.class, defaultSerializer);

		obj.onDeserialize(getNewMuscInstance(obj));
		obj.setLooping(input.readBoolean());
		obj.setVolume(input.readFloat());

		kryo.setReferences(true);

		return obj;
	}

	@Override
	public MusicObject copy (Kryo kryo, MusicObject original) {
		return new MusicObject(original, getNewMuscInstance(original));
	}

	private Music getNewMuscInstance (MusicObject object) {
		return Gdx.audio.newMusic(fileAccess.getAssetsFolder().child(object.getMusicPath()));
	}
}
