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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

/** @author Kotcrab */
public class GsonUtils {
	public static void appendClassProperty (JsonElement json, Object object, JsonSerializationContext context) {
		appendClassProperty(json, object, context, "@class");
	}

	public static void appendClassProperty (JsonElement json, Object object, JsonSerializationContext context, String customMemberName) {
		json.getAsJsonObject().add(customMemberName, context.serialize(object.getClass()));
	}

	public static Class<?> readClassProperty (JsonElement json, JsonDeserializationContext context) {
		return readClassProperty(json, context, "@class");
	}

	public static Class<?> readClassProperty (JsonElement json, JsonDeserializationContext context, String customMemberName) {
		return context.deserialize(json.getAsJsonObject().get(customMemberName), Class.class);
	}
}
