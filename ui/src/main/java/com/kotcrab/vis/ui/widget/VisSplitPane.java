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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.CursorManager;

/**
 * Extends functionality of standard {@link SplitPane}. Style supports handle over {@link Drawable}. Due to scope of
 * changes made this widget is not compatible with {@link SplitPane}.
 * @author mzechner
 * @author Nathan Sweet
 * @author Kotcrab
 * @see SplitPane
 */
public class VisSplitPane extends WidgetGroup {
	VisSplitPaneStyle style;
	private Actor firstWidget, secondWidget;
	boolean vertical;
	float splitAmount = 0.5f, minAmount, maxAmount = 1;
	// private float oldSplitAmount;

	private Rectangle firstWidgetBounds = new Rectangle();
	private Rectangle secondWidgetBounds = new Rectangle();
	Rectangle handleBounds = new Rectangle();
	private Rectangle firstScissors = new Rectangle();
	private Rectangle secondScissors = new Rectangle();

	Vector2 lastPoint = new Vector2();
	Vector2 handlePosition = new Vector2();

	private boolean mouseOnHandle;

	/**
	 * @param firstWidget May be null.
	 * @param secondWidget May be null.
	 */
	public VisSplitPane (Actor firstWidget, Actor secondWidget, boolean vertical) {
		this(firstWidget, secondWidget, vertical, "default-" + (vertical ? "vertical" : "horizontal"));
	}

	/**
	 * @param firstWidget May be null.
	 * @param secondWidget May be null.
	 */
	public VisSplitPane (Actor firstWidget, Actor secondWidget, boolean vertical, String styleName) {
		this(firstWidget, secondWidget, vertical, VisUI.getSkin().get(styleName, VisSplitPaneStyle.class));
	}

	/**
	 * @param firstWidget May be null.
	 * @param secondWidget May be null.
	 */
	public VisSplitPane (Actor firstWidget, Actor secondWidget, boolean vertical, VisSplitPaneStyle style) {
		this.firstWidget = firstWidget;
		this.secondWidget = secondWidget;
		this.vertical = vertical;
		setStyle(style);
		setFirstWidget(firstWidget);
		setSecondWidget(secondWidget);
		setSize(getPrefWidth(), getPrefHeight());
		initialize();
	}

