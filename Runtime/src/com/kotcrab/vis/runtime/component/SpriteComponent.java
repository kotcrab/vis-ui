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

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.kotcrab.vis.runtime.entity.accessor.*;
import com.kotcrab.vis.runtime.util.UsesProtoComponent;

public class SpriteComponent extends Component implements BasicPropertiesAccessor, SizePropertiesAccessor, OriginPropertiesAccessor,
		ScalePropertiesAccessor, ColorPropertiesAccessor, RotationPropertiesAccessor, FlipPropertiesAccessor, UsesProtoComponent {
	public Sprite sprite;

	public SpriteComponent (Sprite sprite) {
		this.sprite = sprite;
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
	public float getHeight () {
		return sprite.getHeight();
	}

	@Override
	public Rectangle getBoundingRectangle () {
		return sprite.getBoundingRectangle();
	}

	@Override
	public void setSize (float width, float height) {
		sprite.setSize(width, height);
	}

	@Override
	public float getOriginX () {
		return sprite.getOriginX();
	}

	@Override
	public float getOriginY () {
		return sprite.getOriginY();
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
	public float getScaleY () {
		return sprite.getScaleY();
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
	public boolean isFlipY () {
		return sprite.isFlipY();
	}

	@Override
	public void setFlip (boolean x, boolean y) {
		sprite.setFlip(x, y);
	}

	@Override
	public ProtoComponent getProtoComponent () {
		return new SpriteProtoComponent(this);
	}
}
