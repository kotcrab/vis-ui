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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.BorderOwner;

/**
 * Due to scope of changes made this widget is not compatible with standard {@link ImageButton}.
 * <p>
 * When listening for button press {@link ChangeListener} should be always preferred (instead of {@link ClickListener}).
 * {@link ClickListener} does not support disabling button and will still report button presses.
 * @author Kotcrab
 * @see ImageButton
 */
public class VisImageButton extends Button implements Focusable, BorderOwner {
	private Image image;

	private VisImageButtonStyle style;

	private boolean drawBorder;
	private boolean focusBorderEnabled = true;

	private boolean generateDisabledImage = false;

	public VisImageButton (Drawable imageUp) {
		this(imageUp, null, null);
	}

	public VisImageButton (Drawable imageUp, String tooltipText) {
		this(imageUp, null, null);
		if (tooltipText != null) new Tooltip.Builder(tooltipText).target(this).build();
	}

	public VisImageButton (Drawable imageUp, Drawable imageDown) {
		this(imageUp, imageDown, null);
	}

	public VisImageButton (Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
		super(new VisImageButtonStyle(VisUI.getSkin().get(VisImageButtonStyle.class)));
		style.imageUp = imageUp;
		style.imageDown = imageDown;
		style.imageChecked = imageChecked;

		init();
	}

	public VisImageButton (String styleName) {
		super(new VisImageButtonStyle(VisUI.getSkin().get(styleName, VisImageButtonStyle.class)));
		init();
	}

	public VisImageButton (VisImageButtonStyle style) {
		super(style);
		init();
	}

	private void init () {
		image = new Image();
		image.setScaling(Scaling.fit);
		add(image);
		setSize(getPrefWidth(), getPrefHeight());

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (isDisabled() == false) FocusManager.switchFocus(getStage(), VisImageButton.this);
				return false;
			}
		});

		updateImage();
	}

	public void setGenerateDisabledImage (boolean generate) {
		generateDisabledImage = generate;
	}

	@Override
	public VisImageButtonStyle getStyle () {
		return style;
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof VisImageButtonStyle))
			throw new IllegalArgumentException("style must be an ImageButtonStyle.");
		super.setStyle(style);
		this.style = (VisImageButtonStyle) style;
		if (image != null) updateImage();
	}

	private void updateImage () {
		Drawable drawable = null;
		if (isDisabled() && style.imageDisabled != null)
			drawable = style.imageDisabled;
		else if (isPressed() && style.imageDown != null)
			drawable = style.imageDown;
		else if (isChecked() && style.imageChecked != null)
			drawable = (style.imageCheckedOver != null && isOver()) ? style.imageCheckedOver : style.imageChecked;
		else if (isOver() && style.imageOver != null)
			drawable = style.imageOver;
		else if (style.imageUp != null) //
			drawable = style.imageUp;
		image.setDrawable(drawable);

		if (generateDisabledImage && style.imageDisabled == null && isDisabled())
			image.setColor(Color.GRAY);
		else
			image.setColor(Color.WHITE);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		updateImage();
		super.draw(batch, parentAlpha);
		if (focusBorderEnabled && drawBorder && style.focusBorder != null)
			style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public Image getImage () {
		return image;
	}

	public Cell<?> getImageCell () {
		return getCell(image);
	}

	@Override
	public void setDisabled (boolean disabled) {
		super.setDisabled(disabled);
		if (disabled) FocusManager.resetFocus(getStage(), this);
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

	/**
	 * The style for an image button, see {@link ImageButton}.
	 * @author Nathan Sweet
	 */
	static public class VisImageButtonStyle extends ButtonStyle {
		/** Optional. */
		public Drawable imageUp, imageDown, imageOver, imageChecked, imageCheckedOver, imageDisabled;
		public Drawable focusBorder;

		public VisImageButtonStyle () {
		}

		public VisImageButtonStyle (Drawable up, Drawable down, Drawable checked, Drawable imageUp, Drawable imageDown,
									Drawable imageChecked) {
			super(up, down, checked);
			this.imageUp = imageUp;
			this.imageDown = imageDown;
			this.imageChecked = imageChecked;
		}

		public VisImageButtonStyle (VisImageButtonStyle style) {
			super(style);
			this.imageUp = style.imageUp;
			this.imageDown = style.imageDown;
			this.imageOver = style.imageOver;
			this.imageChecked = style.imageChecked;
			this.imageCheckedOver = style.imageCheckedOver;
			this.imageDisabled = style.imageDisabled;

			this.focusBorder = style.focusBorder;
		}

	}
}
