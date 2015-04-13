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
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.scene.SpriteObject;

public class SpriteObjectSerializer extends CompatibleFieldSerializer<SpriteObject> {
	private TextureCacheModule textureCache;

	public SpriteObjectSerializer (Kryo kryo, TextureCacheModule textureCache) {
		super(kryo, SpriteObject.class);
		this.textureCache = textureCache;
	}

	@Override
	public void write (Kryo kryo, Output output, SpriteObject obj) {
		super.write(kryo, output, obj);

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
	public SpriteObject read (Kryo kryo, Input input, Class<SpriteObject> type) {
		SpriteObject obj = super.read(kryo, input, type);
		obj.onDeserialize(textureCache.getRegion(obj.getAssetPath()));

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
	public SpriteObject copy (Kryo kryo, SpriteObject original) {
		return new SpriteObject(original, new Sprite(original.getSprite()));
	}
}
