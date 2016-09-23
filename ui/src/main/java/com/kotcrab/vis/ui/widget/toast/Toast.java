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

package com.kotcrab.vis.ui.widget.toast;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.ToastManager;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Base class for all toasts. Toast is a wrapper around actual toast content table. It has close button and reference to
 * {@link ToastManager}. To create your own toast you should generally extend {@link ToastTable} class.
 * <p>
 * If you want further customization and modify other aspects of toast (such as close button) override
 * {@link #createMainTable()}.
 * @author Kotcrab
 * @see MessageToast
 * @see ToastTable
 * @since 1.1.0
 */
public class Toast {
	private ToastStyle style;

	private ToastManager toastManager;

	private Table mainTable;
	private Table contentTable;

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (Table content) {
		this("default", content);
	}

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (String styleName, Table content) {
		this(VisUI.getSkin().get(styleName, ToastStyle.class), content);
	}

	/** @param content table content, preferably instance of {@link ToastTable} */
	public Toast (ToastStyle style, Table content) {
		this.style = style;
		this.contentTable = content;
		if (content instanceof ToastTable) {
			((ToastTable) content).setToast(this);
		}
		createMainTable();
	}

	protected void createMainTable () {
		mainTable = new VisTable();
		mainTable.setBackground(style.background);

		VisImageButton closeButton = new VisImageButton(style.closeButtonStyle);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				close();
			}
		});

		mainTable.add(contentTable).pad(3).fill().expand();
		mainTable.add(closeButton).top();
	}

	/** Called when close button was pressed by default call {@link #fadeOut()} */
	protected void close () {
		fadeOut();
	}

	public void fadeOut () {
		mainTable.addAction(Actions.sequence(Actions.fadeOut(VisWindow.FADE_TIME, Interpolation.fade), new Action() {
			@Override
			public boolean act (float delta) {
				toastManager.remove(Toast.this);
				return true;
			}
		}));
	}

	public Table fadeIn () {
		mainTable.setColor(1, 1, 1, 0);
		mainTable.addAction(Actions.fadeIn(VisWindow.FADE_TIME, Interpolation.fade));
		return mainTable;
	}

	public Table getContentTable () {
		return contentTable;
	}

	public Table getMainTable () {
		return mainTable;
	}

	public void setToastManager (ToastManager toastManager) {
		this.toastManager = toastManager;
	}

	public ToastManager getToastManager () {
		return toastManager;
	}

	public static class ToastStyle {
		public Drawable background;
		public VisImageButtonStyle closeButtonStyle;

		public ToastStyle () {
		}

		public ToastStyle (ToastStyle style) {
			this.background = style.background;
			this.closeButtonStyle = style.closeButtonStyle;
		}

		public ToastStyle (Drawable background, VisImageButtonStyle closeButtonStyle) {
			this.background = background;
			this.closeButtonStyle = closeButtonStyle;
		}
	}
}
