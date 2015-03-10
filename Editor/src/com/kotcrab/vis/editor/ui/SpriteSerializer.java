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

package com.kotcrab.vis.editor.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SpriteSerializer extends Serializer<Sprite> {
	@Override
	public void write (Kryo kryo, Output output, Sprite sprite) {
		output.writeFloat(sprite.getX());
		output.writeFloat(sprite.getY());

		output.writeFloat(sprite.getWidth());
		output.writeFloat(sprite.getHeight());

		output.writeFloat(sprite.getScaleX());
		output.writeFloat(sprite.getScaleY());

		output.writeFloat(sprite.getOriginX());
		output.writeFloat(sprite.getOriginY());

		output.writeFloat(sprite.getRotation());

		kryo.writeObject(output, sprite.getColor());

		output.writeBoolean(sprite.isFlipX());
		output.writeBoolean(sprite.isFlipY());
	}

	@Override
	public Sprite read (Kryo kryo, Input input, Class<Sprite> type) {
		Sprite sprite = new Sprite();

		kryo.reference(sprite);

		sprite.setPosition(input.readFloat(), input.readFloat());
		sprite.setSize(input.readFloat(), input.readFloat());
		sprite.setScale(input.readFloat(), input.readFloat());
		sprite.setOrigin(input.readFloat(), input.readFloat());
		sprite.setRotation(input.readFloat());
		sprite.setColor(kryo.readObject(input, Color.class));
		sprite.setFlip(input.readBoolean(), input.readBoolean());

		return sprite;
	}

	@Override
	public Sprite copy (Kryo kryo, Sprite original) {
		return new Sprite(original);
	}
}
