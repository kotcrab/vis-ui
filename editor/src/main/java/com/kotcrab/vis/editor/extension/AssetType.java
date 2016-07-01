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

package com.kotcrab.vis.editor.extension;

import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor.AssetDirectoryDescriptorBuilder;
import com.kotcrab.vis.editor.plugin.api.AssetTypeStorage;

/**
 * Built-in possible file types for assets files
 * @author Kotcrab
 */
public class AssetType implements AssetTypeStorage {
	private static final String PREFIX = "com.kotcrab.vis.editor.";
	private static final String FILE_TYPE = PREFIX + "file.";
	private static final String DIRECTORY_TYPE = PREFIX + "directory.";

	public static final String UNKNOWN = "<unknown>";

	/** Special type used for directories */
	public static final String DIRECTORY = PREFIX + "Directory";

	public static final String TEXTURE = FILE_TYPE + "Texture";
	public static final String TEXTURE_ATLAS = FILE_TYPE + "TextureAtlas";
	public static final String TEXTURE_ATLAS_IMAGE = FILE_TYPE + "TextureAtlasImage";
	public static final String TTF_FONT = FILE_TYPE + "TtfFont";
	public static final String BMP_FONT_FILE = FILE_TYPE + "BmpFont";
	public static final String BMP_FONT_TEXTURE = FILE_TYPE + "BmpFontTexture";
	public static final String MUSIC = FILE_TYPE + "Music";
	public static final String SOUND = FILE_TYPE + "Sound";
	public static final String PARTICLE_EFFECT = FILE_TYPE + "ParticleEffect";
	public static final String FRAGMENT_SHADER = FILE_TYPE + "FragmentShader";
	public static final String VERTEX_SHADER = FILE_TYPE + "VertexShader";
	public static final String SCENE = FILE_TYPE + "Scene";

	public static final AssetDirectoryDescriptor DIRECTORY_MUSIC
			= new AssetDirectoryDescriptorBuilder(DIRECTORY_TYPE + "Music", "Music Files", Icons.FOLDER_MUSIC_MEDIUM.drawable())
			.build();

	public static final AssetDirectoryDescriptor DIRECTORY_SOUND
			= new AssetDirectoryDescriptorBuilder(DIRECTORY_TYPE + "Sound", "Sound Files", Icons.FOLDER_SOUND_MEDIUM.drawable())
			.build();
}
