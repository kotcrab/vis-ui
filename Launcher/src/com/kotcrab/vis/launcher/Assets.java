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

package com.kotcrab.vis.launcher;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
	public static TextureAtlas icons;

	public static void load () {
		icons = new TextureAtlas("icons.atlas");
	}

	public static void dispose () {
		icons.dispose();
	}

	public static Drawable getIcon (Icons icon) {
		return new TextureRegionDrawable(getIconRegion(icon));
	}

	public static TextureRegion getIconRegion (Icons icon) {
		return icons.findRegion(icon.getIconName());
	}
}
