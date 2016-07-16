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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.ButtonBar;
import com.kotcrab.vis.ui.widget.ButtonBar.ButtonType;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import static com.kotcrab.vis.ui.widget.color.internal.ColorPickerText.*;

/**
 * Color Picker dialog, allows user to select color. ColorPicker is relatively heavy dialog and should be reused whenever possible.
 * This dialog must be disposed when no longer needed! ColorPicker will be centered on screen after adding to Stage
 * use {@link #setCenterOnAdd(boolean)} to change this.
 * @author Kotcrab
 * @see ColorPicker
 * @see BasicColorPicker
 * @see ExtendedColorPicker
 * @since 0.6.0
 */
public class ColorPicker extends VisWindow implements Disposable {
	private ExtendedColorPicker picker;

	private ColorPickerListener listener;

	private VisTextButton restoreButton;
	private VisTextButton cancelButton;
	private VisTextButton okButton;

	private boolean closeAfterPickingFinished = true;

	private boolean fadeOutDueToCanceled;

	public ColorPicker () {
		this((String) null);
	}

	public ColorPicker (String title) {
		this("default", title, null);
	}

	public ColorPicker (String title, ColorPickerListener listener) {
		this("default", title, listener);
	}

	public ColorPicker (ColorPickerListener listener) {
		this("default", null, listener);
	}

	public ColorPicker (String styleName, String title, ColorPickerListener listener) {
		super(title != null ? title : "", VisUI.getSkin().get(styleName, ColorPickerStyle.class));
		this.listener = listener;

		ColorPickerStyle style = (ColorPickerStyle) getStyle();

		if (title == null) getTitleLabel().setText(TITLE.get());

		setModal(true);
		setMovable(true);

		addCloseButton();
		closeOnEscape();

		picker = new ExtendedColorPicker(style.pickerStyle, listener);

		add(picker);
		row();
		add(createButtons()).pad(3).right().expandX().colspan(3);

		pack();
		centerWindow();

		createListeners();
	}

	private VisTable createButtons () {
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setIgnoreSpacing(true);
		buttonBar.setButton(ButtonType.LEFT, restoreButton = new VisTextButton(RESTORE.get()));
		buttonBar.setButton(ButtonType.OK, okButton = new VisTextButton(OK.get()));
		buttonBar.setButton(ButtonType.CANCEL, cancelButton = new VisTextButton(CANCEL.get()));
		return buttonBar.createTable();
	}

	private void createListeners () {
		restoreButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				picker.restoreLastColor();
			}
		});

		okButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if (listener != null) listener.finished(new Color(picker.color));
				setColor(picker.color);
				if (closeAfterPickingFinished) fadeOut();
			}
		});

		cancelButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				fadeOutDueToCanceled = true;
				close();
			}
		});
	}

	@Override
	protected void setStage (Stage stage) {
		super.setStage(stage);
		if (stage == null && fadeOutDueToCanceled) {
			fadeOutDueToCanceled = false;
			setColor(picker.oldColor);
		}
	}

	/**
	 * Controls whether to fade out color picker after users finished color picking and has pressed OK button. If
	 * this is set to false picker won't close after pressing OK button. Default is true.
	 * Note that by default picker is a modal window so might also want to call {@code colorPicker.setModal(false)} to
	 * disable it.
	 */
	public void setCloseAfterPickingFinished (boolean closeAfterPickingFinished) {
		this.closeAfterPickingFinished = closeAfterPickingFinished;
	}

	@Override
	protected void close () {
		if (listener != null) listener.canceled(picker.oldColor);
		super.close();
	}

	@Override
	public void dispose () {
		picker.dispose();
	}

	/** @return internal dialog color picker */
	public ExtendedColorPicker getPicker () {
		return picker;
	}

	// ColorPicker delegates

	public boolean isShowHexFields () {
		return picker.isShowHexFields();
	}

	public void setShowHexFields (boolean showHexFields) {
		picker.setShowHexFields(showHexFields);
	}

	public boolean isDisposed () {
		return picker.isDisposed();
	}

	public void setAllowAlphaEdit (boolean allowAlphaEdit) {
		picker.setAllowAlphaEdit(allowAlphaEdit);
	}

	public boolean isAllowAlphaEdit () {
		return picker.isAllowAlphaEdit();
	}

	public void restoreLastColor () {
		picker.restoreLastColor();
	}

	@Override
	public void setColor (Color newColor) {
		picker.setColor(newColor);
	}

	public void setListener (ColorPickerListener listener) {
		this.listener = listener;
		picker.setListener(listener);
	}

	public ColorPickerListener getListener () {
		return picker.getListener();
	}
}
