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
import com.kotcrab.vis.editor.extension.AssetType;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;
import com.kotcrab.vis.editor.util.FileUtils;

/**
 * Project paths and assets file types related utils
 * @author Kotcrab
 */
public class ProjectPathUtils {
	public static boolean isTexture (FileHandle file) {
		String ext = file.extension();
		return (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("png"));
	}

	public static boolean isTextureAtlas (FileHandle file) {
		return file.extension().equals("atlas")
				&& (FileUtils.siblingExists(file, "png") || FileUtils.siblingExists(file, "jpg") ||
				FileUtils.siblingExists(file, "jpeg"));
	}

	public static boolean isTextureAtlasImage (FileHandle file) {
		return isTexture(file) && FileUtils.siblingExists(file, "atlas");
	}

	public static boolean isParticle (FileHandle file) {
		return file.extension().equals("p");
	}

	public static boolean isScene (FileHandle file) {
		return file.extension().equals("scene");
	}

	public static boolean isTrueTypeFont (FileHandle file) {
		return file.extension().equals("ttf");
	}

	public static boolean isBitmapFont (FileHandle file) {
		return file.extension().equals("fnt") && FileUtils.sibling(file, "png").exists();
	}

	public static boolean isFragmentShader (FileHandle file) {
		return file.extension().equals("frag");
	}

	public static boolean isVertexShader (FileHandle file) {
		return file.extension().equals("vert");
	}

	public static boolean isBitmapFontTexture (FileHandle file) {
		return file.extension().equals("png") && FileUtils.sibling(file, "fnt").exists();
	}

	public static boolean isAudioFile (FileHandle file) {
		String ext = file.extension();
		return ext.equals("wav") || ext.equals("ogg") || ext.equals("mp3");
	}

	public static boolean isMusicFile (AssetsMetadataModule metadata, FileHandle file) {
		return metadata.isDirectoryMarkedAs(file, AssetType.DIRECTORY_MUSIC) && isAudioFile(file);
	}

	public static boolean isSoundFile (AssetsMetadataModule metadata, FileHandle file) {
		return metadata.isDirectoryMarkedAs(file, AssetType.DIRECTORY_SOUND) && isAudioFile(file);
	}
}
