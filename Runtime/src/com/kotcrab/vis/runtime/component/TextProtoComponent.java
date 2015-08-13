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
import com.kotcrab.vis.runtime.system.inflater.TextInflater;

/**
 * {@link ProtoComponent} for {@link TextComponent}
 * @author Kotcrab
 * @see TextInflater
 */
public class TextProtoComponent extends ProtoComponent {
	public float x, y;
	public float originX, originY;
	public float rotation;
	public float scaleX = 1, scaleY = 1;
	public Color tint = Color.WHITE;

	public String text;
	public boolean autoSetOriginToCenter;

	public boolean isUsesDistanceField;

	private TextProtoComponent () {
	}

	public TextProtoComponent (TextComponent component) {
		x = component.getX();
		y = component.getY();

		originX = component.getOriginX();
		originY = component.getOriginY();

		rotation = component.getRotation();

		scaleX = component.getScaleX();
		scaleY = component.getScaleY();

		tint = component.getColor().cpy();

		text = component.getText();
		autoSetOriginToCenter = component.isAutoSetOriginToCenter();

		isUsesDistanceField = component.isDistanceFieldShaderEnabled();
	}
}
