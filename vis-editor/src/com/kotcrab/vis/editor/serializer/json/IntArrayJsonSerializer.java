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

package com.kotcrab.vis.editor.serializer.json;

import com.badlogic.gdx.utils.IntArray;
import com.google.gson.*;

import java.lang.reflect.Type;

/** @author Kotcrab */
public class IntArrayJsonSerializer implements JsonSerializer<IntArray>, JsonDeserializer<IntArray> {
	@Override
	public JsonElement serialize (IntArray intArray, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray json = new JsonArray();

		for (int i = 0; i < intArray.size; i++) {
			json.add(new JsonPrimitive(intArray.get(i)));
		}

		return json;
	}

	@Override
	public IntArray deserialize (JsonElement j, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonArray json = j.getAsJsonArray();

		IntArray intArray = new IntArray(json.size());

		for (int i = 0; i < json.size(); i++) {
			intArray.add(json.get(i).getAsInt());
		}

		return intArray;
	}
}
