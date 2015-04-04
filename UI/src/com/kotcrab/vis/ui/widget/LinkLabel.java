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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;

/** @author Kotcrab */
public class LinkLabel extends VisLabel {

	private static final String DEFAULT_COLOR_NAME = "link-label";
	private LinkLabelListener listener;

	public LinkLabel (CharSequence text) {
		super(text);
		setColor(VisUI.getSkin().getColor(DEFAULT_COLOR_NAME));
		init();
	}

	public LinkLabel (CharSequence text, int alignment) {
		super(text, alignment);
		setColor(VisUI.getSkin().getColor(DEFAULT_COLOR_NAME));
		init();
	}

	public LinkLabel (CharSequence text, Color textColor) {
		super(text, textColor);
		init();
	}

	public LinkLabel (CharSequence text, LabelStyle style) {
		super(text, style);
		init();
	}

	public LinkLabel (CharSequence text, String styleName) {
		super(text, styleName);
		init();
	}

	public LinkLabel (CharSequence text, String fontName, Color color) {
		super(text, fontName, color);
		init();
	}

	public LinkLabel (CharSequence text, String fontName, String colorName) {
		super(text, fontName, colorName);
		init();
	}

	private void init () {
		addListener(new ClickListener(Buttons.LEFT) {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				if(listener == null)
					Gdx.net.openURI(getText().toString());
				else
					listener.clicked(getText().toString());
			}
		});
	}

	public interface LinkLabelListener {
		void clicked (String link);
	}
}
