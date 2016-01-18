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
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;

/**
 * Descriptor provider for texture type assets, supports both gfx textures and atlases
 * @author Kotcrab
 */
public class TextureRegionDescriptorProvider implements AssetDescriptorProvider<TextureRegionAsset> {
	@Override
	public TextureRegionAsset provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (ProjectPathUtils.isTextureAtlasImage(file)) return null;
		if (ProjectPathUtils.isTexture(file)) return new TextureRegionAsset(relativePath);

		return null;
	}

	@Override
	public TextureRegionAsset parametrize (TextureRegionAsset rawAsset, TextureRegionAsset other) {
		return rawAsset; //texture region is not parametrized
	}
}
