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

package com.kotcrab.vis.runtime.component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.kotcrab.vis.runtime.properties.*;
import com.kotcrab.vis.runtime.system.inflater.SpriteInflater;

/**
 * {@link ProtoComponent} for {@link SpriteComponent}
 * @author Kotcrab
 * @see SpriteInflater
 */
public class SpriteProtoComponent extends ProtoComponent implements PositionOwner, SizeOwner, OriginOwner, RotationOwner,
		ScaleOwner, TintOwner, FlipOwner, Resizeable {
	public float x, y;
	public float width, height;
	public float originX, originY;
	public float rotation;
	public float scaleX = 1, scaleY = 1;
	public Color tint = Color.WHITE;
	public boolean flipX, flipY;

	public SpriteProtoComponent () {
	}

	public SpriteProtoComponent (SpriteComponent component) {
		Sprite sprite = component.sprite;
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

	public void fill (SpriteComponent component) {
		component.setPosition(x, y);
		component.setSize(width, height);
		component.setOrigin(originX, originY);
		component.setRotation(rotation);
		component.setScale(scaleX, scaleY);
		component.setTint(tint);
		component.setFlip(flipX, flipY);
	}

	@Override
	public float getX () {
		return x;
	}

	@Override
	public void setX (float x) {
		this.x = x;
	}

	@Override
	public float getY () {
		return y;
	}

	@Override
	public void setY (float y) {
		this.y = y;
	}

	@Override
	public void setPosition (float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public float getWidth () {
		return width;
	}

	@Override
	public float getHeight () {
		return height;
	}

	@Override
	public void setSize (float width, float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public float getOriginX () {
		return originX;
	}

	@Override
	public float getOriginY () {
		return originY;
	}

	@Override
	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
	}

	@Override
	public float getRotation () {
		return rotation;
	}

	@Override
	public void setRotation (float rotation) {
		this.rotation = rotation;
	}

	@Override
	public float getScaleX () {
		return scaleX;
	}

	@Override
	public float getScaleY () {
		return scaleY;
	}

	@Override
	public void setScale (float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public boolean isFlipX () {
		return flipX;
	}

	@Override
	public boolean isFlipY () {
		return flipY;
	}

	@Override
	public void setFlip (boolean flipX, boolean flipY) {
		this.flipX = flipX;
		this.flipY = flipY;
	}

	@Override
	public Color getTint () {
		return tint;
	}

	@Override
	public void setTint (Color tint) {
		this.tint = tint;
	}
}
