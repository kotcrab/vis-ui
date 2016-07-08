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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.assets.ShaderAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.proto.ProtoComponent;
import com.kotcrab.vis.runtime.component.proto.ProtoShader;
import com.kotcrab.vis.runtime.properties.StoresAssetDescriptor;
import com.kotcrab.vis.runtime.properties.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFile;

/**
 * Stores single shader along with it's asset descriptor.
 * @author Kotcrab
 */
public class Shader extends Component implements UsesProtoComponent, StoresAssetDescriptor {
	@ATSelectFile(fieldName = "Shader", extension = "frag", hideExtension = true, handlerAlias = "shader")
	public ShaderAsset asset;
	public transient ShaderProgram shader;

	public Shader () {
	}

	public Shader (ShaderAsset asset, ShaderProgram shader) {
		this.asset = asset;
		this.shader = shader;
	}

	@Override
	public ProtoComponent toProtoComponent () {
		return new ProtoShader(asset);
	}

	@Override
	public VisAssetDescriptor getAsset () {
		return asset;
	}
}
