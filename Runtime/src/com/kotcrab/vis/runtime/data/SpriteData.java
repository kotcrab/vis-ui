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

package com.kotcrab.vis.runtime.data;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.entity.SpriteEntity;

public class SpriteData extends EntityData<SpriteEntity> {
	public String textureAtlas;
	public String texturePath;

	public float x, y;
	public float width, height;
	public float originX, originY;
	public float rotation;
	public float scaleX = 1, scaleY = 1;
	public Color tint = Color.WHITE;
	public boolean flipX, flipY;

	@Override
	public void saveFrom (SpriteEntity sprite, VisAssetDescriptor assetDescriptor) {
		super.saveFrom(sprite, assetDescriptor);
		x = sprite.getX();
		y = sprite.getY();

		width = sprite.getWidth();
		height = sprite.getHeight();

		originX = sprite.getOriginX();
		originY = sprite.getOriginY();

		rotation = sprite.getRotation();

		scaleX = sprite.getScaleX();
		scaleY = sprite.getScaleY();

		tint = sprite.getColor().cpy();

		flipX = sprite.isFlipX();
		flipY = sprite.isFlipY();
	}

	@Override
	public void loadTo (SpriteEntity sprite) {
		sprite.setPosition(x, y);
		sprite.setSize(width, height);
		sprite.setOrigin(originX, originY);
		sprite.setRotation(rotation);
		sprite.setScale(scaleX, scaleY);
		sprite.setColor(tint);
		sprite.setFlip(flipX, flipY);
	}
}
