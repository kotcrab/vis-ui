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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.editor.api.Assets;
import com.kotcrab.vis.editor.api.Icons;
import com.kotcrab.vis.editor.api.scene.EditorObject;
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
