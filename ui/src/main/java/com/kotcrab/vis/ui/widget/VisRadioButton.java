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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;

/**
 * Similar to {@link VisCheckBox} however uses round (instead of square) button {@link Drawable}. Note that if you
 * want to achieve 'select only one option' behaviour you need to use {@link ButtonGroup}.
 * <p>
 * When listening for button press {@link ChangeListener} should be always preferred (instead of {@link ClickListener}).
 * {@link ClickListener} does not support disabling button and will still report button presses.
 * @author Kotcrab
 * @see VisCheckBox
 */
public class VisRadioButton extends VisCheckBox {
	public VisRadioButton (String text) {
		this(text, VisUI.getSkin().get("radio", VisCheckBoxStyle.class));
	}

	public VisRadioButton (String text, VisCheckBoxStyle style) {
		super(text, style);
	}
}
