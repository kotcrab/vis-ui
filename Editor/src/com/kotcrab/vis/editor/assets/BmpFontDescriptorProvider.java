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
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/**
 * Descriptor provider for bitmap fonts
 * @author Kotcrab
 */
public class BmpFontDescriptorProvider implements AssetDescriptorProvider {
	@Override
	public VisAssetDescriptor provide (FileHandle file, String relativePath) {
		if (relativePath.startsWith("bmpfont") == false) return null;

		if (relativePath.endsWith("fnt"))
			return new PathAsset(relativePath);
		else if (relativePath.endsWith("png"))
			return new PathAsset(FileUtils.replaceExtension(relativePath, "fnt"));
		else
			return null;
	}
}
