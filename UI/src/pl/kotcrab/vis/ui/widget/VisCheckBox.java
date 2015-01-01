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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/** A checkbox is a button that contains an image indicating the checked or unchecked state and a label.
 * @author Nathan Sweet
 * @author Pawel Pastuszak */
@SuppressWarnings("rawtypes")
public class VisCheckBox extends TextButton implements Focusable {
	// This class was copied from LibGDX, few lines were changed.

	private Image image;
	private Cell imageCell;
	private VisCheckBoxStyle style;

	private boolean drawBorder;

	public VisCheckBox (String text) {
		this(text, VisUI.skin.get(VisCheckBoxStyle.class));
	}

	public VisCheckBox (String text, String styleName) {
		this(text, VisUI.skin.get(styleName, VisCheckBoxStyle.class));
	}

	public VisCheckBox (String text, VisCheckBoxStyle style) {
		super(text, style);
		clearChildren();
		imageCell = add(image = new Image(style.checkboxOff));
		Label label = getLabel();
		add(label).padLeft(5);
		label.setAlignment(Align.left);
		setSize(getPrefWidth(), getPrefHeight());

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (isDisabled() == false) FocusManager.getFocus(VisCheckBox.this);
				return false;
			}
		});
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof VisCheckBoxStyle)) throw new IllegalArgumentException("style must be a VisCheckBoxStyle.");
		super.setStyle(style);
		this.style = (VisCheckBoxStyle)style;
	}

	/** Returns the checkbox's style. Modifying the returned style may not have an effect until {@link #setStyle(ButtonStyle)} is
	 * called. */
	@Override
	public CheckBoxStyle getStyle () {
		return style;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Drawable checkbox = null;
		if (isDisabled()) {
			if (isChecked())
				checkbox = style.checkboxOnDisabled;
			else
				checkbox = style.checkboxOffDisabled;
		}

		if (checkbox == null) {
			if (isPressed())
				if (isChecked())
					checkbox = style.checkboxOnDown;
				else
					checkbox = style.checkboxOffDown;
			else if (isChecked()) {
				if (isOver())
					checkbox = style.checkboxOnOver;
				else
					checkbox = style.checkboxOn;
			} else {
				if (isOver())
					checkbox = style.checkboxOver;
				else
					checkbox = style.checkboxOff;
			}
		}
		image.setDrawable(checkbox);
		super.draw(batch, parentAlpha);

		if (drawBorder) style.focusBorder.draw(batch, getX(), getY() + image.getWidth() / 3, image.getWidth(), image.getHeight());
	}

	public Image getImage () {
		return image;
	}

	public Cell getImageCell () {
		return imageCell;
	}

	static public class VisCheckBoxStyle extends CheckBoxStyle {
		public Drawable focusBorder;
		public Drawable checkboxOnOver;
		public Drawable checkboxOnDown;
		public Drawable checkboxOffDown;

		public VisCheckBoxStyle () {
			super();
		}

		public VisCheckBoxStyle (Drawable checkboxOff, Drawable checkboxOn, BitmapFont font, Color fontColor) {
			super(checkboxOff, checkboxOn, font, fontColor);
		}

		public VisCheckBoxStyle (VisCheckBoxStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
			this.checkboxOnOver = style.checkboxOnOver;
			this.checkboxOnDown = style.checkboxOnDown;
			this.checkboxOffDown = style.checkboxOffDown;
		}
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
