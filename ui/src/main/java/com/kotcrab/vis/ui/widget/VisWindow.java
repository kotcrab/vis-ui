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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;

/**
 * Extends functionality of standard scene2d.ui {@link Window}.
 * @author Kotcrab
 * @see Window
 */
public class VisWindow extends Window {
	public static float FADE_TIME = 0.3f;

	private boolean centerOnAdd;
	private boolean keepWithinParent = false;

	private boolean fadeOutActionRunning;

	public VisWindow (String title) {
		this(title, true);
		getTitleLabel().setAlignment(VisUI.getDefaultTitleAlign());
	}

	public VisWindow (String title, boolean showWindowBorder) {
		super(title, VisUI.getSkin(), showWindowBorder ? "default" : "noborder");
		getTitleLabel().setAlignment(VisUI.getDefaultTitleAlign());
	}

	public VisWindow (String title, String styleName) {
		super(title, VisUI.getSkin(), styleName);
		getTitleLabel().setAlignment(VisUI.getDefaultTitleAlign());
	}

	public VisWindow (String title, WindowStyle style) {
		super(title, style);
		getTitleLabel().setAlignment(VisUI.getDefaultTitleAlign());
	}

	@Override
	public void setPosition (float x, float y) {
		super.setPosition((int) x, (int) y);
	}

	/**
	 * Centers this window, if it has parent it will be done instantly, if it does not have parent it will be centered when it will
	 * be added to stage
	 * @return true when window was centered, false when window will be centered when added to stage
	 */
	public boolean centerWindow () {
		Group parent = getParent();
		if (parent == null) {
			centerOnAdd = true;
			return false;
		} else {
			moveToCenter();
			return true;
		}
	}

	/**
	 * @param centerOnAdd if true window position will be centered on screen after adding to stage
	 * @see #centerWindow()
	 */
	public void setCenterOnAdd (boolean centerOnAdd) {
		this.centerOnAdd = centerOnAdd;
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);

		if (stage != null) {
			stage.setKeyboardFocus(this); //issue #10, newly created window does not acquire keyboard focus

			if (centerOnAdd) {
				centerOnAdd = false;
				moveToCenter();
			}
		}
	}

	private void moveToCenter () {
		Stage parent = getStage();
		if (parent != null) setPosition((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
	}

	/**
	 * Fade outs this window, when fade out animation is completed, window is removed from Stage. Calling this for the
	 * second time won't have any effect if previous animation is still running.
	 */
	public void fadeOut (float time) {
		if (fadeOutActionRunning) return;
		fadeOutActionRunning = true;
		final Touchable previousTouchable = getTouchable();
		setTouchable(Touchable.disabled);
		Stage stage = getStage();
		if (stage != null && stage.getKeyboardFocus() != null && stage.getKeyboardFocus().isDescendantOf(this)) {
			FocusManager.resetFocus(stage);
		}
		addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade), new Action() {
			@Override
			public boolean act (float delta) {
				setTouchable(previousTouchable);
				remove();
				getColor().a = 1f;
				fadeOutActionRunning = false;
				return true;
			}
		}));
	}

	/** @return this window for the purpose of chaining methods eg. stage.addActor(new MyWindow(stage).fadeIn(0.3f)); */
	public VisWindow fadeIn (float time) {
		setColor(1, 1, 1, 0);
		addAction(Actions.fadeIn(time, Interpolation.fade));
		return this;
	}

	/** Fade outs this window, when fade out animation is completed, window is removed from Stage */
	public void fadeOut () {
		fadeOut(FADE_TIME);
	}

	/** @return this window for the purpose of chaining methods eg. stage.addActor(new MyWindow(stage).fadeIn()); */
	public VisWindow fadeIn () {
		return fadeIn(FADE_TIME);
	}

	/**
	 * Called by window when close button was pressed (added using {@link #addCloseButton()})
	 * or escape key was pressed (for close on escape {@link #closeOnEscape()} have to be called).
	 * Default close behaviour is to fade out window, this can be changed by overriding this function.
	 */
	protected void close () {
		fadeOut();
	}

	/**
	 * Adds close button to window, next to window title. After pressing that button, {@link #close()} is called. If nothing
	 * else was added to title table, and current title alignment is center then the title will be automatically centered.
	 */
	public void addCloseButton () {
		Label titleLabel = getTitleLabel();
		Table titleTable = getTitleTable();

		VisImageButton closeButton = new VisImageButton("close-window");
		titleTable.add(closeButton).padRight(-getPadRight() + 0.7f);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				close();
			}
		});
		closeButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.cancel();
				return true;
			}
		});

		if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
			titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
	}

	/**
	 * Will make this window close when escape key or back key was pressed. After pressing escape or back, {@link #close()} is called.
	 * Back key is Android and iOS only
	 */
	public void closeOnEscape () {
		addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE) {
					close();
					return true;
				}

				return false;
			}

			@Override
			public boolean keyUp (InputEvent event, int keycode) {
				if (keycode == Keys.BACK) {
					close();
					return true;
				}

				return false;
			}
		});
	}

	public boolean isKeepWithinParent () {
		return keepWithinParent;
	}

	public void setKeepWithinParent (boolean keepWithinParent) {
		this.keepWithinParent = keepWithinParent;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		if (keepWithinParent && getParent() != null) {
			float parentWidth = getParent().getWidth();
			float parentHeight = getParent().getHeight();
			if (getX() < 0) setX(0);
			if (getRight() > parentWidth) setX(parentWidth - getWidth());
			if (getY() < 0) setY(0);
			if (getTop() > parentHeight) setY(parentHeight - getHeight());
		}
		super.draw(batch, parentAlpha);
	}
}
