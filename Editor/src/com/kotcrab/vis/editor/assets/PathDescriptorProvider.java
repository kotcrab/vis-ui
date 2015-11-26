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
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.runtime.assets.PathAsset;

/**
 * Basic descriptor provider for {@link PathAsset}, supporting TrueType fonts, music, sound
 * @author Kotcrab
 */
public class PathDescriptorProvider implements AssetDescriptorProvider<PathAsset> {
	@Override
	public PathAsset provide (AssetsMetadataModule metadata, FileHandle file, String relativePath) {
		if (checkIfSupported(file, relativePath) == false) return null;
		return new PathAsset(relativePath);
	}

	private boolean checkIfSupported (FileHandle file, String relativePath) {
		if (relativePath.startsWith("music") || relativePath.startsWith("sound")) return true;
		return false;
	}

	@Override
	public PathAsset parametrize (PathAsset rawAsset, PathAsset other) {
		return rawAsset;
	} //path is not parametrized
}
