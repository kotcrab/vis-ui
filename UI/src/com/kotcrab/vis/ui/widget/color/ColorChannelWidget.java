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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.color.ColorInputField.ColorInputFieldListener;

/**
 * Used to display one color channel (hue, saturation etc.) with label, ColorInputField and ChannelBar
 * @author Kotcrab
 */
public class ColorChannelWidget extends VisTable implements Disposable {
	private ColorPickerStyle style;
	private Sizes sizes;
	private int value;
	private int maxValue;
	private ColorChannelWidgetListener drawer;
	private boolean useAlpha;

	private ColorInputField inputField;

	private Texture texture;
	private Pixmap pixmap;
	private ChannelBar bar;

	private ChangeListener barListener;

	public ColorChannelWidget (ColorPickerStyle style, Sizes sizes, String label, int maxValue, final ColorChannelWidgetListener drawer) {
		this(style, sizes, label, maxValue, false, drawer);
	}

	public ColorChannelWidget (ColorPickerStyle style, Sizes sizes, String label, int maxValue, boolean useAlpha, final ColorChannelWidgetListener drawer) {
		super(true);

		this.style = style;
		this.sizes = sizes;
		this.value = 0;
		this.maxValue = maxValue;
		this.drawer = drawer;
		this.useAlpha = useAlpha;

		barListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				value = bar.getValue();
				drawer.updateFields();
				inputField.setValue(value);
			}
		};

		if (useAlpha)
			pixmap = new Pixmap(maxValue, 1, Format.RGBA8888);
		else
			pixmap = new Pixmap(maxValue, 1, Format.RGB888);

		texture = new Texture(pixmap);
		add(new VisLabel(label)).width(10 * sizes.scaleFactor).center();
		add(inputField = new ColorInputField(maxValue, new ColorInputFieldListener() {
			@Override
			public void changed (int newValue) {
				value = newValue;
				drawer.updateFields();
				bar.setValue(newValue);
			}
		})).width(ColorPicker.FIELD_WIDTH * sizes.scaleFactor);
		add(bar = createBarImage()).size(ColorPicker.BAR_WIDTH * sizes.scaleFactor, ColorPicker.BAR_HEIGHT * sizes.scaleFactor);

		inputField.setValue(0);
	}

	@Override
	public void dispose () {
		pixmap.dispose();
		texture.dispose();
	}

	public void redraw () {
		drawer.draw(pixmap);
		texture.draw(pixmap, 0, 0);
	}

	public int getValue () {
		return value;
	}

	public void setValue (int value) {
		this.value = value;
		inputField.setValue(value);
		bar.setValue(value);
	}

	private ChannelBar createBarImage () {
		if (useAlpha)
			return new AlphaChannelBar(style, sizes, texture, value, maxValue, barListener);
		else
			return new ChannelBar(style, sizes, texture, value, maxValue, barListener);
	}

	public boolean isInputValid () {
		return inputField.isInputValid();
	}

	interface ColorChannelWidgetListener {
		void updateFields ();

		void draw (Pixmap pixmap);
	}
}
