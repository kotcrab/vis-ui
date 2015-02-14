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

/** Colors palette used to display colors using all possible values of saturation and value, not intended to use outside ColorPicker */
public class Palette extends VisImage {
	private static final Drawable cross = VisUI.getSkin().getDrawable("color-picker-cross");
	private static final Drawable verticalSelector = VisUI.getSkin().getDrawable("color-picker-selector-horizontal");
	private static final Drawable horizontalSelector = VisUI.getSkin().getDrawable("color-picker-selector-vertical");

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
		horizontalSelector.draw(batch, getX(), getY() + selectorY - horizontalSelector.getMinHeight() / 2 + 0.1f, getImageWidth(), horizontalSelector.getMinHeight());
		verticalSelector.draw(batch, getX() + selectorX - verticalSelector.getMinWidth() / 2 + 0.1f, getY(), verticalSelector.getMinWidth(), getImageHeight());
		cross.draw(batch, getX() + selectorX - cross.getMinWidth() / 2 + 0.1f, getY() + selectorY - cross.getMinHeight() / 2 + 0.1f, cross.getMinWidth(), cross.getMinHeight());
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
