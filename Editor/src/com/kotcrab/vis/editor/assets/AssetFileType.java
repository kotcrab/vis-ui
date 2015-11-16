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

import com.kotcrab.vis.editor.module.project.assetsmanager.FileItem;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;

/**
 * Possible file types in VisEditor project assets
 * @author Kotcrab
 */
public enum AssetFileType {
	UNKNOWN,
	/** Used when {@link FileItem} content is created from external {@link EditorEntitySupport}, for example by plugin. */
	NON_STANDARD,
	TEXTURE, TEXTURE_ATLAS,
	TTF_FONT, BMP_FONT_FILE, BMP_FONT_TEXTURE,
	MUSIC, SOUND, PARTICLE_EFFECT,
	FRAGMENT_SHADER, VERTEX_SHADER,
	SPRITER_SCML,
	SCENE
}
