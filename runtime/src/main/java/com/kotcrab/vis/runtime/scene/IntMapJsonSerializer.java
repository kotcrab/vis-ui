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

package com.kotcrab.vis.runtime.scene;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Json serializer for {@link IntMap}.
 * @author Kotcrab
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IntMapJsonSerializer implements Json.Serializer<IntMap> {
	@Override
	public void write (Json json, IntMap object, Class knownType) {
		json.writeObjectStart();

		for (IntMap.Entry entry : (IntMap.Entries<?>) object.entries()) {
			json.writeValue(String.valueOf(entry.key), entry.value, null);
		}

		json.writeObjectEnd();
	}

	@Override
	public IntMap read (Json json, JsonValue jsonData, Class type) {
		IntMap intMap = new IntMap();

		for (JsonValue entry = jsonData.child; entry != null; entry = entry.next) {
			intMap.put(Integer.parseInt(entry.name), json.readValue(entry.name, null, jsonData));
		}

		return intMap;
	}
}
