/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.VisUI;

/**
 * Compatible with {@link Label}. Does not provide additional features.
 * @author Kotcrab
 * @see Label
 */
public class VisLabel extends Label {
	public VisLabel () {
		super("", VisUI.getSkin());
	}

	public VisLabel (CharSequence text, Color textColor) {
		super(text, VisUI.getSkin());
		setColor(textColor);
	}

	public VisLabel (CharSequence text, int alignment) {
		this(text);
		setAlignment(alignment);
	}

	public VisLabel (CharSequence text) {
		super(text, VisUI.getSkin());
	}

	public VisLabel (CharSequence text, LabelStyle style) {
		super(text, style);
	}

	public VisLabel (CharSequence text, String styleName) {
		super(text, VisUI.getSkin(), styleName);
	}

	public VisLabel (CharSequence text, String fontName, Color color) {
		super(text, VisUI.getSkin(), fontName, color);
	}

	public VisLabel (CharSequence text, String fontName, String colorName) {
		super(text, VisUI.getSkin(), fontName, colorName);
	}

}
