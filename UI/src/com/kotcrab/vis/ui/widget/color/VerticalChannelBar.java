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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;

/**
 * Vertical channel bar used to display vertical hue bar, not intended to use outside ColorPicker
 * @author Kotcrab
 */
public class VerticalChannelBar extends VisImage {
	private static final Drawable BAR_SELECTOR = VisUI.getSkin().getDrawable("color-picker-selector-vertical");

	private int maxValue;
	private float selectorY;
	private int value;

	public VerticalChannelBar (Texture texture, int value, final int maxValue, ChangeListener listener) {
		super(texture);
		this.maxValue = maxValue;
		setValue(value);
		addListener(listener);

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				updateValueFromTouch(y);
				return true;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				updateValueFromTouch(y);
			}
		});
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		BAR_SELECTOR.draw(batch, getX(), getY() + getImageY() + selectorY - 2.5f, getImageWidth(), BAR_SELECTOR.getMinHeight());
	}

	public void setValue (int newValue) {
		value = newValue;
		if (value < 0) value = 0;
		if (value > maxValue) value = maxValue;

		selectorY = ((float) value / maxValue) * ColorPicker.PALETTE_SIZE;
	}

	private void updateValueFromTouch (float y) {
		int newValue = (int) (y / ColorPicker.PALETTE_SIZE * maxValue);
		setValue(newValue);

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	public int getValue () {
		return value;
	}
}
