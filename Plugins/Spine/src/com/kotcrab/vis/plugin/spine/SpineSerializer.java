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

package com.kotcrab.vis.plugin.spine;

import com.badlogic.gdx.graphics.Color;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

public class SpineSerializer extends CompatibleFieldSerializer<SpineObject> {
	private SpineCacheModule spineCache;

	public SpineSerializer (Kryo kryo, SpineCacheModule spineCache) {
		super(kryo, SpineObject.class);
		this.spineCache = spineCache;
	}

	@Override
	public void write (Kryo kryo, Output output, SpineObject object) {
		super.write(kryo, output, object);
		output.writeFloat(object.getX());
		output.writeFloat(object.getY());

		output.writeBoolean(object.isFlipX());
		output.writeBoolean(object.isFlipY());

		kryo.writeObject(output, object.getColor());
	}

	@Override
	public SpineObject read (Kryo kryo, Input input, Class<SpineObject> type) {
		SpineObject object = super.read(kryo, input, type);
		object.onDeserialize(spineCache.get(object.getAtlasPath(), object.getAssetPath()));
		object.setPosition(input.readFloat(), input.readFloat());
		object.setFlip(input.readBoolean(), input.readBoolean());
		object.setColor(kryo.readObject(input, Color.class));
		return object;
	}

	@Override
	public SpineObject copy (Kryo kryo, SpineObject original) {
		return new SpineObject(original);
	}
}

