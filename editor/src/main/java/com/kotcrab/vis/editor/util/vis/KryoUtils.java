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

package com.kotcrab.vis.editor.util.vis;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.kotcrab.vis.editor.serializer.ArraySerializer;
import com.kotcrab.vis.editor.serializer.ColorSerializer;
import com.kotcrab.vis.editor.serializer.UUIDSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.UUID;

/** @author Kotcrab */
public class KryoUtils {
	public static Kryo getCommonSettingsKryo () {
		Kryo kryo = new Kryo(new VisKryoClassResolver(), new MapReferenceResolver());
		kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
		kryo.setDefaultSerializer(new SettingsSerializerFactory());
		kryo.register(Array.class, new ArraySerializer(), 10);
		kryo.register(UUID.class, new UUIDSerializer(), 12);
		kryo.register(Color.class, new ColorSerializer(), 11);
		kryo.register(Texture.TextureFilter.class, 13);
		return kryo;
	}

	private static class VisKryoClassResolver extends DefaultClassResolver {
		private String OLD_UPDATE_CHANNEL_TYPE = "com.kotcrab.vis.editor.webapi.UpdateChannelType";

		@Override
		protected Class<?> getTypeByName (String className) {

			//UpdateChannelType was moved to another package, needs remap to still support older editor versions
			if (className.startsWith(OLD_UPDATE_CHANNEL_TYPE)) {
				try {
					return Class.forName("com.kotcrab.vis.editor.util.vis.UpdateChannelType" + className.substring(OLD_UPDATE_CHANNEL_TYPE.length()));
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException(e);
				}
			}

			return super.getTypeByName(className);
		}
	}
}
