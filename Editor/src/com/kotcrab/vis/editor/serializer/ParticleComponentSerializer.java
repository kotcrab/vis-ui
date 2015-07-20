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
import com.kotcrab.vis.editor.entity.PixelsPerUnitComponent;
import com.kotcrab.vis.editor.module.project.ParticleCacheModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ParticleComponent;

/**
 * @author Kotcrab
 */
public class ParticleComponentSerializer extends EntityComponentSerializer<ParticleComponent> {
	private static final int VERSION_CODE = 1;

	private ParticleCacheModule particleCache;

	public ParticleComponentSerializer (Kryo kryo, ParticleCacheModule particleCache) {
		super(kryo, ParticleComponent.class);
		this.particleCache = particleCache;
	}

	@Override
	public void write (Kryo kryo, Output output, ParticleComponent obj) {
		super.write(kryo, output, obj);
		parentWrite(kryo, output, obj);

		output.writeInt(VERSION_CODE);

		kryo.writeClassAndObject(output, getComponent(AssetComponent.class).asset);

		output.writeFloat(getComponent(PixelsPerUnitComponent.class).scale);

		output.writeFloat(obj.getX());
		output.writeFloat(obj.getY());
	}

	@Override
	public ParticleComponent read (Kryo kryo, Input input, Class<ParticleComponent> type) {
		super.read(kryo, input, type);
		ParticleComponent component = parentRead(kryo, input, type);

		input.readInt(); //version code

		VisAssetDescriptor asset = (VisAssetDescriptor) kryo.readClassAndObject(input);

		float scale = input.readFloat();

		ParticleEffect effect = getNewEffect(asset, scale);
		effect.setPosition(input.readFloat(), input.readFloat());
		component.effect = effect;

		return component;
	}

	@Override
	public ParticleComponent copy (Kryo kryo, ParticleComponent original) {
		super.copy(kryo, original);
		return new ParticleComponent(original, getNewEffect(getComponent(AssetComponent.class).asset, getComponent(PixelsPerUnitComponent.class).scale));
	}

	private ParticleEffect getNewEffect (VisAssetDescriptor asset, float scaleFactor) {
		return particleCache.get(asset, scaleFactor);
	}
}
