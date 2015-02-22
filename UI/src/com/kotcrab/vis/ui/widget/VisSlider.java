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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;

/**
 * A slider is a horizontal indicator that allows a user to set a value. The slider has a range (min, max) and a stepping between
 * each value the slider represents.
 * <p/>
 * {@link ChangeEvent} is fired when the slider knob is moved. Canceling the event will move the knob to where it was previously.
 * <p/>
 * The preferred height of a slider is determined by the larger of the knob and background. The preferred width of a slider is
 * 140, a relatively arbitrary size.
 * @author mzechner
 * @author Nathan Sweet
 * @author Pawel Pastuszak
 */
public class VisSlider extends VisProgressBar {
	int draggingPointer = -1;

	private ClickListener clickListener;

	public VisSlider (float min, float max, float stepSize, boolean vertical) {
		this(min, max, stepSize, vertical, VisUI.getSkin()
				.get("default-" + (vertical ? "vertical" : "horizontal"), VisSliderStyle.class));
	}

	public VisSlider (float min, float max, float stepSize, boolean vertical, String styleName) {
		this(min, max, stepSize, vertical, VisUI.getSkin().get(styleName, VisSliderStyle.class));
	}

	/**
	 * Creates a new slider. It's width is determined by the given prefWidth parameter, its height is determined by the maximum of
	 * the height of either the slider {@link NinePatch} or slider handle {@link TextureRegion}. The min and max values determine
	 * the range the values of this slider can take on, the stepSize parameter specifies the distance between individual values.
	 * E.g. min could be 4, max could be 10 and stepSize could be 0.2, giving you a total of 30 values, 4.0 4.2, 4.4 and so on.
	 * @param min      the minimum value
	 * @param max      the maximum value
	 * @param stepSize the step size between values
	 * @param style    the {@link VisSliderStyle}
	 */
	public VisSlider (float min, float max, float stepSize, boolean vertical, VisSliderStyle style) {
		super(min, max, stepSize, vertical, style);

		shiftIgnoresSnap = true;

		addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (disabled) return false;
				if (draggingPointer != -1) return false;
				draggingPointer = pointer;
				calculatePositionAndValue(x, y);
				FocusManager.getFocus();
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (pointer != draggingPointer) return;
				draggingPointer = -1;
				if (!calculatePositionAndValue(x, y)) {
					// Fire an event on touchUp even if the value didn't change, so listeners can see when a drag ends via isDragging.
					ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
					fire(changeEvent);
					Pools.free(changeEvent);
				}
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				calculatePositionAndValue(x, y);
			}
		});

		clickListener = new ClickListener();
		addListener(clickListener);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		VisSliderStyle style = getStyle();
		boolean disabled = this.disabled;
		final Drawable knob = (disabled && style.disabledKnob != null) ? style.disabledKnob : //
				(isDragging() ? style.knobDown : ((clickListener.isOver() == true) ? style.knobOver : style.knob));
		final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
		final Drawable knobBefore = (disabled && style.disabledKnobBefore != null) ? style.disabledKnobBefore : style.knobBefore;
		final Drawable knobAfter = (disabled && style.disabledKnobAfter != null) ? style.disabledKnobAfter : style.knobAfter;

		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();
		float knobHeight = knob == null ? 0 : knob.getMinHeight();
		float knobWidth = knob == null ? 0 : knob.getMinWidth();
		float value = getVisualValue();

		float min = getMinValue();
		float max = getMaxValue();

		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		if (vertical) {
			bg.draw(batch, x + (int) ((width - bg.getMinWidth()) * 0.5f), y, bg.getMinWidth(), height);

			float positionHeight = height - (bg.getTopHeight() + bg.getBottomHeight());
			float knobHeightHalf = 0;
			if (min != max) {
				if (knob == null) {
					knobHeightHalf = knobBefore == null ? 0 : knobBefore.getMinHeight() * 0.5f;
					position = (value - min) / (max - min) * (positionHeight - knobHeightHalf);
					position = Math.min(positionHeight - knobHeightHalf, position);
				} else {
					knobHeightHalf = knobHeight * 0.5f;
					position = (value - min) / (max - min) * (positionHeight - knobHeight);
					position = Math.min(positionHeight - knobHeight, position) + bg.getBottomHeight();
				}
				position = Math.max(0, position);
			}

			if (knobBefore != null) {
				float offset = 0;
				if (bg != null) offset = bg.getTopHeight();
				knobBefore.draw(batch, x + (int) ((width - knobBefore.getMinWidth()) * 0.5f), y + offset, knobBefore.getMinWidth(),
						(int) (position + knobHeightHalf));
			}
			if (knobAfter != null) {
				knobAfter.draw(batch, x + (int) ((width - knobAfter.getMinWidth()) * 0.5f), y + (int) (position + knobHeightHalf),
						knobAfter.getMinWidth(), height - (int) (position + knobHeightHalf));
			}
			if (knob != null)
				knob.draw(batch, x + (int) ((width - knobWidth) * 0.5f), (int) (y + position), knobWidth, knobHeight);
		} else {
			bg.draw(batch, x, y + (int) ((height - bg.getMinHeight()) * 0.5f), width, bg.getMinHeight());

			float positionWidth = width - (bg.getLeftWidth() + bg.getRightWidth());
			float knobWidthHalf = 0;
			if (min != max) {
				if (knob == null) {
					knobWidthHalf = knobBefore == null ? 0 : knobBefore.getMinWidth() * 0.5f;
					position = (value - min) / (max - min) * (positionWidth - knobWidthHalf);
					position = Math.min(positionWidth - knobWidthHalf, position);
				} else {
					knobWidthHalf = knobWidth * 0.5f;
					position = (value - min) / (max - min) * (positionWidth - knobWidth);
					position = Math.min(positionWidth - knobWidth, position) + bg.getLeftWidth();
				}
				position = Math.max(0, position);
			}

			if (knobBefore != null) {
				float offset = 0;
				if (bg != null) offset = bg.getLeftWidth();
				knobBefore.draw(batch, x + offset, y + (int) ((height - knobBefore.getMinHeight()) * 0.5f),
						(int) (position + knobWidthHalf), knobBefore.getMinHeight());
			}
			if (knobAfter != null) {
				knobAfter.draw(batch, x + (int) (position + knobWidthHalf), y + (int) ((height - knobAfter.getMinHeight()) * 0.5f),
						width - (int) (position + knobWidthHalf), knobAfter.getMinHeight());
			}
			if (knob != null)
				knob.draw(batch, (int) (x + position), (int) (y + (height - knobHeight) * 0.5f), knobWidth, knobHeight);
		}
	}

	public void setStyle (VisSliderStyle style) {
		if (style == null) throw new NullPointerException("style cannot be null");
		if (!(style instanceof VisSliderStyle)) throw new IllegalArgumentException("style must be a SliderStyle.");
		super.setStyle(style);
	}

	/**
	 * Returns the slider's style. Modifying the returned style may not have an effect until {@link #setStyle(VisSliderStyle)} is
	 * called.
	 */
	@Override
	public VisSliderStyle getStyle () {
		return (VisSliderStyle) super.getStyle();
	}

	boolean calculatePositionAndValue (float x, float y) {
		final VisSliderStyle style = getStyle();
		final Drawable knob = (disabled && style.disabledKnob != null) ? style.disabledKnob : style.knob;
		final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;

		float value;
		float oldPosition = position;

		final float min = getMinValue();
		final float max = getMaxValue();

		if (vertical) {
			float height = getHeight() - bg.getTopHeight() - bg.getBottomHeight();
			float knobHeight = knob == null ? 0 : knob.getMinHeight();
			position = y - bg.getBottomHeight() - knobHeight * 0.5f;
			value = min + (max - min) * (position / (height - knobHeight));
			position = Math.max(0, position);
			position = Math.min(height - knobHeight, position);
		} else {
			float width = getWidth() - bg.getLeftWidth() - bg.getRightWidth();
			float knobWidth = knob == null ? 0 : knob.getMinWidth();
			position = x - bg.getLeftWidth() - knobWidth * 0.5f;
			value = min + (max - min) * (position / (width - knobWidth));
			position = Math.max(0, position);
			position = Math.min(width - knobWidth, position);
		}

		float oldValue = value;
		boolean valueSet = setValue(value);
		if (value == oldValue) position = oldPosition;
		return valueSet;
	}

	/** Returns true if the slider is being dragged. */
	public boolean isDragging () {
		return draggingPointer != -1;
	}

	/**
	 * The style for a slider, see {@link VisSlider}.
	 * @author mzechner
	 * @author Nathan Sweet
	 */
	static public class VisSliderStyle extends ProgressBarStyle {
		public Drawable knobOver;
		public Drawable knobDown;

		public VisSliderStyle () {
		}

		public VisSliderStyle (Drawable background, Drawable knob) {
			super(background, knob);
		}

		public VisSliderStyle (VisSliderStyle style) {
			super(style);
			this.knobOver = style.knobOver;
			this.knobDown = style.knobDown;
		}
	}
}
