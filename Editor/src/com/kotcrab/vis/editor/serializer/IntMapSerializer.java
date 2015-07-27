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

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author Kotcrab
 */
public class IntMapSerializer extends Serializer<IntMap> {
	private Class genericType;

	public IntMapSerializer () {
		super(true);
	}

	@Override
	public void setGenerics (Kryo kryo, Class[] generics) {
		if (kryo.isFinal(generics[0])) genericType = generics[0];
	}

	@Override
	public void write (Kryo kryo, Output output, IntMap map) {
		int length = map.size;
		output.writeInt(length, true);
		if (length == 0) return;
		if (genericType != null) {
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (Object object : map.entries()) {
				Entry entry = (Entry) object;
				output.writeInt(entry.key);
				kryo.writeObjectOrNull(output, entry.value, serializer);
			}
		} else {
			for (Object object : map.entries()) {
				Entry entry = (Entry) object;
				output.writeInt(entry.key);
				kryo.writeClassAndObject(output, entry.value);
			}
		}
	}

	@Override
	public IntMap read (Kryo kryo, Input input, Class<IntMap> mapType) {
		IntMap map = new IntMap();
		kryo.reference(map);
		int length = input.readInt(true);
		map.ensureCapacity(length);
		if (genericType != null) {
			Class elementClass = genericType;
			Serializer serializer = kryo.getSerializer(genericType);
			genericType = null;
			for (int i = 0; i < length; i++) {
				int key = input.readInt();
				Object value = kryo.readObjectOrNull(input, elementClass, serializer);
				map.put(key, value);
			}
		} else {
			for (int i = 0; i < length; i++) {
				int key = input.readInt();
				Object value = kryo.readClassAndObject(input);
				map.put(key, value);
			}
		}
		return map;
	}

	@Override
	public IntMap copy (Kryo kryo, IntMap original) {
		IntMap map = new IntMap(original.size);

		for (Object object : original.entries()) {
			Entry entry = (Entry) object;
			map.put(entry.key, kryo.copy(entry.value));
		}

		return map;
	}
}
