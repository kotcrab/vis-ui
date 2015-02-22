/*
 * Copyright 2014-2015 Pawel Pastuszak
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;

/** Channel bar intended for alpha channel, renders alpha grid bellow channel texture. Not intended to use outside ColorPicker */
public class AlphaChannelBar extends ChannelBar {
	private static Drawable ALPHA_BAR = VisUI.getSkin().getDrawable("alpha-bar-10px");

	public AlphaChannelBar (Texture texture, int value, int maxValue, ChangeListener listener) {
		super(texture, value, maxValue, listener);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		ALPHA_BAR.draw(batch, getX() + getImageX(), getY() + getImageY(), getImageWidth() * getScaleX(), getImageHeight() * getScaleY());
		super.draw(batch, parentAlpha);
	}
}
