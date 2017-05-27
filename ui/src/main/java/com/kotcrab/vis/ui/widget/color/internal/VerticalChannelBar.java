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

package com.kotcrab.vis.ui.widget.color.internal;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle;

/**
 * Vertical channel bar is used to display vertical hue bar
 * @author Kotcrab
 */
public class VerticalChannelBar extends ShaderImage {
	private ColorPickerWidgetStyle style;
	private Sizes sizes;
	private int maxValue;
	private float selectorY;
	private int value;

	public VerticalChannelBar (PickerCommons commons, int maxValue, ChangeListener listener) {
		super(commons.verticalChannelShader, commons.whiteTexture);
		this.style = commons.style;
		this.sizes = commons.sizes;
		this.maxValue = maxValue;
		
		setTouchable(Touchable.enabled);
		setValue(0);
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
		style.verticalSelector.draw(batch, getX(), getY() + getImageY() + selectorY - 2.5f, getImageWidth(), style.verticalSelector.getMinHeight());
	}

	public void setValue (int newValue) {
		value = newValue;
		if (value < 0) value = 0;
		if (value > maxValue) value = maxValue;

		selectorY = ((float) value / maxValue) * BasicColorPicker.PALETTE_SIZE * sizes.scaleFactor;
	}

	private void updateValueFromTouch (float y) {
		int newValue = (int) (y / BasicColorPicker.PALETTE_SIZE * maxValue / sizes.scaleFactor);
		setValue(newValue);

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	public int getValue () {
		return value;
	}
}
