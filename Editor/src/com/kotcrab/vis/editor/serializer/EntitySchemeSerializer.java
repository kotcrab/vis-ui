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
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.module.project.SceneIOModule;

/** @author Kotcrab */
public class EntitySchemeSerializer extends CompatibleFieldSerializer<EntityScheme> {
	private SceneIOModule sceneIO;

	public EntitySchemeSerializer (Kryo kryo, SceneIOModule sceneIO) {
		super(kryo, EntityScheme.class);
		this.sceneIO = sceneIO;
	}

	@Override
	public void write (Kryo kryo, Output output, EntityScheme scheme) {
		sceneIO.setEngineSerializationContext(scheme.components);
		super.write(kryo, output, scheme);
		sceneIO.setEngineSerializationContext(null);
	}

	@Override
	public EntityScheme read (Kryo kryo, Input input, Class<EntityScheme> type) {
		return super.read(kryo, input, type);
	}
}
