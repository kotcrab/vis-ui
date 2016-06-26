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

import com.badlogic.gdx.utils.IntMap;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map.Entry;

/** @author Kotcrab */
public class IntMapJsonSerializer<T> implements JsonSerializer<IntMap<T>>, JsonDeserializer<IntMap<T>> {
	@Override
	public JsonElement serialize (IntMap<T> intMap, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray jsonArray = new JsonArray();
		for (IntMap.Entry<T> entry : intMap.entries()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add(String.valueOf(entry.key), context.serialize(entry.value, entry.value.getClass()));
			GsonUtils.appendClassProperty(jsonObject, entry.value, context);

			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

	@Override
	public IntMap<T> deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonArray jsonArray = json.getAsJsonArray();

		IntMap<T> intMap = new IntMap<>(jsonArray.size());

		for (JsonElement element : jsonArray) {
			JsonObject object = element.getAsJsonObject();
			Entry<String, JsonElement> entry = object.entrySet().iterator().next();
			int mapKey = Integer.parseInt(entry.getKey());
			Class<?> mapObjectClass = GsonUtils.readClassProperty(object, context);
			intMap.put(mapKey, context.deserialize(entry.getValue(), mapObjectClass));
		}

		return intMap;
	}
}
