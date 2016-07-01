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

package com.kotcrab.vis.plugin.spriter.util;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.plugin.api.AssetsFileSorter;
import com.kotcrab.vis.plugin.spriter.assets.SpriterAssetType;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/** @author Kotcrab */
@VisPlugin
public class SpriterAssetsFileSorter implements AssetsFileSorter {
	@Override
	public boolean isSupported (AssetsMetadataModule assetsMetadata, FileHandle file, String assetsFolderRelativePath) {
		return assetsMetadata.isDirectoryMarkedAs(file, SpriterAssetType.DIRECTORY_SPRITER);
	}

	@Override
	public boolean isMainFile (FileHandle file) {
		return file.extension().equals("scml");
	}

	@Override
	public boolean isExportedFile (FileHandle file) {
		return true;
	}
}
