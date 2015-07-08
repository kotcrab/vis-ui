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

import com.badlogic.gdx.utils.IntArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class IntArraySerializer extends Serializer<IntArray> {
	public IntArraySerializer () {
		super(true);
	}

	@Override
	public void write (Kryo kryo, Output output, IntArray array) {
		int length = array.size;
		output.writeInt(length, true);
		if (length == 0) return;
		for (int i = 0, n = array.size; i < n; i++)
			output.writeInt(array.get(i), true);
	}

	@Override
	public IntArray read (Kryo kryo, Input input, Class<IntArray> type) {
		IntArray array = new IntArray();
		kryo.reference(array);
		int length = input.readInt(true);
		array.ensureCapacity(length);
		for (int i = 0; i < length; i++)
			array.add(input.readInt(true));
		return array;
	}

	@Override
	public IntArray copy (Kryo kryo, IntArray original) {
		IntArray array = new IntArray(original.size);

		for (int i = 0; i < original.size; i++)
			array.add(original.get(i));

		return array;
	}
}
