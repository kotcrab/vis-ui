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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;

/**
 * Colors palette used to display colors using all possible values of saturation and value, not intended to use outside ColorPicker
 * @author Kotcrab
 */
public class Palette extends VisImage {
	private static final Drawable CROSS = VisUI.getSkin().getDrawable("color-picker-cross");
	private static final Drawable VERTICAL_SELECTOR = VisUI.getSkin().getDrawable("color-picker-selector-horizontal");
	private static final Drawable HORIZONTAL_SELECTOR = VisUI.getSkin().getDrawable("color-picker-selector-vertical");

	private int x, y;
	private int maxValue;
	private float selectorX;
	private float selectorY;

	public Palette (Texture texture, int x, int y, final int maxValue, ChangeListener listener) {
		super(texture);
		this.maxValue = maxValue;
		setValue(x, y);
		addListener(listener);

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				updateValueFromTouch(x, y);
				return true;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				updateValueFromTouch(x, y);
			}
		});
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		HORIZONTAL_SELECTOR.draw(batch, getX(), getY() + selectorY - HORIZONTAL_SELECTOR.getMinHeight() / 2 + 0.1f, getImageWidth(), HORIZONTAL_SELECTOR.getMinHeight());
		VERTICAL_SELECTOR.draw(batch, getX() + selectorX - VERTICAL_SELECTOR.getMinWidth() / 2 + 0.1f, getY(), VERTICAL_SELECTOR.getMinWidth(), getImageHeight());
		CROSS.draw(batch, getX() + selectorX - CROSS.getMinWidth() / 2 + 0.1f, getY() + selectorY - CROSS.getMinHeight() / 2 + 0.1f, CROSS.getMinWidth(), CROSS.getMinHeight());
	}

	public void setValue (int v, int s) {
		x = v;
		y = s;

		if (x < 0) x = 0;
		if (x > maxValue) x = maxValue;

		if (y < 0) y = 0;
		if (y > maxValue) y = maxValue;

		selectorX = ((float) x / maxValue) * ColorPicker.PALETTE_SIZE;
		selectorY = ((float) y / maxValue) * ColorPicker.PALETTE_SIZE;
	}

	private void updateValueFromTouch (float touchX, float touchY) {
		int newX = (int) (touchX / ColorPicker.PALETTE_SIZE * maxValue);
		int newY = (int) (touchY / ColorPicker.PALETTE_SIZE * maxValue);

		setValue(newX, newY);

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	public int getS () {
		return x;
	}

	public int getV () {
		return y;
	}
}
