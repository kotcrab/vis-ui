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

import com.badlogic.gdx.utils.Array;
import com.google.gson.*;

import java.lang.reflect.Type;

/** @author Kotcrab */
public class ArrayJsonSerializer<T> implements JsonSerializer<Array<T>>, JsonDeserializer<Array<T>> {
	public static final String PRIMITIVE_CONTENT = "@content";

	@Override
	public JsonElement serialize (Array<T> array, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray jsonArray = new JsonArray();
		for (T element : array) {
			JsonElement jsonElement = context.serialize(element);

			if (jsonElement.isJsonObject()) {
				GsonUtils.appendClassProperty(jsonElement.getAsJsonObject(), element, context);
			} else if (jsonElement.isJsonPrimitive()) {
				JsonObject jsonNestedPrimitive = new JsonObject();
				jsonNestedPrimitive.add(PRIMITIVE_CONTENT, jsonElement);
				GsonUtils.appendClassProperty(jsonNestedPrimitive, element, context);
				jsonElement = jsonNestedPrimitive;
			} else if (jsonElement instanceof JsonArray) {
				throw new UnsupportedOperationException("Nested Arrays are not supported by ArrayJsonSerializer");
			}

			jsonArray.add(jsonElement);
		}

		return jsonArray;
	}

	@Override
	public Array<T> deserialize (JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonArray jsonArray = element.getAsJsonArray();
		Array<T> array = new Array<>(jsonArray.size());

		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement.isJsonObject()) {
				JsonObject jsonObjectElement = jsonElement.getAsJsonObject();
				if (jsonObjectElement.has(PRIMITIVE_CONTENT)) {
					array.add(context.deserialize(jsonObjectElement.get(PRIMITIVE_CONTENT), GsonUtils.readClassProperty(jsonObjectElement, context)));
				} else {
					array.add(context.deserialize(jsonObjectElement, GsonUtils.readClassProperty(jsonObjectElement, context)));
				}

				continue;
			}

			throw new UnsupportedOperationException("Invalid JsonElement type in ArrayJsonSerializer");
		}

		return array;
	}
}