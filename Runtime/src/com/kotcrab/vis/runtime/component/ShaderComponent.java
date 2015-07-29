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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.kotcrab.vis.runtime.assets.ShaderAsset;
import com.kotcrab.vis.runtime.util.UsesProtoComponent;
import com.kotcrab.vis.runtime.util.autotable.SelectFilePropertyUI;

/** @author Kotcrab */
public class ShaderComponent extends Component implements UsesProtoComponent {
	@SelectFilePropertyUI(fieldName = "Shader", relativeFolderPath = "shader/", extension = "frag", hideExtension = true,
			handlerClass = "com.kotcrab.vis.editor.ui.scene.entityproperties.components.ShaderSelectFilePropertyHandler")
	public ShaderAsset asset;
	public transient ShaderProgram shader;

	private ShaderComponent () {
	}

	public ShaderComponent (ShaderAsset asset, ShaderProgram shader) {
		this.asset = asset;
		this.shader = shader;
	}

	@Override
	public ProtoComponent getProtoComponent () {
		return new ShaderProtoComponent(asset);
	}
}
