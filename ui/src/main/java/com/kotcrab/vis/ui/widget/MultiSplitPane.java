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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.SnapshotArray;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.CursorManager;

import java.util.Arrays;

/**
 * Similar to {@link VisSplitPane} but supports multiple widgets with multiple split bars at once. Use {@link #setWidgets(Actor...)}
 * after creating to set pane widgets.
 * @author Kotcrab
 * @see VisSplitPane
 * @since 1.1.4
 */
public class MultiSplitPane extends WidgetGroup {
	private MultiSplitPaneStyle style;
	private boolean vertical;

	private Array<Rectangle> widgetBounds = new Array<Rectangle>();
	private Array<Rectangle> scissors = new Array<Rectangle>();

	private Array<Rectangle> handleBounds = new Array<Rectangle>();
	private FloatArray splits = new FloatArray();

	private Vector2 handlePosition = new Vector2();
	private Vector2 lastPoint = new Vector2();

	private Rectangle handleOver;
	private int handleOverIndex;

	public MultiSplitPane (boolean vertical) {
		this(vertical, "default-" + (vertical ? "vertical" : "horizontal"));
	}

	public MultiSplitPane (boolean vertical, String styleName) {
		this(vertical, VisUI.getSkin().get(styleName, MultiSplitPaneStyle.class));
	}

