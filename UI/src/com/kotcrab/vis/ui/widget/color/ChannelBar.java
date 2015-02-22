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
 * Class used to display channel color bars in color picker, not intended to use outside ColorPicker
 * @author Kotcrab
 */
public class ChannelBar extends VisImage {
	private static final Drawable BAR_SELECTOR = VisUI.getSkin().getDrawable("color-picker-bar-selector");

	private int maxValue;
	private int value;
	private float selectorX;

	public ChannelBar (Texture texture, int value, final int maxValue, ChangeListener listener) {
		super(texture);
		this.maxValue = maxValue;
		setValue(value);
		addListener(listener);

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				updateValueFromTouch(x);
				return true;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				updateValueFromTouch(x);
			}
		});
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		BAR_SELECTOR.draw(batch, getX() + selectorX - BAR_SELECTOR.getMinWidth() / 2, getY() - 2, BAR_SELECTOR.getMinWidth(), BAR_SELECTOR.getMinHeight());
	}

	public void setValue (int newValue) {
		this.value = newValue;
		if (value < 0) value = 0;
		if (value > maxValue) value = maxValue;

		selectorX = ((float) value / maxValue) * ColorPicker.BAR_WIDTH;
	}

	public int getValue () {
		return value;
	}

	private void updateValueFromTouch (float x) {
		int newValue = (int) (x / ColorPicker.BAR_WIDTH * maxValue);
		setValue(newValue);

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}
}

