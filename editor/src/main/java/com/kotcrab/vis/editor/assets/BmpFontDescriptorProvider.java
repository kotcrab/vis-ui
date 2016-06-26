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
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;

/**
 * Descriptor provider for bitmap fonts
 * @author Kotcrab
 */
public class BmpFontDescriptorProvider implements AssetDescriptorProvider<BmpFontAsset> {
	@Override
	public BmpFontAsset provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (ProjectPathUtils.isBitmapFont(file) == false && ProjectPathUtils.isBitmapFontTexture(file) == false)
			return null;

		if (relativePath.endsWith("fnt"))
			return new BmpFontAsset(relativePath, null);
		else if (relativePath.endsWith("png"))
			return new BmpFontAsset(FileUtils.replaceExtension(relativePath, "fnt"), null);
		else
			return null;
	}

	@Override
	public BmpFontAsset parametrize (BmpFontAsset rawAsset, BmpFontAsset other) {
		return new BmpFontAsset(rawAsset.getPath(), other.getFontParameter());
	}
}
