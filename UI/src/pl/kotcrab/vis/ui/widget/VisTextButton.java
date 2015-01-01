/*******************************************************************************
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
 ******************************************************************************/

package pl.kotcrab.vis.ui.widget;

import pl.kotcrab.vis.ui.FocusManager;
import pl.kotcrab.vis.ui.Focusable;
import pl.kotcrab.vis.ui.VisUI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class VisTextButton extends TextButton implements Focusable {
	// This class was copied from LibGDX, few lines were changed.

	private VisTextButtonStyle style;

	private boolean drawBorder;
	private boolean focusBorderEnabled = true;

	public VisTextButton (String text, String styleName) {
		super(text, VisUI.skin.get(styleName, VisTextButtonStyle.class));
		init();
	}

	public VisTextButton (String text) {
		super(text, VisUI.skin.get(VisTextButtonStyle.class));
		init();
	}

	public VisTextButton (String text, VisTextButtonStyle buttonStyle) {
		super(text, buttonStyle);
		init();
	}

	private void init () {
		style = (VisTextButtonStyle)getStyle();

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (isDisabled() == false) FocusManager.getFocus(VisTextButton.this);
				return false;
			}
		});
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (focusBorderEnabled && drawBorder) style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	static public class VisTextButtonStyle extends TextButtonStyle {
		public Drawable focusBorder;

		public VisTextButtonStyle () {
			super();
		}

		public VisTextButtonStyle (Drawable up, Drawable down, Drawable checked, BitmapFont font) {
			super(up, down, checked, font);
		}

		public VisTextButtonStyle (VisTextButtonStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
		}
	}

	public boolean isFocusBorderEnabled () {
		return focusBorderEnabled;
	}

	public void setFocusBorderEnabled (boolean focusBorderEnabled) {
		this.focusBorderEnabled = focusBorderEnabled;
	}

	@Override
	public void focusLost () {
		drawBorder = false;
	}

	@Override
	public void focusGained () {
		drawBorder = true;
	}
}
