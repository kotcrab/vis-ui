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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.runtime.data.SpriteData;

public class Object2d extends EditorSceneObject {
	public String regionRelativePath;
	public transient Sprite sprite;

	private SpriteData data;

	public Object2d (String regionRelativePath, TextureRegion region, float x, float y) {
		this.sprite = new Sprite(region);
		this.regionRelativePath = regionRelativePath;
		data = new SpriteData();
		sprite.setPosition(x, y);
	}

	public void saveSpriteDataValuesToData () {
		data.saveFrom(sprite);
	}

	public void loadSpriteValuesFromData () {
		data.loadTo(sprite);
	}

	public void updateSpriteRegion (TextureRegion newRegion) {
		saveSpriteDataValuesToData();
		sprite.setRegion(newRegion);
		loadSpriteValuesFromData();
	}
}
