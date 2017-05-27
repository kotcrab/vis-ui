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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
 * Used to display channel color bars in color picker.
 * @author Kotcrab
 */
public class ChannelBar extends ShaderImage {
	public static final int MODE_ALPHA = 0;

	public static final int MODE_R = 1;
	public static final int MODE_G = 2;
	public static final int MODE_B = 3;

	public static final int MODE_H = 4;
	public static final int MODE_S = 5;
	public static final int MODE_V = 6;

	protected ColorPickerWidgetStyle style;
	private Sizes sizes;

	private int maxValue;
	private int value;
	private float selectorX;

	private int mode;
	private ChannelBarListener channelBarListener;

	public ChannelBar (PickerCommons commons, int mode, int maxValue, ChangeListener changeListener) {
		super(commons.getBarShader(mode), commons.whiteTexture);
		this.style = commons.style;
		this.sizes = commons.sizes;
		this.mode = mode;
		this.maxValue = maxValue;

		setTouchable(Touchable.enabled);
		setValue(value);
		addListener(changeListener);

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
		style.barSelector.draw(batch, getX() + selectorX - style.barSelector.getMinWidth() / 2, getY() - 1, style.barSelector.getMinWidth(), style.barSelector.getMinHeight());
	}

	public void setValue (int newValue) {
		this.value = newValue;
		if (value < 0) value = 0;
		if (value > maxValue) value = maxValue;

		selectorX = ((float) value / maxValue) * BasicColorPicker.BAR_WIDTH * sizes.scaleFactor;
	}

	public int getValue () {
		return value;
	}

	private void updateValueFromTouch (float x) {
		int newValue = (int) (x / BasicColorPicker.BAR_WIDTH * maxValue / sizes.scaleFactor);
		setValue(newValue);

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	@Override
	protected void setShaderUniforms (ShaderProgram shader) {
		shader.setUniformi("u_mode", mode);
		channelBarListener.setShaderUniforms(shader);
	}

	public void setChannelBarListener (ChannelBarListener channelBarListener) {
		this.channelBarListener = channelBarListener;
	}

	public interface ChannelBarListener {
		void updateFields ();

		void setShaderUniforms (ShaderProgram shader);
	}
}

