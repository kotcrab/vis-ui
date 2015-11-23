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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;

/**
 * A separator that can be used in menus, tables or other widgets, typically add to new row with fillX() and expandX().
 * {@link PopupMenu} and {@link VisTable} provides addSeparator() that automatically adds new separator.
 * @author Kotcrab
 * @since 0.1.0
 */
public class Separator extends Widget {
	private SeparatorStyle style;

	/** New separator with default style */
	public Separator () {
		style = VisUI.getSkin().get(SeparatorStyle.class);
	}

	public Separator (boolean vertical) {
		style = VisUI.getSkin().get(vertical ? "vertical" : "default", SeparatorStyle.class);
	}

	public Separator (String styleName) {
		style = VisUI.getSkin().get(styleName, SeparatorStyle.class);
	}

	public Separator (SeparatorStyle style) {
		this.style = style;
	}

	@Override
	public float getPrefHeight () {
		return style.thickness;
	}

	@Override
	public float getPrefWidth () {
		return style.thickness;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Color c = getColor();
		batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
		style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public SeparatorStyle getStyle () {
		return style;
	}

	static public class SeparatorStyle {
		public Drawable background;
		public int thickness;
		public boolean vertical;

		public SeparatorStyle () {
		}

		public SeparatorStyle (Drawable bg, int thickness) {
			this(bg, thickness, false);
		}

		public SeparatorStyle (Drawable bg, int thickness, boolean vertical) {
			this.background = bg;
			this.thickness = thickness;
			this.vertical = vertical;
		}
	}
}
