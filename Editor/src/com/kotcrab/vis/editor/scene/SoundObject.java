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

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.runtime.entity.SoundEntity;

public class SoundObject extends SoundEntity implements EditorObject {
	private float x, y;
	private transient TextureRegion icon;
	private Rectangle bounds;

	public SoundObject (String soundPath, Sound sound) {
		super(null, soundPath, sound);
		this.icon = Assets.getIconRegion(Icons.SOUND);

		bounds = new Rectangle(x, y, icon.getRegionWidth(), icon.getRegionHeight());
	}

	public SoundObject (SoundObject other, Sound newSound) {
		super(other.getId(), other.getAssetPath(), newSound);

		this.x = other.x;
		this.y = other.y;
		this.icon = other.icon;
		this.bounds = new Rectangle();
		calcBounds();
	}

	private void calcBounds () {
		bounds.set(x, y, icon.getRegionWidth(), icon.getRegionHeight());
	}

	public void onDeserialize (Sound sound) {
		this.icon = Assets.getIconRegion(Icons.SOUND);
		this.sound = sound;
	}

	@Override
	public void render (Batch batch) {
		batch.draw(icon, x, y);
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public void setX (float x) {
		this.x = x;
		bounds.setX(x);
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public void setY (float y) {
		this.y = y;
		bounds.setY(y);
	}

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
		bounds.setPosition(x, y);
	}

	@Override
	public float getWidth () {
		return icon.getRegionWidth();
	}

	@Override
	public float getHeight () {
		return icon.getRegionHeight();
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return bounds;
	}
}
