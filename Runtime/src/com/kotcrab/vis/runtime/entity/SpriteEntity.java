/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.runtime.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class SpriteEntity extends Entity {
	protected transient Sprite sprite;

	public SpriteEntity (String id, Sprite sprite) {
		super(id);
		this.sprite = sprite;
	}

	@Override
	public void render (Batch batch) {
		sprite.draw(batch);
	}

	public float getX () {
		return sprite.getX();
	}

	public void setX (float x) {
		sprite.setX(x);
	}

	public float getY () {
		return sprite.getY();
	}

	public void setY (float y) {
		sprite.setY(y);
	}

	public void setPosition (float x, float y) {
		sprite.setPosition(x, y);
	}

	public float getWidth () {
		return sprite.getWidth();
	}

	public float getHeight () {
		return sprite.getHeight();
	}

	public void setSize (float width, float height) {
		sprite.setSize(width, height);
	}

	public float getOriginX () {
		return sprite.getOriginX();
	}

	public float getOriginY () {
		return sprite.getOriginY();
	}

	public void setOrigin (float x, float y) {
		sprite.setOrigin(x, y);
	}

	public float getScaleX () {
		return sprite.getScaleX();
	}

	public float getScaleY () {
		return sprite.getScaleY();
	}

	public void setScale (float x, float y) {
		sprite.setScale(x, y);
	}

	public Color getColor () {
		return sprite.getColor();
	}

	public void setColor (Color color) {
		sprite.setColor(color);
	}

	public float getRotation () {
		return sprite.getRotation();
	}

	public void setRotation (float rotation) {
		sprite.setRotation(rotation);
	}

	public boolean isFlipX () {
		return sprite.isFlipX();
	}

	public boolean isFlipY () {
		return sprite.isFlipY();
	}

	public void setFlip (boolean x, boolean y) {
		sprite.setFlip(x, y);
	}

	public Rectangle getBoundingRectangle () {
		return sprite.getBoundingRectangle();
	}
}
