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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.SpriteComponent;

/**
 * Kryo serializer for {@link SpriteComponent}
 * @author Kotcrab
 */
public class SpriteComponentSerializer extends EntityComponentSerializer<SpriteComponent> {
	private static final int VERSION_CODE = 1;

	private TextureCacheModule textureCache;

	public SpriteComponentSerializer (Kryo kryo, TextureCacheModule textureCache) {
		super(kryo, SpriteComponent.class);
		this.textureCache = textureCache;
	}

	@Override
	public void write (Kryo kryo, Output output, SpriteComponent obj) {
		super.write(kryo, output, obj);
		output.writeInt(VERSION_CODE);

		kryo.writeClassAndObject(output, getComponent(AssetComponent.class).asset);

		output.writeFloat(obj.getX());
		output.writeFloat(obj.getY());

		output.writeFloat(obj.getWidth());
		output.writeFloat(obj.getHeight());

		output.writeFloat(obj.getScaleX());
		output.writeFloat(obj.getScaleY());

		output.writeFloat(obj.getOriginX());
		output.writeFloat(obj.getOriginY());

		output.writeFloat(obj.getRotation());

		kryo.writeObject(output, obj.getColor());

		output.writeBoolean(obj.isFlipX());
		output.writeBoolean(obj.isFlipY());
	}

	@Override
	public SpriteComponent read (Kryo kryo, Input input, Class<SpriteComponent> type) {
		super.read(kryo, input, type);
		input.readInt(); //version code

		VisAssetDescriptor asset = (VisAssetDescriptor) kryo.readClassAndObject(input);
		SpriteComponent obj = new SpriteComponent(textureCache.getSprite(asset, 1));

		obj.setPosition(input.readFloat(), input.readFloat());
		obj.setSize(input.readFloat(), input.readFloat());
		obj.setScale(input.readFloat(), input.readFloat());
		obj.setOrigin(input.readFloat(), input.readFloat());
		obj.setRotation(input.readFloat());
		obj.setColor(kryo.readObject(input, Color.class));
		obj.setFlip(input.readBoolean(), input.readBoolean());

		return obj;
	}

	@Override
	public SpriteComponent copy (Kryo kryo, SpriteComponent original) {
		super.copy(kryo, original);
		return new SpriteComponent(new Sprite(original.sprite));
	}
}
