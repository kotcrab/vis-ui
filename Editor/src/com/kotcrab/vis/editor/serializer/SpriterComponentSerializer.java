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
import com.kotcrab.vis.editor.module.project.SpriterCacheModule;
import com.kotcrab.vis.runtime.assets.SpriterAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ShaderComponent;
import com.kotcrab.vis.runtime.component.SpriterComponent;

/**
 * Kryo serializer for {@link SpriterComponent}
 * @author Kotcrab
 */
public class SpriterComponentSerializer extends EntityComponentSerializer<SpriterComponent> {
	private static final int VERSION_CODE = 1;
	private final SpriterCacheModule cache;

	public SpriterComponentSerializer (Kryo kryo, SpriterCacheModule shaderCache) {
		super(kryo, ShaderComponent.class);
		this.cache = shaderCache;
	}

	@Override
	public void write (Kryo kryo, Output output, SpriterComponent comp) {
		super.write(kryo, output, comp);

		output.writeInt(VERSION_CODE);

		kryo.writeClassAndObject(output, getComponent(AssetComponent.class).asset);

		output.writeFloat(comp.getX());
		output.writeFloat(comp.getY());

		output.writeFloat(comp.player.getScale());

		output.writeFloat(comp.getRotation());

		output.writeBoolean(comp.isFlipX());
		output.writeBoolean(comp.isFlipY());

		output.writeBoolean(comp.playOnStart);
		output.writeInt(comp.defaultAnimation);
	}

	@Override
	public SpriterComponent read (Kryo kryo, Input input, Class<SpriterComponent> type) {
		super.read(kryo, input, type);

		input.readInt(); //version code

		SpriterAsset asset = (SpriterAsset) kryo.readClassAndObject(input);

		SpriterComponent comp = cache.createComponent(asset, 1);

		comp.setPosition(input.readFloat(), input.readFloat());
		comp.player.setScale(input.readFloat());
		comp.setRotation(input.readFloat());
		comp.setFlip(input.readBoolean(), input.readBoolean());
		comp.onDeserialize(input.readBoolean(), input.readInt());

		return comp;
	}

	@Override
	public SpriterComponent copy (Kryo kryo, SpriterComponent original) {
		super.copy(kryo, original);
		return cache.cloneComponent((SpriterAsset) getComponent(AssetComponent.class).asset, original);
	}
}
