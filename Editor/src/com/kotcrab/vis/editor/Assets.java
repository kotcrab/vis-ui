/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
	public static TextureAtlas icons;
	public static TextureAtlas misc;

	public static void load () {
		icons = new TextureAtlas("icons.atlas");
		misc = new TextureAtlas("misc.atlas");
	}

	public static void dispose () {
		icons.dispose();
		misc.dispose();
	}

	public static Drawable getIcon (Icons icon) {
		return new TextureRegionDrawable(getIconRegion(icon));
	}

	public static Drawable getMisc (String name) {
		return new TextureRegionDrawable(misc.findRegion(name));
	}

	public static TextureRegion getIconRegion (Icons icon) {
		return icons.findRegion(icon.getIconName());
	}
}
