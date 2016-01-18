/*
 * Copyright 2014-2016 See AUTHORS file.
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.Sizes;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle;
import com.kotcrab.vis.ui.widget.color.internal.ColorInputField.ColorInputFieldListener;

/**
 * Used to display one color channel (hue, saturation etc.) with label, ColorInputField and ChannelBar.
 * @author Kotcrab
 */
public class ColorChannelWidget extends VisTable {
	private PickerCommons commons;
	private ColorPickerWidgetStyle style;
	private Sizes sizes;

	private ChannelBar bar;
	private ChangeListener barListener;
	private ColorInputField inputField;

	private int mode;
	private int value;
	private int maxValue;

	public ColorChannelWidget (PickerCommons commons, String label, int mode, int maxValue, final ChannelBar.ChannelBarListener listener) {
		super(true);
		this.commons = commons;

		this.style = commons.style;
		this.sizes = commons.sizes;
		this.mode = mode;
		this.maxValue = maxValue;

		barListener = new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				value = bar.getValue();
				listener.updateFields();
				inputField.setValue(value);
			}
		};

		add(new VisLabel(label)).width(10 * sizes.scaleFactor).center();
		add(inputField = new ColorInputField(maxValue, new ColorInputFieldListener() {
			@Override
			public void changed (int newValue) {
				value = newValue;
				listener.updateFields();
				bar.setValue(newValue);
			}
		})).width(BasicColorPicker.FIELD_WIDTH * sizes.scaleFactor);
		add(bar = createBarImage()).size(BasicColorPicker.BAR_WIDTH * sizes.scaleFactor, BasicColorPicker.BAR_HEIGHT * sizes.scaleFactor);
		bar.setChannelBarListener(listener);

		inputField.setValue(0);
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
		if (mode == ChannelBar.MODE_ALPHA)
			return new AlphaChannelBar(commons, mode, maxValue, barListener);
		else
			return new ChannelBar(commons, mode, maxValue, barListener);
	}

	public ChannelBar getBar () {
		return bar;
	}

	public boolean isInputValid () {
		return inputField.isInputValid();
	}

}
