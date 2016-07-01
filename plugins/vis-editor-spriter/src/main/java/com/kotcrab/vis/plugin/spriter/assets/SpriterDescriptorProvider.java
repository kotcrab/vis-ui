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

package com.kotcrab.vis.plugin.spriter.assets;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.util.SpriterProjectPathUtils;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/**
 * Descriptor provider for spriter assets
 * @author Kotcrab
 */
@VisPlugin
public class SpriterDescriptorProvider implements AssetDescriptorProvider<SpriterAsset> {
	@Override
	public SpriterAsset provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (SpriterProjectPathUtils.isImportedSpriterAnimationDir(metadata, file))
			return new SpriterAsset(relativePath, -1);
		else
			return null;

	}

	@Override
	public SpriterAsset parametrize (SpriterAsset rawAsset, SpriterAsset other) {
		return new SpriterAsset(rawAsset.getPath(), other.getImageScale());
	}
}
