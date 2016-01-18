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
public class ClassJsonSerializer implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
	private ObjectMap<Class<?>, String> classTagMap = new ObjectMap<>();
	private ObjectMap<String, Class<?>> tagClassMap = new ObjectMap<>();

	private ClassLoader classLoader;

	public ClassJsonSerializer (ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public ClassJsonSerializer registerTag (String tag, Class<?> clazz) {
		classTagMap.put(clazz, tag);
		tagClassMap.put(tag, clazz);
		return this;
	}

	@Override
	public JsonElement serialize (Class<?> clazz, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(getTagClassName(clazz));
	}

	@Override
	public Class<?> deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		try {
			return getFullClassName(json.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

	private String getTagClassName (Class<?> clazz) {
		String shortName = classTagMap.get(clazz);
		if (shortName != null)
			return shortName;
		else
			return clazz.getName();
	}

	private Class<?> getFullClassName (String name) throws ClassNotFoundException {
		Class<?> clazz = tagClassMap.get(name);

		if (clazz != null)
			return clazz;
		else
			return Class.forName(name, false, classLoader);
	}
}
