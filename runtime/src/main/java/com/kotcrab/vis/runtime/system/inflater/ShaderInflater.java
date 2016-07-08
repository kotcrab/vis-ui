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

package com.kotcrab.vis.runtime.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.Shader;
import com.kotcrab.vis.runtime.component.proto.ProtoShader;

/**
 * Inflates {@link ProtoShader} into {@link Shader}.
 * @author Kotcrab
 */
public class ShaderInflater extends InflaterSystem {
	private ComponentMapper<Shader> shaderCm;
	private ComponentMapper<ProtoShader> protoCm;

	private AssetManager manager;

	public ShaderInflater (AssetManager manager) {
		super(Aspect.all(ProtoShader.class, AssetReference.class));
		this.manager = manager;
	}

	@Override
	public void inserted (int entityId) {
		ProtoShader protoComponent = protoCm.get(entityId);

		if (protoComponent.asset != null) {
			String shaderPath = protoComponent.asset.getPathWithoutExtension();
			ShaderProgram program = manager.get(shaderPath, ShaderProgram.class);
			if (program == null)
				throw new IllegalStateException("Can't load scene, shader program is missing:" + shaderPath);

			Shader shader = shaderCm.create(entityId);
			shader.asset = protoComponent.asset;
			shader.shader = program;
		}

		protoCm.remove(entityId);
	}
}
