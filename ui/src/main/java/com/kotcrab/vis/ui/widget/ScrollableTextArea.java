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

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;

/**
 * Custom {@link VisTextArea} supporting embedding in scroll pane by calculating required space needed for current text.
 * <p>
 * Warning: By default this can only support vertical scrolling. Scrolling in X direction MUST be disabled. It is NOT possible
 * to use vertical scrolling without child class properly implementing {@link #getPrefWidth()} and disabling soft wraps.
 * Example of such class is {@link HighlightTextArea}.
 * <p>
 * For best scroll pane settings you should create scroll pane using {@link #createCompatibleScrollPane()}.
 * @author Kotcrab
 * @since 1.1.2
 */
public class ScrollableTextArea extends VisTextArea implements Cullable {
	private Rectangle cullingArea;

	public ScrollableTextArea (String text) {
		super(text, "textArea");
	}

	public ScrollableTextArea (String text, String styleName) {
		super(text, styleName);
	}

	public ScrollableTextArea (String text, VisTextFieldStyle style) {
		super(text, style);
	}

	@Override
	protected InputListener createInputListener () {
		return new ScrollTextAreaListener();
	}

	@Override
	protected void setParent (Group parent) {
		super.setParent(parent);
		if (parent instanceof ScrollPane) {
			calculateOffsets();
		}
	}

	private void updateScrollPosition () {
		if (cullingArea == null || getParent() instanceof ScrollPane == false) return;
		ScrollPane scrollPane = (ScrollPane) getParent();

		if (cullingArea.contains(getCursorX(), cullingArea.y) == false) {
			scrollPane.setScrollPercentX(getCursorX() / getWidth());
		}

		if (cullingArea.contains(cullingArea.x, (getHeight() - getCursorY())) == false) {
			scrollPane.setScrollPercentY(getCursorY() / getHeight());
		}
	}

	@Override
	public void setCullingArea (Rectangle cullingArea) {
		this.cullingArea = cullingArea;
	}

	/**
	 * Creates scroll pane for this scrolling text area with best possible default settings. Note that text area
	 * can belong to only one scroll pane, calling this multiple times will break previously created scroll pane.
	 * The scroll pane should be embedded in container with fixed size or optionally grow property.
	 * @return newly created scroll pane which can be added directly to container.
	 */
	public ScrollPane createCompatibleScrollPane () {
		VisScrollPane scrollPane = new VisScrollPane(this);
		scrollPane.setOverscroll(false, false);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setScrollbarsOnTop(true);
		scrollPane.setScrollingDisabled(true, false);
		return scrollPane;
	}

	@Override
	protected void sizeChanged () {
		super.sizeChanged();
		linesShowing = 1000000000; //aka a lot, forces text area not to use its stupid 'scrolling'
	}

	@Override
	public float getPrefHeight () {
		return getLines() * style.font.getLineHeight();
	}

	@Override
	public void setText (String str) {
		super.setText(str);
		if (programmaticChangeEvents == false) { //changeText WILL NOT be called when programmaticChangeEvents are disabled
			updateScrollLayout();
		}
	}

	@Override
	boolean changeText (String oldText, String newText) {
		boolean changed = super.changeText(oldText, newText);
		updateScrollLayout();
		return changed;
	}

	void updateScrollLayout () {
		invalidateHierarchy();
		layout();
		if (getParent() instanceof ScrollPane) ((ScrollPane) getParent()).layout();
		updateScrollPosition();
	}

	public class ScrollTextAreaListener extends TextAreaListener {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			updateScrollPosition();
			return super.keyDown(event, keycode);
		}

		@Override
		public boolean keyTyped (InputEvent event, char character) {
			updateScrollPosition();
			return super.keyTyped(event, character);
		}
	}
}
