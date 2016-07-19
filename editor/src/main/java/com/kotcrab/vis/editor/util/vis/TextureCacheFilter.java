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
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.SimpleImageInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/** @author Kotcrab */
public class TextureCacheFilter implements FilenameFilter {
	private AssetsMetadataModule metadata;
	private int maxTextureSize;

	public TextureCacheFilter (AssetsMetadataModule metadata, int maxTextureSize) {
		this.metadata = metadata;
		this.maxTextureSize = maxTextureSize;
	}

	@Override
	public boolean accept (File baseDir, String name) {
		FileHandle dir = FileUtils.toFileHandle(baseDir);
		FileHandle file = dir.child(name);
		if (file.isDirectory()) return true;
		if (ProjectPathUtils.isTexture(file) == false) return false;
		try {
			SimpleImageInfo imgInfo = new SimpleImageInfo(file.file());
			if (imgInfo.getWidth() > maxTextureSize || imgInfo.getHeight() > maxTextureSize) return false;
		} catch (IOException e) {
			Log.exception(e);
		} catch (EditorException e) {
			Log.warn("Unsupported image file type: " + file);
		}
		if (ProjectPathUtils.isTextureAtlasImage(file)) return false;

		AssetDirectoryDescriptor desc = metadata.getAsDirectoryDescriptorRecursively(dir);
		if (desc != null && desc.isExcludeFromTextureCache()) return false;

		return true;
	}
}
