/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;

/**
 * @author Kotcrab
 * @since 1.1.4
 */
public class BusyBar extends Widget {
	private BusyBarStyle style;

	private float segmentX;

	public BusyBar () {
		style = VisUI.getSkin().get(BusyBarStyle.class);
	}

	public BusyBar (String styleName) {
		style = VisUI.getSkin().get(styleName, BusyBarStyle.class);
	}

	public BusyBar (BusyBarStyle style) {
		this.style = style;
	}

	@Override
	public float getPrefHeight () {
		return style.height;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Color c = getColor();
		batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
		segmentX += getSegmentDeltaX();
		style.segment.draw(batch, getX() + segmentX, getY(), style.segmentWidth, style.height);
		if (segmentX > getWidth() + style.segmentOverflow) {
			resetSegment();
		}
		if (isVisible()) Gdx.graphics.requestRendering();
	}

	public void resetSegment () {
		segmentX = -style.segmentWidth - style.segmentOverflow;
	}

	protected float getSegmentDeltaX () {
		return Gdx.graphics.getDeltaTime() * getWidth();
	}

	public BusyBarStyle getStyle () {
		return style;
	}

	static public class BusyBarStyle {
		public Drawable segment;
		public int segmentOverflow;
		public int segmentWidth;
		public int height;

		public BusyBarStyle () {
		}

		public BusyBarStyle (Drawable segment, int segmentOverflow, int segmentWidth, int height) {
			this.segment = segment;
			this.segmentOverflow = segmentOverflow;
			this.segmentWidth = segmentWidth;
			this.height = height;
		}
	}
}
