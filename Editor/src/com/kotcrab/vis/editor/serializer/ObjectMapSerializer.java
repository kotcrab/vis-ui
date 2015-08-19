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

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;

import java.util.Iterator;

/**
 * Kryo {@link MapSerializer} slightly modified to serialize LibGDX {@link ObjectMap}
 * <p>
 * With the default constructor, a map requires a 1-3 byte header and an extra 4 bytes is written for each key/value pair.
 * @author Nathan Sweet <misc@n4te.com>
 */
public class ObjectMapSerializer extends Serializer<ObjectMap> {
	private Class keyClass, valueClass;
	private Serializer keySerializer, valueSerializer;
	private boolean keysCanBeNull = true, valuesCanBeNull = true;
	private Class keyGenericType, valueGenericType;

	/**
	 * @param keysCanBeNull False if all keys are not null. This saves 1 byte per key if keyClass is set. True if it is not known
	 * (default).
	 */
	public void setKeysCanBeNull (boolean keysCanBeNull) {
		this.keysCanBeNull = keysCanBeNull;
	}

	/**
	 * @param keyClass The concrete class of each key. This saves 1 byte per key. Set to null if the class is not known or varies
	 * per key (default).
	 * @param keySerializer The serializer to use for each key.
	 */
	public void setKeyClass (Class keyClass, Serializer keySerializer) {
		this.keyClass = keyClass;
		this.keySerializer = keySerializer;
	}

	/**
	 * @param valueClass The concrete class of each value. This saves 1 byte per value. Set to null if the class is not known or
	 * varies per value (default).
	 * @param valueSerializer The serializer to use for each value.
	 */
	public void setValueClass (Class valueClass, Serializer valueSerializer) {
		this.valueClass = valueClass;
		this.valueSerializer = valueSerializer;
	}

	/**
	 * @param valuesCanBeNull True if values are not null. This saves 1 byte per value if keyClass is set. False if it is not known
	 * (default).
	 */
	public void setValuesCanBeNull (boolean valuesCanBeNull) {
		this.valuesCanBeNull = valuesCanBeNull;
	}

	public void setGenerics (Kryo kryo, Class[] generics) {
		keyGenericType = null;
		valueGenericType = null;

		if (generics != null && generics.length > 0) {
			if (generics[0] != null && kryo.isFinal(generics[0])) keyGenericType = generics[0];
			if (generics.length > 1 && generics[1] != null && kryo.isFinal(generics[1])) valueGenericType = generics[1];
		}
	}

	public void write (Kryo kryo, Output output, ObjectMap map) {
		int length = map.size;
		output.writeInt(length, true);

		Serializer keySerializer = this.keySerializer;
		if (keyGenericType != null) {
			if (keySerializer == null) keySerializer = kryo.getSerializer(keyGenericType);
			keyGenericType = null;
		}
		Serializer valueSerializer = this.valueSerializer;
		if (valueGenericType != null) {
			if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueGenericType);
			valueGenericType = null;
		}

		for (Iterator iter = map.entries().iterator(); iter.hasNext(); ) {
			Entry entry = (Entry) iter.next();
			if (keySerializer != null) {
				if (keysCanBeNull)
					kryo.writeObjectOrNull(output, entry.key, keySerializer);
				else
					kryo.writeObject(output, entry.key, keySerializer);
			} else
				kryo.writeClassAndObject(output, entry.key);
			if (valueSerializer != null) {
				if (valuesCanBeNull)
					kryo.writeObjectOrNull(output, entry.value, valueSerializer);
				else
					kryo.writeObject(output, entry.value, valueSerializer);
			} else
				kryo.writeClassAndObject(output, entry.value);
		}
	}

	/**
	 * Used by {@link #read(Kryo, Input, Class)} to create the new object. This can be overridden to customize object creation, eg
	 * to call a constructor with arguments. The default implementation uses {@link Kryo#newInstance(Class)}.
	 */
	protected ObjectMap create (Kryo kryo, Input input, Class<ObjectMap> type) {
		return kryo.newInstance(type);
	}

	public ObjectMap read (Kryo kryo, Input input, Class<ObjectMap> type) {
		ObjectMap map = create(kryo, input, type);
		int length = input.readInt(true);

		Class keyClass = this.keyClass;
		Class valueClass = this.valueClass;

		Serializer keySerializer = this.keySerializer;
		if (keyGenericType != null) {
			keyClass = keyGenericType;
			if (keySerializer == null) keySerializer = kryo.getSerializer(keyClass);
			keyGenericType = null;
		}
		Serializer valueSerializer = this.valueSerializer;
		if (valueGenericType != null) {
			valueClass = valueGenericType;
			if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueClass);
			valueGenericType = null;
		}

		kryo.reference(map);

		for (int i = 0; i < length; i++) {
			Object key;
			if (keySerializer != null) {
				if (keysCanBeNull)
					key = kryo.readObjectOrNull(input, keyClass, keySerializer);
				else
					key = kryo.readObject(input, keyClass, keySerializer);
			} else
				key = kryo.readClassAndObject(input);
			Object value;
			if (valueSerializer != null) {
				if (valuesCanBeNull)
					value = kryo.readObjectOrNull(input, valueClass, valueSerializer);
				else
					value = kryo.readObject(input, valueClass, valueSerializer);
			} else
				value = kryo.readClassAndObject(input);
			map.put(key, value);
		}
		return map;
	}

	protected ObjectMap createCopy (Kryo kryo, ObjectMap original) {
		return kryo.newInstance(original.getClass());
	}

	public ObjectMap copy (Kryo kryo, ObjectMap original) {
		ObjectMap copy = createCopy(kryo, original);
		for (Iterator iter = original.entries().iterator(); iter.hasNext(); ) {
			Entry entry = (Entry) iter.next();
			copy.put(kryo.copy(entry.key), kryo.copy(entry.value));
		}
		return copy;
	}
}
