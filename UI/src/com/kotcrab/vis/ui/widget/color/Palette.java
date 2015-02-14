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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;

/** Colors palette, not intended to use outside ColorPicker */
public class Palette extends VisImage {
	private static final Drawable cross = VisUI.skin.getDrawable("color-picker-cross");
	private static final Drawable verticalSelector = VisUI.skin.getDrawable("color-picker-selector-horizontal");
	private static final Drawable horizontalSelector = VisUI.skin.getDrawable("color-picker-selector-vertical");

	private PaletteListener listener;

	private int maxValue;
	private float selectorX;
	private float selectorY;

	public Palette (Texture texture, int x, int y, final int maxValue, PaletteListener listener) {
		super(texture);
		this.maxValue = maxValue;
		this.listener = listener;
		setValue(x, y);

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
		selectorX = ((float) v / maxValue) * ColorPicker.PALETTE_SIZE;
		selectorY = ((float) s / maxValue) * ColorPicker.PALETTE_SIZE;
	}

	private void updateValueFromTouch (float x, float y) {
		int newX = (int) (x / ColorPicker.PALETTE_SIZE * maxValue);
		int newY = (int) (y / ColorPicker.PALETTE_SIZE * maxValue);

		if (newX < 0) newX = 0;
		if (newX > maxValue) newX = maxValue;

		if (newY < 0) newY = 0;
		if (newY > maxValue) newY = maxValue;


		setValue(newX, newY);
		listener.valueChanged(newX, newY);
	}

	interface PaletteListener {
		public void valueChanged (int newS, int newV);
	}
}
