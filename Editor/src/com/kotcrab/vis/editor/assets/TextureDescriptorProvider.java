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

package com.kotcrab.vis.editor.assets;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/**
 * Descriptor provider for texture type assets, supports both gfx textures and atlases
 * @author Kotcrab
 */
public class TextureDescriptorProvider implements AssetDescriptorProvider {
	@Override
	public VisAssetDescriptor provide (FileHandle file, String relativePath) {
		if (relativePath.startsWith("gfx")) return new TextureRegionAsset(relativePath);
		if (relativePath.startsWith("atlas"))
			return new AtlasRegionAsset(relativePath, null); //usage analyzer ignores region name

		return null;
	}
}
