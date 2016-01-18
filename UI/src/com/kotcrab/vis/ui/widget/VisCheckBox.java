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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.BorderOwner;

/**
 * A checkbox is a button that contains an image indicating the checked or unchecked state and a label.
 * Extends functionality of standard {@link CheckBox}, style supports more checkbox states, focus and error border. Due
 * to scope of changes made this widget is not compatible with {@link CheckBox}.
 * @author Nathan Sweet
 * @author Kotcrab
 * @see CheckBox
 */
@SuppressWarnings("rawtypes")
public class VisCheckBox extends TextButton implements Focusable, BorderOwner {
	private Image image;
	private Cell imageCell;
	private VisCheckBoxStyle style;

	private boolean drawBorder;
	private boolean stateInvalid;
	private boolean focusBorderEnabled = true;

	public VisCheckBox (String text) {
		this(text, VisUI.getSkin().get(VisCheckBoxStyle.class));
	}

	public VisCheckBox (String text, boolean checked) {
		this(text, VisUI.getSkin().get(VisCheckBoxStyle.class));
		setChecked(checked);
	}

	public VisCheckBox (String text, String styleName) {
		this(text, VisUI.getSkin().get(styleName, VisCheckBoxStyle.class));
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
				if (isDisabled() == false) FocusManager.switchFocus(getStage(), VisCheckBox.this);
				return false;
			}
		});
	}

	/**
	 * Returns the checkbox's style. Modifying the returned style may not have an effect until {@link #setStyle(ButtonStyle)} is
	 * called.
	 */
	@Override
	public CheckBoxStyle getStyle () {
		return style;
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof VisCheckBoxStyle))
			throw new IllegalArgumentException("style must be a VisCheckBoxStyle.");
		super.setStyle(style);
		this.style = (VisCheckBoxStyle) style;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		Drawable checkbox = getCheckboxImage();

		image.setDrawable(checkbox);
		super.draw(batch, parentAlpha);

		if (stateInvalid && style.errorBorder != null)
			style.errorBorder.draw(batch, getX(), getY() + image.getY(), image.getWidth(), image.getHeight());
		else if (focusBorderEnabled && drawBorder && style.focusBorder != null)
			style.focusBorder.draw(batch, getX(), getY() + image.getY(), image.getWidth(), image.getHeight());
	}

	public Image getImage () {
		return image;
	}

	public Cell getImageCell () {
		return imageCell;
	}

	/** @param stateInvalid if true error border around this checkbox will be drawn. Does not affect any other properties */
	public void setStateInvalid (boolean stateInvalid) {
		this.stateInvalid = stateInvalid;
	}

	public boolean setStateInvalid () {
		return stateInvalid;
	}

	@Override
	public void focusLost () {
		drawBorder = false;
	}

	@Override
	public void focusGained () {
		drawBorder = true;
	}

	protected Drawable getCheckboxImage () {
		if (isDisabled()) {
			if (isChecked())
				return style.checkboxOnDisabled;
			else
				return style.checkboxOffDisabled;
		}

		if (isPressed())
			if (isChecked())
				return style.checkboxOnDown;
			else
				return style.checkboxOffDown;

		if (isChecked()) {
			if (isOver())
				return style.checkboxOnOver;
			else
				return style.checkboxOn;
		}

		if (isOver())
			return style.checkboxOver;
		else
			return style.checkboxOff;
	}

	@Override
	public boolean isFocusBorderEnabled () {
		return focusBorderEnabled;
	}

	@Override
	public void setFocusBorderEnabled (boolean focusBorderEnabled) {
		this.focusBorderEnabled = focusBorderEnabled;
	}

	static public class VisCheckBoxStyle extends CheckBoxStyle {
		public Drawable focusBorder;
		public Drawable errorBorder;
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
			this.errorBorder = style.errorBorder;
			this.checkboxOnOver = style.checkboxOnOver;
			this.checkboxOnDown = style.checkboxOnDown;
			this.checkboxOffDown = style.checkboxOffDown;
		}
	}
}
