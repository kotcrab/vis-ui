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

package com.kotcrab.vis.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * VisEditor assets helper class.
 * @author Kotcrab
 */
public class Assets {
	public static final int ICON_SIZE = 22;
	public static final int FOLDER_ROOT_ICON_SIZE = 44;
	public static final int BIG_ICON_SIZE = 76;

	private static TextureAtlas icons;
	private static TextureAtlas misc;

	private static ShaderProgram distanceFieldShader;

	static void load () {
		icons = new TextureAtlas("gfx/icons.atlas");
		misc = new TextureAtlas("gfx/misc.atlas");

		distanceFieldShader = new ShaderProgram(Gdx.files.internal("shader/bmp-font-df.vert"), Gdx.files.internal("shader/bmp-font-df.frag"));
		if (!distanceFieldShader.isCompiled()) {
			Log.fatal("Renderer", "FontShader compilation failed:\n" + distanceFieldShader.getLog());
			throw new IllegalStateException("Shader compilation failed");
		}
	}

	static void dispose () {
		if (icons != null) icons.dispose();
		if (misc != null) misc.dispose();
		if (distanceFieldShader != null) distanceFieldShader.dispose();
		icons = null;
		misc = null;
		distanceFieldShader = null;
	}

	public static ShaderProgram getDistanceFieldShader () {
		return distanceFieldShader;
	}

	public static Drawable getIcon (String name) {
		return new TextureRegionDrawable(getIconRegion(name));
	}

	public static TextureRegion getIconRegion (String name) {
		return icons.findRegion(name);
	}

	public static Drawable getMisc (String name) {
		return new TextureRegionDrawable(getMiscRegion(name));
	}

	public static TextureRegion getMiscRegion (String name) {
		return misc.findRegion(name);
	}
}
