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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Image that displays alpha grid as background, used by ColorPicker to display selected colors, should not be used outside Picker
 * due to that this Image scales 25px grid image to whatever size it needed. It will look weird for smaller or bigger images.
 * @author Kotcrab
 */
public class AlphaImage extends Image {
	private ColorPickerStyle style;
	private Drawable alphaDrawable;
	private boolean shiftAlpha;

	public AlphaImage (ColorPickerStyle style) {
		super(style.white);
		this.style = style;
		setAlphaDrawable();
	}

	public AlphaImage (ColorPickerStyle style, boolean shiftAlpha) {
		super(style.white);
		this.style = style;
		this.shiftAlpha = shiftAlpha;
		setAlphaDrawable();
	}

	private void setAlphaDrawable () {
		alphaDrawable = shiftAlpha ? style.alphaBar25pxShifted : style.alphaBar25px;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		alphaDrawable.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
		super.draw(batch, parentAlpha);
	}
}
