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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.TextureCacheModule;
import com.kotcrab.vis.editor.scene.SpriteObject;

public class SpriteObjectSerializer extends Serializer<SpriteObject> {
	private Serializer defaultSerializer;
	private TextureCacheModule textureCache;

	public SpriteObjectSerializer (Kryo kryo, TextureCacheModule textureCache) {
		this.textureCache = textureCache;

		defaultSerializer = kryo.getSerializer(SpriteObject.class);
	}

	@Override
	public void write (Kryo kryo, Output output, SpriteObject obj) {
		kryo.setReferences(false);

		kryo.writeObject(output, obj, defaultSerializer);

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

		kryo.setReferences(true);
	}

	@Override
	public SpriteObject read (Kryo kryo, Input input, Class<SpriteObject> type) {
		kryo.setReferences(false);

		SpriteObject obj = kryo.readObject(input, SpriteObject.class, defaultSerializer);
		obj.onDeserialize(textureCache.getRegion(obj.getAssetPath()));

		obj.setPosition(input.readFloat(), input.readFloat());
		obj.setSize(input.readFloat(), input.readFloat());
		obj.setScale(input.readFloat(), input.readFloat());
		obj.setOrigin(input.readFloat(), input.readFloat());
		obj.setRotation(input.readFloat());
		obj.setColor(kryo.readObject(input, Color.class));
		obj.setFlip(input.readBoolean(), input.readBoolean());

		kryo.setReferences(true);

		return obj;
	}

	@Override
	public SpriteObject copy (Kryo kryo, SpriteObject original) {
		return new SpriteObject(original, new Sprite(original.getSprite()));
	}

}
