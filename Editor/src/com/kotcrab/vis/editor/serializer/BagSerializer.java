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

import com.artemis.utils.Bag;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

/**
 * Kryo serializer for {@link Bag}
 */
public class BagSerializer extends Serializer<Bag> {
	private Class genericType;

	public BagSerializer () {
		super(true);
	}

	@Override
	public void setGenerics (Kryo kryo, Class[] generics) {
		if (kryo.isFinal(generics[0])) genericType = generics[0];
	}

	@Override
	public void write (Kryo kryo, Output output, Bag bag) {
		int length = bag.size();
		output.writeInt(length, true);
		if (length == 0) return;
		if (genericType != null) {
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (Object element : bag)
				kryo.writeObjectOrNull(output, element, serializer);
		} else {
			for (Iterator iterator = bag.iterator(); iterator.hasNext(); ) {
				Object element = iterator.next();
				kryo.writeClassAndObject(output, element);
			}
		}
	}

	@Override
	public Bag read (Kryo kryo, Input input, Class<Bag> type) {
		Bag bag = new Bag();
		kryo.reference(bag);
		int length = input.readInt(true);
		bag.ensureCapacity(length);
		if (genericType != null) {
			Class elementClass = genericType;
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (int i = 0; i < length; i++)
				bag.add(kryo.readObjectOrNull(input, elementClass, serializer));
		} else {
			for (int i = 0; i < length; i++)
				bag.add(kryo.readClassAndObject(input));
		}
		return bag;
	}

	@Override
	public Bag copy (Kryo kryo, Bag original) {
		Bag bag = new Bag(original.size());

		for (Object obj : original)
			bag.add(kryo.copy(obj));

		return bag;
	}
}