	private void initialize () {
		addListener(new ClickListener() {
			SystemCursor currentCursor;
			SystemCursor targetCursor;

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				CursorManager.restoreDefaultCursor();
				currentCursor = null;
			}

			@Override
			public boolean mouseMoved (InputEvent event, float x, float y) {
				if (handleBounds.contains(x, y)) {
					if (vertical) {
						targetCursor = SystemCursor.VerticalResize;
					} else {
						targetCursor = SystemCursor.HorizontalResize;
					}

					if (currentCursor != targetCursor) {
						Gdx.graphics.setSystemCursor(targetCursor);
						currentCursor = targetCursor;
					}
				} else {
					clearCustomCursor();
				}

				return false;
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);
				if (pointer == -1) {
					clearCustomCursor();
				}
			}

			private void clearCustomCursor () {
				if (currentCursor != null) {
					CursorManager.restoreDefaultCursor();
					currentCursor = null;
				}
			}
		});

		addListener(new InputListener() {
			int draggingPointer = -1;

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				//TODO potential bug with libgdx scene2d?
				//fixes issue when split bar could be still dragged even when touchable is set to childrenOnly, probably scene2d issue
				if (isTouchable() == false) return false;

				if (draggingPointer != -1) return false;
				if (pointer == 0 && button != 0) return false;
				if (handleBounds.contains(x, y)) {
					FocusManager.resetFocus(getStage());

					draggingPointer = pointer;
					lastPoint.set(x, y);
					handlePosition.set(handleBounds.x, handleBounds.y);
					return true;
				}
				return false;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (pointer == draggingPointer) draggingPointer = -1;
			}

			@Override
			public boolean mouseMoved (InputEvent event, float x, float y) {
				mouseOnHandle = handleBounds.contains(x, y);
				return false;
			}

			@Override
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				if (pointer != draggingPointer) return;

				Drawable handle = style.handle;
				if (!vertical) {
					float delta = x - lastPoint.x;
					float availWidth = getWidth() - handle.getMinWidth();
					float dragX = handlePosition.x + delta;
					handlePosition.x = dragX;
					dragX = Math.max(0, dragX);
					dragX = Math.min(availWidth, dragX);
					splitAmount = dragX / availWidth;
					if (splitAmount < minAmount) splitAmount = minAmount;
					if (splitAmount > maxAmount) splitAmount = maxAmount;
					lastPoint.set(x, y);
				} else {
					float delta = y - lastPoint.y;
					float availHeight = getHeight() - handle.getMinHeight();
					float dragY = handlePosition.y + delta;
					handlePosition.y = dragY;
					dragY = Math.max(0, dragY);
					dragY = Math.min(availHeight, dragY);
					splitAmount = 1 - (dragY / availHeight);
					if (splitAmount < minAmount) splitAmount = minAmount;
					if (splitAmount > maxAmount) splitAmount = maxAmount;
					lastPoint.set(x, y);
				}
				invalidate();
			}
		});
	}

	/**
	 * Returns the split pane's style. Modifying the returned style may not have an effect until {@link #setStyle(VisSplitPaneStyle)}
	 * is called.
	 */
	public VisSplitPaneStyle getStyle () {
		return style;
	}

	public void setStyle (VisSplitPaneStyle style) {
		this.style = style;
		invalidateHierarchy();
	}

	@Override
	public void layout () {
		if (!vertical)
			calculateHorizBoundsAndPositions();
		else
			calculateVertBoundsAndPositions();

		Actor firstWidget = this.firstWidget;
		if (firstWidget != null) {
			Rectangle firstWidgetBounds = this.firstWidgetBounds;
			firstWidget.setBounds(firstWidgetBounds.x, firstWidgetBounds.y, firstWidgetBounds.width, firstWidgetBounds.height);
			if (firstWidget instanceof Layout) ((Layout) firstWidget).validate();
		}
		Actor secondWidget = this.secondWidget;
		if (secondWidget != null) {
			Rectangle secondWidgetBounds = this.secondWidgetBounds;
			secondWidget.setBounds(secondWidgetBounds.x, secondWidgetBounds.y, secondWidgetBounds.width, secondWidgetBounds.height);
			if (secondWidget instanceof Layout) ((Layout) secondWidget).validate();
		}
	}

	@Override
	public float getPrefWidth () {
		float width = 0;
		if (firstWidget != null)
			width = firstWidget instanceof Layout ? ((Layout) firstWidget).getPrefWidth() : firstWidget.getWidth();
		if (secondWidget != null)
			width += secondWidget instanceof Layout ? ((Layout) secondWidget).getPrefWidth() : secondWidget.getWidth();
		if (!vertical) width += style.handle.getMinWidth();
		return width;
	}

	@Override
	public float getPrefHeight () {
		float height = 0;
		if (firstWidget != null)
			height = firstWidget instanceof Layout ? ((Layout) firstWidget).getPrefHeight() : firstWidget.getHeight();
		if (secondWidget != null)
			height += secondWidget instanceof Layout ? ((Layout) secondWidget).getPrefHeight() : secondWidget.getHeight();
		if (vertical) height += style.handle.getMinHeight();
		return height;
	}

	@Override
	public float getMinWidth () {
		return 0;
	}

	@Override
	public float getMinHeight () {
		return 0;
	}

	/** @return first widgets bounds, changing returned rectangle values does not have any effect */
	public Rectangle getFirstWidgetBounds () {
		return new Rectangle(firstWidgetBounds);
	}

	/** @return seconds widgets bounds, changing returned rectangle values does not have any effect */
	public Rectangle getSecondWidgetBounds () {
		return new Rectangle(secondWidgetBounds);
	}

	public void setVertical (boolean vertical) {
		this.vertical = vertical;
	}

	private void calculateHorizBoundsAndPositions () {
		Drawable handle = style.handle;

		float height = getHeight();

		float availWidth = getWidth() - handle.getMinWidth();
		float leftAreaWidth = (int) (availWidth * splitAmount);
		float rightAreaWidth = availWidth - leftAreaWidth;
		float handleWidth = handle.getMinWidth();

		firstWidgetBounds.set(0, 0, leftAreaWidth, height);
		secondWidgetBounds.set(leftAreaWidth + handleWidth, 0, rightAreaWidth, height);
		handleBounds.set(leftAreaWidth, 0, handleWidth, height);
	}

	private void calculateVertBoundsAndPositions () {
		Drawable handle = style.handle;

		float width = getWidth();
		float height = getHeight();

		float availHeight = height - handle.getMinHeight();
		float topAreaHeight = (int) (availHeight * splitAmount);
		float bottomAreaHeight = availHeight - topAreaHeight;
		float handleHeight = handle.getMinHeight();

		firstWidgetBounds.set(0, height - topAreaHeight, width, topAreaHeight);
		secondWidgetBounds.set(0, 0, width, bottomAreaHeight);
		handleBounds.set(0, bottomAreaHeight, width, handleHeight);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		validate();

		Color color = getColor();

		applyTransform(batch, computeTransform());
		// Matrix4 transform = batch.getTransformMatrix();
		if (firstWidget != null) {
			getStage().calculateScissors(firstWidgetBounds, firstScissors);
			if (ScissorStack.pushScissors(firstScissors)) {
				if (firstWidget.isVisible()) firstWidget.draw(batch, parentAlpha * color.a);
				batch.flush();
				ScissorStack.popScissors();
			}
		}
		if (secondWidget != null) {
			getStage().calculateScissors(secondWidgetBounds, secondScissors);
			if (ScissorStack.pushScissors(secondScissors)) {
				if (secondWidget.isVisible()) secondWidget.draw(batch, parentAlpha * color.a);
				batch.flush();
				ScissorStack.popScissors();
			}
		}

		Drawable handle = style.handle;
		if (mouseOnHandle && isTouchable() && style.handleOver != null) handle = style.handleOver;
		batch.setColor(color.r, color.g, color.b, parentAlpha * color.a);
		handle.draw(batch, handleBounds.x, handleBounds.y, handleBounds.width, handleBounds.height);
		resetTransform(batch);

	}

	@Override
	public Actor hit (float x, float y, boolean touchable) {
		if (touchable && getTouchable() == Touchable.disabled) return null;
		if (handleBounds.contains(x, y)) {
			return this;
		} else {
			return super.hit(x, y, touchable);
		}
	}

	/** @param split The split amount between the min and max amount. */
	public void setSplitAmount (float split) {
		this.splitAmount = Math.max(Math.min(maxAmount, split), minAmount);
		invalidate();
	}

	public float getSplit () {
		return splitAmount;
	}

	public void setMinSplitAmount (float minAmount) {
		if (minAmount < 0) throw new GdxRuntimeException("minAmount has to be >= 0");
		if (minAmount >= maxAmount) throw new GdxRuntimeException("minAmount has to be < maxAmount");
		this.minAmount = minAmount;
	}

	public void setMaxSplitAmount (float maxAmount) {
		if (maxAmount > 1) throw new GdxRuntimeException("maxAmount has to be >= 0");
		if (maxAmount <= minAmount) throw new GdxRuntimeException("maxAmount has to be > minAmount");
		this.maxAmount = maxAmount;
	}

	/**
	 * @param firstWidget May be null
	 * @param secondWidget May be null
	 */
	public void setWidgets (Actor firstWidget, Actor secondWidget) {
		setFirstWidget(firstWidget);
		setSecondWidget(secondWidget);
	}

	/** @param widget May be null. */
	public void setFirstWidget (Actor widget) {
		if (firstWidget != null) super.removeActor(firstWidget);
		firstWidget = widget;
		if (widget != null) super.addActor(widget);
		invalidate();
	}

	/** @param widget May be null. */
	public void setSecondWidget (Actor widget) {
		if (secondWidget != null) super.removeActor(secondWidget);
		secondWidget = widget;
		if (widget != null) super.addActor(widget);
		invalidate();
	}

	@Override
	public void addActor (Actor actor) {
		throw new UnsupportedOperationException("Manual actor manipulation not supported");
	}

	@Override
	public void addActorAt (int index, Actor actor) {
		throw new UnsupportedOperationException("Manual actor manipulation not supported");
	}

	@Override
	public void addActorBefore (Actor actorBefore, Actor actor) {
		throw new UnsupportedOperationException("Manual actor manipulation not supported");
	}

	@Override
	public boolean removeActor (Actor actor) {
		throw new UnsupportedOperationException("Manual actor manipulation not supported");
	}

	public static class VisSplitPaneStyle extends SplitPaneStyle {
		/** Optional **/
		public Drawable handleOver;

		public VisSplitPaneStyle () {
		}

		public VisSplitPaneStyle (VisSplitPaneStyle style) {
			super(style);
			this.handleOver = style.handleOver;
		}

		public VisSplitPaneStyle (Drawable handle, Drawable handleOver) {
			super(handle);
			this.handleOver = handleOver;
		}
	}
}
