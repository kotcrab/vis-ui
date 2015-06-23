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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;

/**
 * Simple LinkLabel allowing to create label with clickable link.
 * Link can have custom text. By default clicking link will open it in default browser, this can be changed by settings label listener.
 * @author Kotcrab
 * @since 0.7.2
 */
public class LinkLabel extends VisLabel {
	private static final String DEFAULT_COLOR_NAME = "link-label";

	private LinkLabelListener listener;
	private CharSequence url;

	public LinkLabel (CharSequence url) {
		super(url);
		setColor(VisUI.getSkin().getColor(DEFAULT_COLOR_NAME));
		init(url);
	}

	public LinkLabel (CharSequence text, CharSequence url) {
		super(text);
		setColor(VisUI.getSkin().getColor(DEFAULT_COLOR_NAME));
		init(url);
	}

	public LinkLabel (CharSequence text, int alignment) {
		super(text, alignment);
		setColor(VisUI.getSkin().getColor(DEFAULT_COLOR_NAME));
		init(text);
	}

	public LinkLabel (CharSequence text, Color textColor) {
		super(text, textColor);
		init(text);
	}

	public LinkLabel (CharSequence text, LabelStyle style) {
		super(text, style);
		init(text);
	}

	public LinkLabel (CharSequence text, CharSequence url, String styleName) {
		super(text, styleName);
		init(url);
	}

	public LinkLabel (CharSequence text, String fontName, Color color) {
		super(text, fontName, color);
		init(text);
	}

	public LinkLabel (CharSequence text, String fontName, String colorName) {
		super(text, fontName, colorName);
		init(text);
	}

	private void init (CharSequence linkUrl) {
		this.url = linkUrl;
		addListener(new ClickListener(Buttons.LEFT) {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				super.clicked(event, x, y);

				if (listener == null)
					Gdx.net.openURI(url.toString());
				else
					listener.clicked(url.toString());
			}
		});
	}

	public CharSequence getUrl () {
		return url;
	}

	public void setUrl (CharSequence url) {
		this.url = url;
	}

	public LinkLabelListener getListener () {
		return listener;
	}

	public void setListener (LinkLabelListener listener) {
		this.listener = listener;
	}

	public interface LinkLabelListener {
		void clicked (String url);
	}
}
