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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.files.FileHandle;

/**
 * Project paths releated utils
 * @author Kotcrab
 */
public class ProjectPathUtils {
	public static boolean isTexture (String relativePath, String ext) {
		return relativePath.startsWith("gfx") && (ext.equals("jpg") || ext.equals("png"));
	}

	public static boolean isTextureAtlas (FileHandle file, String relativePath) {
		return relativePath.startsWith("atlas") && file.extension().equals("atlas") && (FileUtils.siblingExists(file, "png") || FileUtils.siblingExists(file, "jpg"));
	}
}
