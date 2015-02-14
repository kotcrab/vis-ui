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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisTable;
import com.kotcrab.vis.ui.widget.VisLabel;

class ColorBarWidget extends VisTable implements Disposable {
	private int value;
	private int maxValue;
	private ColorInputField.ColorBarListener drawer;
	private boolean useAlphaBar;

	private ColorInputField inputField;

	private Texture texture;
	private Pixmap pixmap;
	private Cell<ChannelBar> cell;

	//private ChannelBar.ColorBarListener imageCallback;
	private ChangeListener barListener;

	public ColorBarWidget (String label, int maxValue, final ColorInputField.ColorBarListener drawer) {
		this(label, maxValue, false, drawer);
	}

	public ColorBarWidget (String label, int maxValue, boolean useAlphaBar, final ColorInputField.ColorBarListener drawer) {
		super(true);

		this.value = 0;
		this.maxValue = maxValue;
		this.drawer = drawer;
		this.useAlphaBar = useAlphaBar;

//		imageCallback = new ChannelBar.ColorBarListener() {
//			@Override
//			public void valueChanged (int newValue) {
//				value = newValue;
//				drawer.updateFields();
//				inputField.setValue(newValue);
//			}
//		};

		barListener= new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				value = cell.getActor().getValue();
				drawer.updateFields();
				inputField.setValue(value);
			}
		};

		pixmap = new Pixmap(maxValue, 1, Format.RGBA8888);
		texture = new Texture(pixmap);
		add(new VisLabel(label)).width(10).center();
		add(inputField = new ColorInputField(maxValue, new ChannelBar.ColorBarListener() {
			@Override
			public void valueChanged (int newValue) {
				value = newValue;
				drawer.updateFields();
				cell.getActor().setValue(newValue);
			}
		})).width(ColorPicker.FIELD_WIDTH);
		cell = add(getNewBarImage()).size(ColorPicker.BAR_WIDTH, ColorPicker.BAR_HEIGHT);

		inputField.setValue(0);
	}

	@Override
	public void dispose () {
		pixmap.dispose();
		texture.dispose();
	}

	public void redraw () {
		drawer.draw(pixmap);
		texture.dispose();
		texture = new Texture(pixmap);
		cell.getActor().setDrawable(texture);
	}

	public int getValue () {
		return value;
	}

	public void setValue (int value) {
		this.value = value;
		inputField.setValue(value);
		cell.getActor().setValue(value);
	}

	private ChannelBar getNewBarImage () {
		if (useAlphaBar)
			return new AlphaChannelBar(texture, value, maxValue, barListener);
		else
			return new ChannelBar(texture, value, maxValue, barListener);
	}

	public boolean isInputValid () {
		return inputField.isInputValid();
	}
}
