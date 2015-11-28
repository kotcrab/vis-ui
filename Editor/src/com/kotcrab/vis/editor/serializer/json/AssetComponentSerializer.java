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

package com.kotcrab.vis.editor.serializer.json;

import com.google.gson.*;
import com.kotcrab.vis.runtime.component.AssetComponent;

import java.lang.reflect.Type;

/** @author Kotcrab */
public class AssetComponentSerializer implements JsonSerializer<AssetComponent>, JsonDeserializer<AssetComponent> {
	@Override
	public JsonElement serialize (AssetComponent asset, Type typeOfSrc, JsonSerializationContext context) {
		JsonElement jsonAsset = context.serialize(asset.asset, asset.asset.getClass());
		GsonUtils.appendClassProperty(jsonAsset, asset.asset, context);

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("asset", jsonAsset);
		return jsonObject;
	}

	@Override
	public AssetComponent deserialize (JsonElement j, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = j.getAsJsonObject().get("asset").getAsJsonObject();
		AssetComponent asset = new AssetComponent();
		asset.asset = context.deserialize(json, GsonUtils.readClassProperty(json, context));
		return asset;
	}
}
