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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.kotcrab.vis.editor.module.project.ShaderCacheModule;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.component.ShaderComponent;
import com.kotcrab.vis.runtime.component.ShaderProtoComponent;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorShaderInflater extends InflaterSystem {
	private ComponentMapper<ShaderComponent> shaderCm;
	private ComponentMapper<ShaderProtoComponent> protoCm;
	private ShaderCacheModule shaderCache;

	public EditorShaderInflater () {
		super(Aspect.all(ShaderProtoComponent.class, AssetComponent.class));
	}

	@Override
	public void inserted (int entityId) {
		ShaderProtoComponent protoComponent = protoCm.get(entityId);

		ShaderComponent shaderComponent = shaderCm.create(entityId);
		shaderComponent.asset = protoComponent.asset;
		if (shaderComponent.asset != null) {
			shaderComponent.shader = shaderCache.get(shaderComponent.asset);
		}

		protoCm.remove(entityId);
	}
}
