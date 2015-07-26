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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.Sizes;

/**
 * Channel bar intended for alpha channel, renders alpha grid bellow channel texture. Not intended to use outside ColorPicker
 * @author Kotcrab
 */
public class AlphaChannelBar extends ChannelBar {
	public AlphaChannelBar (ColorPickerStyle style, Sizes sizes, Texture texture, int value, int maxValue, ChangeListener listener) {
		super(style, sizes, texture, value, maxValue, listener);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		style.alphaBar10px.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
		super.draw(batch, parentAlpha);
	}
}
