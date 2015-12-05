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

package com.kotcrab.vis.runtime.component.proto;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.runtime.component.TextComponent;
import com.kotcrab.vis.runtime.properties.*;
import com.kotcrab.vis.runtime.system.inflater.TextInflater;

/**
 * {@link ProtoComponent} for {@link TextComponent}
 * @author Kotcrab
 * @see TextInflater
 */
public class TextProtoComponent extends ProtoComponent<TextComponent> implements PositionOwner, OriginOwner, RotationOwner, ScaleOwner, TintOwner {
	public float x, y;
	public float originX, originY;
	public float rotation;
	public float scaleX = 1, scaleY = 1;
	public Color tint = Color.WHITE;

	public String text;
	public boolean autoSetOriginToCenter;

	public boolean isUsesDistanceField;

	public TextProtoComponent () {
	}

	public TextProtoComponent (TextComponent component) {
		x = component.getX();
		y = component.getY();

		originX = component.getOriginX();
		originY = component.getOriginY();

		rotation = component.getRotation();

		scaleX = component.getScaleX();
		scaleY = component.getScaleY();

		tint = component.getTint().cpy();

		text = component.getText();
		autoSetOriginToCenter = component.isAutoSetOriginToCenter();

		isUsesDistanceField = component.isDistanceFieldShaderEnabled();
	}

	@Override
	public void fill (TextComponent component) {
		component.setPosition(x, y);
		component.setOrigin(originX, originY);
		component.setRotation(rotation);
		component.setScale(scaleX, scaleY);

		component.setText(text, tint);

		component.setAutoSetOriginToCenter(autoSetOriginToCenter);

		component.setDistanceFieldShaderEnabled(isUsesDistanceField);
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
	public float getOriginX () {
		return originX;
	}

	@Override
	public float getOriginY () {
		return originY;
	}

	@Override
	public void setOrigin (float x, float y) {
		this.originX = x;
		this.originY = y;
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
	public void setScale (float x, float y) {
		this.scaleX = x;
		this.scaleY = y;
	}

	public void setScaleY (float scaleY) {
		this.scaleY = scaleY;
	}

	@Override
	public Color getTint () {
		return tint;
	}

	@Override
	public void setTint (Color tint) {
		this.tint = this.tint;
	}
}
