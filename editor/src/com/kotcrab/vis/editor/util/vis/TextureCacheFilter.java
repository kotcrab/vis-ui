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

package com.kotcrab.vis.editor.util.vis;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.util.FileUtils;

import java.io.File;
import java.io.FilenameFilter;

/** @author Kotcrab */
public class TextureCacheFilter implements FilenameFilter {
	private AssetsMetadataModule metadata;

	public TextureCacheFilter (AssetsMetadataModule metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean accept (File dir, String name) {
		FileHandle file = FileUtils.toFileHandle(dir);
		if (ProjectPathUtils.isTextureAtlasImage(file.child(name))) return false;

		AssetDirectoryDescriptor desc = metadata.getAsDirectoryDescriptorRecursively(file);
		if (desc != null && desc.isExcludeFromTextureCache()) return false;

		return true;
	}
}
