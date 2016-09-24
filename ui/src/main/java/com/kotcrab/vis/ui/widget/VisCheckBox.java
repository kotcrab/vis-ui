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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.BorderOwner;

/**
 * A checkbox is a button that contains an image indicating the checked or unchecked state and a label.
 * This widget is different than scene2d.ui's {@link CheckBox}. Style supports more checkbox states, focus and error border.
 * {@link VisCheckBoxStyle} is significantly different than {@link CheckBox.CheckBoxStyle} here background and tick are
 * stored as separate drawables. Due to scope of changes made this widget is not compatible with {@link CheckBox}.
 * <p>
 * When listening for checkbox press {@link ChangeListener} should be always preferred (instead of {@link ClickListener}).
 * {@link ClickListener} does not support disabling checkbox and will still report checkbox presses.
 * @author Nathan Sweet
 * @author Kotcrab
 * @see CheckBox
 */
public class VisCheckBox extends TextButton implements Focusable, BorderOwner {
	private VisCheckBoxStyle style;

	private Image bgImage;
	private Image tickImage;
	private Stack imageStack;
	private Cell<Stack> imageStackCell;

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

		bgImage = new Image(style.checkBackground);
		tickImage = new Image(style.tick);
		imageStackCell = add(imageStack = new Stack(bgImage, tickImage));
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
	public VisCheckBoxStyle getStyle () {
		return style;
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (style instanceof VisCheckBoxStyle == false)
			throw new IllegalArgumentException("style must be a VisCheckBoxStyle.");
		super.setStyle(style);
		this.style = (VisCheckBoxStyle) style;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		bgImage.setDrawable(getCheckboxBgImage());
		tickImage.setDrawable(getCheckboxTickImage());
		super.draw(batch, parentAlpha);

		if (isDisabled() == false && stateInvalid && style.errorBorder != null) {
			style.errorBorder.draw(batch, getX() + imageStack.getX(), getY() + imageStack.getY(), imageStack.getWidth(), imageStack.getHeight());
		} else if (focusBorderEnabled && drawBorder && style.focusBorder != null) {
			style.focusBorder.draw(batch, getX() + imageStack.getX(), getY() + imageStack.getY(), imageStack.getWidth(), imageStack.getHeight());
		}
	}

	protected Drawable getCheckboxBgImage () {
		if (isDisabled()) return style.checkBackground;
		if (isPressed()) return style.checkBackgroundDown;
		if (isOver()) return style.checkBackgroundOver;
		return style.checkBackground;
	}

	protected Drawable getCheckboxTickImage () {
		if (isChecked()) {
			return isDisabled() ? style.tickDisabled : style.tick;
		}
		return null;
	}

	public Image getBackgroundImage () {
		return bgImage;
	}

	public Image getTickImage () {
		return tickImage;
	}

	public Stack getImageStack () {
		return imageStack;
	}

	public Cell<Stack> getImageStackCell () {
		return imageStackCell;
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

	@Override
	public boolean isFocusBorderEnabled () {
		return focusBorderEnabled;
	}

	@Override
	public void setFocusBorderEnabled (boolean focusBorderEnabled) {
		this.focusBorderEnabled = focusBorderEnabled;
	}

	static public class VisCheckBoxStyle extends TextButtonStyle {
		public Drawable focusBorder;
		public Drawable errorBorder;

		public Drawable checkBackground;
		public Drawable checkBackgroundOver;
		public Drawable checkBackgroundDown;
		public Drawable tick;
		public Drawable tickDisabled;

		public VisCheckBoxStyle () {
			super();
		}

		public VisCheckBoxStyle (Drawable checkBackground, Drawable tick, BitmapFont font, Color fontColor) {
			this.checkBackground = checkBackground;
			this.tick = tick;
			this.font = font;
			this.fontColor = fontColor;
		}

		public VisCheckBoxStyle (VisCheckBoxStyle style) {
			super(style);
			this.focusBorder = style.focusBorder;
			this.errorBorder = style.errorBorder;
			this.checkBackground = style.checkBackground;
			this.checkBackgroundOver = style.checkBackgroundOver;
			this.checkBackgroundDown = style.checkBackgroundDown;
			this.tick = style.tick;
			this.tickDisabled = style.tickDisabled;
		}
	}
}
