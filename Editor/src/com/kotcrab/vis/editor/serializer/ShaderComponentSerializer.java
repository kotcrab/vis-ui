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

package com.kotcrab.vis.editor.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.module.project.ShaderCacheModule;
import com.kotcrab.vis.runtime.component.ShaderComponent;

/**
 * Kryo serializer for {@link ShaderComponent}
 * @author Kotcrab
 */
public class ShaderComponentSerializer extends EntityComponentSerializer<ShaderComponent> {
	private static final int VERSION_CODE = 1;
	private final ShaderCacheModule shaderCache;

	public ShaderComponentSerializer (Kryo kryo, ShaderCacheModule shaderCache) {
		super(kryo, ShaderComponent.class);
		this.shaderCache = shaderCache;
	}

	@Override
	public void write (Kryo kryo, Output output, ShaderComponent shaderComponent) {
		super.write(kryo, output, shaderComponent);

		parentWrite(kryo, output, shaderComponent);

		output.writeInt(VERSION_CODE);
	}

	@Override
	public ShaderComponent read (Kryo kryo, Input input, Class<ShaderComponent> type) {
		super.read(kryo, input, type);

		ShaderComponent component = parentRead(kryo, input, type);

		input.readInt(); //version code

		component.shader = shaderCache.get(component.asset);

		return component;
	}

	@Override
	public ShaderComponent copy (Kryo kryo, ShaderComponent original) {
		super.copy(kryo, original);
		return new ShaderComponent(original.asset, original.shader);
	}
}
