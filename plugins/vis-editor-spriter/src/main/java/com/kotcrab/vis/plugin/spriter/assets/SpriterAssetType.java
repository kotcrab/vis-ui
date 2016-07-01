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

import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor.AssetDirectoryDescriptorBuilder;
import com.kotcrab.vis.editor.plugin.api.AssetTypeStorage;
import com.kotcrab.vis.plugin.spriter.SpriterIcons;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/** @author Kotcrab */
@VisPlugin
public class SpriterAssetType implements AssetTypeStorage {
	private static final String PREFIX = "com.kotcrab.vis.editor."; //for backward compatibility this doesn't have plugin
	private static final String FILE_TYPE = PREFIX + "file.";
	private static final String DIRECTORY_TYPE = PREFIX + "directory.";

	public static final String SPRITER_SCML = FILE_TYPE + "SpriterScml";

	public static final AssetDirectoryDescriptor DIRECTORY_SPRITER
			= new AssetDirectoryDescriptorBuilder(DIRECTORY_TYPE + "Spriter", "Spriter Animation", SpriterIcons.FOLDER_SPRITER_MEDIUM::drawable)
			.excludeFromTextureCache()
			.build();
}
