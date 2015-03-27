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

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ArraySerializer extends Serializer<Array> {
	private Class genericType;

	public ArraySerializer () {
		super(true);
	}

	@Override
	public void setGenerics (Kryo kryo, Class[] generics) {
		if (kryo.isFinal(generics[0])) genericType = generics[0];
	}

	@Override
	public void write (Kryo kryo, Output output, Array array) {
		int length = array.size;
		output.writeInt(length, true);
		if (length == 0) return;
		if (genericType != null) {
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (Object element : array)
				kryo.writeObjectOrNull(output, element, serializer);
		} else {
			for (Object element : array)
				kryo.writeClassAndObject(output, element);
		}
	}

	@Override
	public Array read (Kryo kryo, Input input, Class<Array> type) {
		Array array = new Array();
		kryo.reference(array);
		int length = input.readInt(true);
		array.ensureCapacity(length);
		if (genericType != null) {
			Class elementClass = genericType;
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (int i = 0; i < length; i++)
				array.add(kryo.readObjectOrNull(input, elementClass, serializer));
		} else {
			for (int i = 0; i < length; i++)
				array.add(kryo.readClassAndObject(input));
		}
		return array;
	}

	@Override
	public Array copy (Kryo kryo, Array original) {
		Array array = new Array(original.size);

		for (Object obj : original)
			array.add(kryo.copy(obj));

		return array;
	}
}
