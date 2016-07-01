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

package com.kotcrab.vis.plugin.spriter;

import com.badlogic.gdx.graphics.Texture;
import com.kotcrab.vis.editor.plugin.PluginFileHandle;
import com.kotcrab.vis.editor.plugin.api.ResourceLoader;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/** @author Kotcrab */
@VisPlugin
public class SpriterAssets implements ResourceLoader {
	public static Texture folderSpriterMedium;

	@Override
	public void load () {
		folderSpriterMedium = new Texture(new PluginFileHandle(SpriterEditorSupport.class, "icons/folder-spriter-medium.png"));
	}

	@Override
	public String getName () {
		return "SpriterPluginAssets";
	}

	@Override
	public void dispose () {
		folderSpriterMedium.dispose();
	}
}
