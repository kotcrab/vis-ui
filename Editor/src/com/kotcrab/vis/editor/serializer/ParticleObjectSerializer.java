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

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.editor.scene.ParticleEffectObject;

public class ParticleObjectSerializer extends CompatibleFieldSerializer<ParticleEffectObject> {
	private FileAccessModule fileAccess;
	private ParticleCacheModule particleCache;

	public ParticleObjectSerializer (Kryo kryo, FileAccessModule fileAccess, ParticleCacheModule particleCache) {
		super(kryo, ParticleEffectObject.class);
		this.fileAccess = fileAccess;
		this.particleCache = particleCache;
	}

	@Override
	public void write (Kryo kryo, Output output, ParticleEffectObject obj) {
		super.write(kryo, output, obj);
		output.writeFloat(obj.getX());
		output.writeFloat(obj.getY());
	}

	@Override
	public ParticleEffectObject read (Kryo kryo, Input input, Class<ParticleEffectObject> type) {
		ParticleEffectObject obj = super.read(kryo, input, type);

		ParticleEffect effect = getNewEffect(obj);
		obj.onDeserialize(effect, input.readFloat(), input.readFloat());

		return obj;
	}

	@Override
	public ParticleEffectObject copy (Kryo kryo, ParticleEffectObject original) {
		return new ParticleEffectObject(original, getNewEffect(original));
	}

	private ParticleEffect getNewEffect (ParticleEffectObject obj) {
		return particleCache.get(obj.getAssetDescriptor());
	}
}
