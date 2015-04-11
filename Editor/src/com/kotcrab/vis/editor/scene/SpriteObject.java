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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.api.scene.EditorObject;
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
