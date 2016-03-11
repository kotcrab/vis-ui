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

package com.kotcrab.vis.editor.assets;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.ShaderAsset;

/*** @author Kotcrab */
public class ShaderDescriptorProvider implements AssetDescriptorProvider<ShaderAsset> {
	@Override
	public ShaderAsset provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (ProjectPathUtils.isVertexShader(file)) {
			String frag = relativePath.substring(0, relativePath.length() - 4) + "frag";
			return new ShaderAsset(relativePath, frag);
		}

		if (ProjectPathUtils.isFragmentShader(file)) {
			String vert = relativePath.substring(0, relativePath.length() - 4) + "vert";
			return new ShaderAsset(vert, relativePath);
		}

		return null;
	}

	@Override
	public ShaderAsset parametrize (ShaderAsset rawAsset, ShaderAsset other) {
		return rawAsset;
	}
}
