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

package com.kotcrab.vis.editor.plugin.api;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

import java.lang.reflect.Type;

/** @author Kotcrab */
public interface GsonConfigurator {
	void configure (VisGsonBuilder builder);

	/** Encapsulates {@link GsonBuilder} to only allow plugins for registering type adapters */
	class VisGsonBuilder {
		private GsonBuilder builder;

		public VisGsonBuilder (GsonBuilder builder) {
			this.builder = builder;
		}

		public GsonBuilder registerTypeAdapter (Type type, Object typeAdapter) {
			return builder.registerTypeAdapter(type, typeAdapter);
		}

		public GsonBuilder registerTypeAdapterFactory (TypeAdapterFactory factory) {
			return builder.registerTypeAdapterFactory(factory);
		}
	}
}
