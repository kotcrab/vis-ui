/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo serializer for LibGDX {@link Array}
 */
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