	public MultiSplitPane (boolean vertical, MultiSplitPaneStyle style) {
		this.vertical = vertical;
		setStyle(style);
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
				if (getHandleContaining(x, y) != null) {
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
				if (isTouchable() == false) return false;

				if (draggingPointer != -1) return false;
				if (pointer == 0 && button != 0) return false;
				Rectangle containingHandle = getHandleContaining(x, y);
				if (containingHandle != null) {
					handleOverIndex = handleBounds.indexOf(containingHandle, true);
					FocusManager.resetFocus(getStage());

					draggingPointer = pointer;
					lastPoint.set(x, y);
					handlePosition.set(containingHandle.x, containingHandle.y);
					return true;
				}
				return false;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if (pointer == draggingPointer) draggingPointer = -1;
				handleOver = getHandleContaining(x, y);
			}

			@Override
			public boolean mouseMoved (InputEvent event, float x, float y) {
				handleOver = getHandleContaining(x, y);
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
					float targetSplit = dragX / availWidth;
					setSplit(handleOverIndex, targetSplit);
					lastPoint.set(x, y);
				} else {
					float delta = y - lastPoint.y;
					float availHeight = getHeight() - handle.getMinHeight();
					float dragY = handlePosition.y + delta;
					handlePosition.y = dragY;
					dragY = Math.max(0, dragY);
					dragY = Math.min(availHeight, dragY);
					float targetSplit = 1 - (dragY / availHeight);
					setSplit(handleOverIndex, targetSplit);
					lastPoint.set(x, y);
				}
				invalidate();
			}
		});
	}

	private Rectangle getHandleContaining (float x, float y) {
		for (Rectangle rect : handleBounds) {
			if (rect.contains(x, y)) {
				return rect;
			}
		}

		return null;
	}

	/**
	 * Returns the split pane's style. Modifying the returned style may not have an effect until {@link #setStyle(MultiSplitPaneStyle)}
	 * is called.
	 */
	public MultiSplitPaneStyle getStyle () {
		return style;
	}

	public void setStyle (MultiSplitPaneStyle style) {
		this.style = style;
		invalidateHierarchy();
	}

	@Override
	public void layout () {
		if (!vertical)
			calculateHorizBoundsAndPositions();
		else
			calculateVertBoundsAndPositions();

		SnapshotArray<Actor> actors = getChildren();
		for (int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			Rectangle bounds = widgetBounds.get(i);
			actor.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
			if (actor instanceof Layout) ((Layout) actor).validate();
		}
	}

	@Override
	public float getPrefWidth () {
		float width = 0;
		for (Actor actor : getChildren()) {
			width = actor instanceof Layout ? ((Layout) actor).getPrefWidth() : actor.getWidth();
		}
		if (!vertical) width += handleBounds.size * style.handle.getMinWidth();
		return width;
	}

	@Override
	public float getPrefHeight () {
		float height = 0;
		for (Actor actor : getChildren()) {
			height = actor instanceof Layout ? ((Layout) actor).getPrefHeight() : actor.getHeight();

		}
		if (vertical) height += handleBounds.size * style.handle.getMinHeight();
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

	public void setVertical (boolean vertical) {
		this.vertical = vertical;
	}

	private void calculateHorizBoundsAndPositions () {
		float height = getHeight();
		float width = getWidth();
		float handleWidth = style.handle.getMinWidth();

		float availWidth = width - (handleBounds.size * handleWidth);

		float areaUsed = 0;
		float currentX = 0;
		for (int i = 0; i < splits.size; i++) {
			float areaWidthFromLeft = (int) (availWidth * splits.get(i));
			float areaWidth = areaWidthFromLeft - areaUsed;
			areaUsed += areaWidth;
			widgetBounds.get(i).set(currentX, 0, areaWidth, height);
			currentX += areaWidth;
			handleBounds.get(i).set(currentX, 0, handleWidth, height);
			currentX += handleWidth;
		}
		if (widgetBounds.size != 0) widgetBounds.peek().set(currentX, 0, availWidth - areaUsed, height);
	}

	private void calculateVertBoundsAndPositions () {
		float width = getWidth();
		float height = getHeight();
		float handleHeight = style.handle.getMinHeight();

		float availHeight = height - (handleBounds.size * handleHeight);

		float areaUsed = 0;
		float currentY = height;
		for (int i = 0; i < splits.size; i++) {
			float areaHeightFromTop = (int) (availHeight * splits.get(i));
			float areaHeight = areaHeightFromTop - areaUsed;
			areaUsed += areaHeight;
			widgetBounds.get(i).set(0, currentY - areaHeight, width, areaHeight);
			currentY -= areaHeight;
			handleBounds.get(i).set(0, currentY - handleHeight, width, handleHeight);
			currentY -= handleHeight;
		}
		if (widgetBounds.size != 0) widgetBounds.peek().set(0, 0, width, availHeight - areaUsed);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		validate();

		Color color = getColor();

		applyTransform(batch, computeTransform());

		SnapshotArray<Actor> actors = getChildren();
		for (int i = 0; i < actors.size; i++) {
			Actor actor = actors.get(i);
			Rectangle bounds = widgetBounds.get(i);
			Rectangle scissor = scissors.get(i);
			getStage().calculateScissors(bounds, scissor);
			if (ScissorStack.pushScissors(scissor)) {
				if (actor.isVisible()) actor.draw(batch, parentAlpha * color.a);
				batch.flush();
				ScissorStack.popScissors();
			}
		}

		batch.setColor(color.r, color.g, color.b, parentAlpha * color.a);

		Drawable handle = style.handle;
		Drawable handleOver = style.handle;
		if (isTouchable() && style.handleOver != null) handleOver = style.handleOver;
		for (Rectangle rect : handleBounds) {
			if (this.handleOver == rect) {
				handleOver.draw(batch, rect.x, rect.y, rect.width, rect.height);
			} else {
				handle.draw(batch, rect.x, rect.y, rect.width, rect.height);
			}
		}
		resetTransform(batch);
	}

	@Override
	public Actor hit (float x, float y, boolean touchable) {
		if (touchable && getTouchable() == Touchable.disabled) return null;
		if (getHandleContaining(x, y) != null) {
			return this;
		} else {
			return super.hit(x, y, touchable);
		}
	}

	/** Changes widgets of this split pane. You can pass any number of actors even 1 or 0. Actors can't be null. */
	public void setWidgets (Actor... actors) {
		setWidgets(Arrays.asList(actors));
	}

	/** Changes widgets of this split pane. You can pass any number of actors even 1 or 0. Actors can't be null. */
	public void setWidgets (Iterable<Actor> actors) {
		clearChildren();
		widgetBounds.clear();
		scissors.clear();
		handleBounds.clear();
		splits.clear();

		for (Actor actor : actors) {
			super.addActor(actor);
			widgetBounds.add(new Rectangle());
			scissors.add(new Rectangle());
		}
		float currentSplit = 0;
		float splitAdvance = 1f / getChildren().size;
		for (int i = 0; i < getChildren().size - 1; i++) {
			handleBounds.add(new Rectangle());
			currentSplit += splitAdvance;
			splits.add(currentSplit);
		}
		invalidate();
	}

	/**
	 * @param handleBarIndex index of handle bar starting from zero, max index is number of widgets - 1
	 * @param split new value of split, must be greater than 0 and lesser than 1 and must be smaller and bigger than
	 * previous and next split value. Invalid values will be clamped to closest valid one.
	 */
	public void setSplit (int handleBarIndex, float split) {
		if (handleBarIndex < 0) throw new IllegalStateException("handleBarIndex can't be < 0");
		if (handleBarIndex >= splits.size) throw new IllegalStateException("handleBarIndex can't be >= splits size");
		float minSplit = handleBarIndex == 0 ? 0 : splits.get(handleBarIndex - 1);
		float maxSplit = handleBarIndex == splits.size - 1 ? 1 : splits.get(handleBarIndex + 1);
		split = MathUtils.clamp(split, minSplit, maxSplit);
		splits.set(handleBarIndex, split);
	}

	@Override
	public void addActorAfter (Actor actorAfter, Actor actor) {
		throw new UnsupportedOperationException("Manual actor management not supported by MultiSplitPane");
	}

	@Override
	public void addActor (Actor actor) {
		throw new UnsupportedOperationException("Manual actor management not supported by MultiSplitPane");
	}

	@Override
	public void addActorAt (int index, Actor actor) {
		throw new UnsupportedOperationException("Manual actor management not supported by MultiSplitPane");
	}

	@Override
	public void addActorBefore (Actor actorBefore, Actor actor) {
		throw new UnsupportedOperationException("Manual actor management not supported by MultiSplitPane");
	}

	@Override
	public boolean removeActor (Actor actor) {
		throw new UnsupportedOperationException("Manual actor management not supported by MultiSplitPane");
	}

	public static class MultiSplitPaneStyle extends VisSplitPane.VisSplitPaneStyle {
		public MultiSplitPaneStyle () {
		}

		public MultiSplitPaneStyle (VisSplitPane.VisSplitPaneStyle style) {
			super(style);
		}

		public MultiSplitPaneStyle (Drawable handle, Drawable handleOver) {
			super(handle, handleOver);
		}
	}
}
