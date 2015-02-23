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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class SpriteObject extends EditorEntity {
	public String regionRelativePath;
	public Sprite sprite;

	public SpriteObject (String regionRelativePath, TextureRegion region, float x, float y) {
		this.sprite = new Sprite(region);
		this.regionRelativePath = regionRelativePath;
		sprite.setPosition(x, y);
	}

	@Override
	public float getX () {
		return sprite.getX();
	}

	@Override
	public void setX (float x) {
		sprite.setX(x);
	}

	@Override
	public float getY () {
		return sprite.getY();
	}

	@Override
	public void setY (float y) {
		sprite.setY(y);
	}

	@Override
	public void setPosition (float x, float y) {
		sprite.setPosition(x, y);
	}

	@Override
	public float getWidth () {
		return sprite.getWidth();
	}

	@Override
	public void setWidth (float width) {
		sprite.setSize(width, sprite.getHeight());
	}

	@Override
	public float getHeight () {
		return sprite.getHeight();
	}

	@Override
	public void setSize (float width, float height) {
		sprite.setSize(width, height);
	}

	@Override
	public void setHeight (float height) {
		sprite.setSize(sprite.getWidth(), height);
	}

	@Override
	public float getOriginX () {
		return sprite.getOriginX();
	}

	@Override
	public void setOriginX (float x) {
		sprite.setOrigin(x, sprite.getOriginY());
	}

	@Override
	public float getOriginY () {
		return sprite.getOriginY();
	}

	@Override
	public void setOriginY (float y) {
		sprite.setOrigin(sprite.getOriginX(), y);
	}

	@Override
	public void setOrigin (float x, float y) {
		sprite.setOrigin(x, y);
	}

	@Override
	public float getScaleX () {
		return sprite.getScaleX();
	}

	@Override
	public void setScaleX (float x) {
		sprite.setScale(x, sprite.getScaleY());
	}

	@Override
	public float getScaleY () {
		return sprite.getScaleY();
	}

	@Override
	public void setScaleY (float y) {
		sprite.setScale(sprite.getScaleX(), y);
	}

	@Override
	public void setScale (float x, float y) {
		sprite.setScale(x, y);
	}

	@Override
	public Color getColor () {
		return sprite.getColor();
	}

	@Override
	public void setColor (Color color) {
		sprite.setColor(color);
	}

	@Override
	public float getRotation () {
		return sprite.getRotation();
	}

	@Override
	public void setRotation (float rotation) {
		sprite.setRotation(rotation);
	}

	@Override
	public boolean isFlipX () {
		return sprite.isFlipX();
	}

	@Override
	public void setFlipX (boolean x) {
		sprite.setFlip(x, sprite.isFlipY());
	}

	@Override
	public boolean isFlipY () {
		return sprite.isFlipY();
	}

	@Override
	public void setFlipY (boolean y) {
		sprite.setFlip(sprite.isFlipX(), y);
	}

	@Override
	public void setFlip (boolean x, boolean y) {
		sprite.setFlip(x, y);
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return sprite.getBoundingRectangle();
	}
}
