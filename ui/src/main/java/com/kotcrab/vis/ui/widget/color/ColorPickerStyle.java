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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;

/** @author Kotcrab */
public class ColorPickerStyle extends WindowStyle {
	public ColorPickerWidgetStyle pickerStyle;

	public ColorPickerStyle () {
	}

	public ColorPickerStyle (ColorPickerStyle style) {
		super(style);
		if (style.pickerStyle != null) this.pickerStyle = new ColorPickerWidgetStyle(style.pickerStyle);
	}
}
