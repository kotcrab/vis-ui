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

package com.kotcrab.vis.ui.test.manual;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class TestColorPicker extends VisWindow {
	private static final Drawable white = VisUI.getSkin().getDrawable("white");

	private ColorPicker picker;

	public TestColorPicker () {
		super("color picker");

		final Image image = new Image(white);

		picker = new ColorPicker("color picker", new ColorPickerAdapter() {
			@Override
			public void finished (Color newColor) {
				image.setColor(newColor);
			}
		});

		VisTextButton showPickerButton = new VisTextButton("show color picker");
		showPickerButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				getStage().addActor(picker.fadeIn());
			}
		});

		Color c = new Color(27 / 255.0f, 161 / 255.0f, 226 / 255.0f, 1);
		picker.setColor(c);
		image.setColor(c);

		TableUtils.setSpacingDefaults(this);

		add(showPickerButton);
		add(image).size(32).pad(3);

		pack();
		setPosition(948, 148);
	}

	@Override
	protected void close () {
		super.close();
		picker.dispose();
	}
}
