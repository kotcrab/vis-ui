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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.runtime.entity.SpriteEntity;

public class SpriteObject extends SpriteEntity implements EditorObject {
	public SpriteObject (String texturePath, TextureRegion region, float x, float y) {
		super(null, texturePath, new Sprite(region));
		sprite.setPosition(x, y);
	}

	public SpriteObject (SpriteObject other, Sprite sprite) {
		super(other.getId(), other.getAssetPath(), sprite);
	}

	public void onDeserialize (TextureRegion region) {
		this.sprite = new Sprite(region);
	}

	@Override
	public void render (Batch batch) {
		sprite.draw(batch);
	}

	@Override
	public boolean isFlipSupported () {
		return true;
	}

	@Override
	public boolean isResizeSupported () {
		return true;
	}

	@Override
	public boolean isOriginSupported () {
		return true;
	}

	@Override
	public boolean isScaleSupported () {
		return true;
	}

	@Override
	public boolean isTintSupported () {
		return true;
	}

	@Override
	public boolean isRotationSupported () {
		return true;
	}

	public Sprite getSprite () {
		return sprite;
	}
}
