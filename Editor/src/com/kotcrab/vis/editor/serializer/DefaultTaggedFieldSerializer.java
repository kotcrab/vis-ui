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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** @author Kotcrab */
public class DefaultTaggedFieldSerializer<T> extends VisTaggedFieldSerializer<T> {
	private static final int VERSION_CODE = 1;

	public DefaultTaggedFieldSerializer (Kryo kryo, Class type) {
		super(kryo, type);
	}

	@Override
	public void write (Kryo kryo, Output output, T scheme) {
		super.write(kryo, output, scheme);
		output.writeInt(VERSION_CODE);
	}

	@Override
	public T read (Kryo kryo, Input input, Class<T> type) {
		T obj = super.read(kryo, input, type);
		int versionCode = input.readInt();
		return obj;
	}
}
