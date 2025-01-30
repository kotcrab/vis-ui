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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.BorderOwner;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

/**
 * A button with a child {@link Image} and {@link Label}.
 * <p>
 * Due to scope of changes made this widget is not compatible with standard {@link ImageTextButton}.
 * <p>
 * When listening for button press {@link ChangeListener} should be always preferred (instead of {@link ClickListener}).
 * {@link ClickListener} does not support disabling button and will still report button presses.
 * @author Nathan Sweet
 * @author Kotcrab
 * @see ImageButton
 * @see TextButton
 * @see Button
 */
public class VisImageTextButton extends Button implements Focusable, BorderOwner {
	public enum Orientation { TEXT_RIGHT, TEXT_LEFT, TEXT_TOP, TEXT_BOTTOM }

	private Image image;
	private Label label;

	private boolean drawBorder;
	private boolean focusBorderEnabled = true;

	private boolean generateDisabledImage = false;

	private VisImageTextButtonStyle style;
	private Orientation orientation = Orientation.TEXT_RIGHT;

	public VisImageTextButton (String text, Drawable imageUp) {
		this(text, "default", imageUp, null);
	}

	public VisImageTextButton (String text, String styleName, Drawable imageUp) {
		this(text, styleName, imageUp, null);
	}

	public VisImageTextButton (String text, String styleName, Drawable imageUp, Drawable imageDown) {
		super(new VisImageTextButtonStyle(VisUI.getSkin().get(styleName, VisImageTextButtonStyle.class)));
		style.imageUp = imageUp;
		style.imageDown = imageDown;

		init(text);
	}

	public VisImageTextButton (String text, String styleName) {
		super(new VisImageTextButtonStyle(VisUI.getSkin().get(styleName, VisImageTextButtonStyle.class)));
		init(text);
	}

	public VisImageTextButton (String text, VisImageTextButtonStyle style) {
		super(style);
		init(text);
	}

	private void init (String text) {
		defaults().space(3);

		image = new Image();
		image.setScaling(Scaling.fit);

		label = new Label(text, new LabelStyle(style.font, style.fontColor));
		label.setAlignment(Align.center);

		addActorsBasedOnOrientation();

		setStyle(style);

		setSize(getPrefWidth(), getPrefHeight());

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (isDisabled() == false) FocusManager.switchFocus(getStage(), VisImageTextButton.this);
				return false;
			}
		});
	}

	private void addActorsBasedOnOrientation() {
		switch (orientation) {
			case TEXT_RIGHT:
				add(image);
				add(label);
				break;
			case TEXT_LEFT:
				add(label);
				add(image);
				break;
			case TEXT_TOP:
				add(label);
				row();
				add(image);
				break;
			case TEXT_BOTTOM:
				add(image);
				row();
				add(label);
				break;
		}
	}

	@Override
	public void setStyle (ButtonStyle style) {
		if (!(style instanceof VisImageTextButtonStyle))
			throw new IllegalArgumentException("style must be a VisImageTextButtonStyle.");
		super.setStyle(style);
		this.style = (VisImageTextButtonStyle) style;
		if (image != null) updateImage();
		if (label != null) {
			VisImageTextButtonStyle textButtonStyle = (VisImageTextButtonStyle) style;
			LabelStyle labelStyle = label.getStyle();
			labelStyle.font = textButtonStyle.font;
			labelStyle.fontColor = textButtonStyle.fontColor;
			label.setStyle(labelStyle);
		}
	}

	@Override
	public VisImageTextButtonStyle getStyle () {
		return style;
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
		else if (style.imageUp != null)
			drawable = style.imageUp;
		image.setDrawable(drawable);

		if (generateDisabledImage && style.imageDisabled == null) {
			if (isDisabled()) {
				image.setColor(Color.GRAY);
			} else {
				image.setColor(Color.WHITE);
			}
		}
	}

	/**
	 * Returns the appropriate label font color from the style based on the current button state.
	 * <p>
	 * Taken from LibGDX 1.13.0's {@link TextButton#getFontColor()}
	 **/
	protected @Null Color getFontColor () {
		if (isDisabled() && style.disabledFontColor != null) return style.disabledFontColor;
		if (isPressed()) {
			if (isChecked() && style.checkedDownFontColor != null) return style.checkedDownFontColor;
			if (style.downFontColor != null) return style.downFontColor;
		}
		if (isOver()) {
			if (isChecked()) {
				if (style.checkedOverFontColor != null) return style.checkedOverFontColor;
			} else {
				if (style.overFontColor != null) return style.overFontColor;
			}
		}
		boolean focused = hasKeyboardFocus();
		if (isChecked()) {
			if (focused && style.checkedFocusedFontColor != null) return style.checkedFocusedFontColor;
			if (style.checkedFontColor != null) return style.checkedFontColor;
			if (isOver() && style.overFontColor != null) return style.overFontColor;
		}
		if (focused && style.focusedFontColor != null) return style.focusedFontColor;
		return style.fontColor;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		updateImage();
		Color fontColor = getFontColor();
		if (fontColor != null) label.getStyle().fontColor = fontColor;
		super.draw(batch, parentAlpha);
		if (focusBorderEnabled && drawBorder && style.focusBorder != null)
			style.focusBorder.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		clearChildren();
		addActorsBasedOnOrientation();
	}

	public Image getImage () {
		return image;
	}

	public Cell getImageCell () {
		return getCell(image);
	}

	public Label getLabel () {
		return label;
	}

	public Cell getLabelCell () {
		return getCell(label);
	}

	public void setText (CharSequence text) {
		label.setText(text);
	}

	public CharSequence getText () {
		return label.getText();
	}

	public String toString () {
		return super.toString() + ": " + label.getText();
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

	public boolean isGenerateDisabledImage () {
		return generateDisabledImage;
	}

	/**
	 * @param generate when set to true and button state is set to disabled then button image will be tinted with gray
	 * color to better symbolize that button is disabled. This works best for white images.
	 */
	public void setGenerateDisabledImage (boolean generate) {
		generateDisabledImage = generate;
	}

	/**
	 * The style for an image text button, see {@link ImageTextButton}.
	 * @author Nathan Sweet
	 */
	static public class VisImageTextButtonStyle extends VisTextButtonStyle {
		/** Optional. */
		public Drawable imageUp, imageDown, imageOver, imageChecked, imageCheckedOver, imageDisabled;

		public VisImageTextButtonStyle () {
		}

		public VisImageTextButtonStyle (Drawable up, Drawable down, Drawable checked, BitmapFont font) {
			super(up, down, checked, font);
		}

		public VisImageTextButtonStyle (VisImageTextButtonStyle style) {
			super(style);
			if (style.imageUp != null) this.imageUp = style.imageUp;
			if (style.imageDown != null) this.imageDown = style.imageDown;
			if (style.imageOver != null) this.imageOver = style.imageOver;
			if (style.imageChecked != null) this.imageChecked = style.imageChecked;
			if (style.imageCheckedOver != null) this.imageCheckedOver = style.imageCheckedOver;
			if (style.imageDisabled != null) this.imageDisabled = style.imageDisabled;
		}

		public VisImageTextButtonStyle (VisTextButtonStyle style) {
			super(style);
		}
	}
}
