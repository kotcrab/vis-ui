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

import com.badlogic.gdx.utils.ObjectMap;
import com.google.gson.*;

import java.lang.reflect.Type;

/** @author Kotcrab */
public class ObjectMapJsonSerializer<K, V> implements JsonSerializer<ObjectMap<K, V>>, JsonDeserializer<ObjectMap<K, V>> {
	private static final String PROPERTY_CLASS_KEY = "@class-key";
	private static final String PROPERTY_CLASS_VALUE = "@class-value";

	@Override
	public JsonElement serialize (ObjectMap<K, V> objMap, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray jsonArray = new JsonArray();
		for (ObjectMap.Entry<K, V> entry : objMap.entries()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("key", context.serialize(entry.key, entry.key.getClass()));
			jsonObject.add("value", context.serialize(entry.value, entry.value.getClass()));
			GsonUtils.appendClassProperty(jsonObject, entry.key, context, PROPERTY_CLASS_KEY);
			GsonUtils.appendClassProperty(jsonObject, entry.value, context, PROPERTY_CLASS_VALUE);

			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

	@Override
	public ObjectMap<K, V> deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonArray jsonArray = json.getAsJsonArray();

		ObjectMap<K, V> objMap = new ObjectMap<>(jsonArray.size());

		for (JsonElement element : jsonArray) {
			JsonObject object = element.getAsJsonObject();

			K key = context.deserialize(object.get("key"), GsonUtils.readClassProperty(object, context, PROPERTY_CLASS_KEY));
			V value = context.deserialize(object.get("value"), GsonUtils.readClassProperty(object, context, PROPERTY_CLASS_VALUE));
			objMap.put(key, value);
		}

		return objMap;
	}
}
