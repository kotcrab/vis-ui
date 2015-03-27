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

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.editor.scene.ParticleObject;

public class ParticleObjectSerializer extends Serializer<ParticleObject> {
	private Serializer defaultSerializer;
	private FileAccessModule fileAccess;
	private ParticleCacheModule particleCache;

	public ParticleObjectSerializer (Kryo kryo, FileAccessModule fileAccess, ParticleCacheModule particleCache) {
		this.fileAccess = fileAccess;
		this.particleCache = particleCache;

		defaultSerializer = kryo.getSerializer(ParticleObject.class);
	}

	@Override
	public void write (Kryo kryo, Output output, ParticleObject obj) {
		kryo.setReferences(false);

		kryo.writeObject(output, obj, defaultSerializer);
		output.writeFloat(obj.getX());
		output.writeFloat(obj.getY());

		kryo.setReferences(true);
	}

	@Override
	public ParticleObject read (Kryo kryo, Input input, Class<ParticleObject> type) {
		kryo.setReferences(false);

		ParticleObject obj = kryo.readObject(input, ParticleObject.class, defaultSerializer);

		ParticleEffect effect = getNewEffect(obj);
		obj.onDeserialize(effect, input.readFloat(), input.readFloat());

		kryo.setReferences(true);

		return obj;
	}

	@Override
	public ParticleObject copy (Kryo kryo, ParticleObject original) {
		return new ParticleObject(original, getNewEffect(original));
	}

	private ParticleEffect getNewEffect (ParticleObject obj) {
		return particleCache.get(fileAccess.getAssetsFolder().child(obj.getRelativeEffectPath()));
	}
}
