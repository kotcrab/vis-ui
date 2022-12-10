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

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.toast.MessageToast;
import com.kotcrab.vis.ui.widget.toast.Toast;
import com.kotcrab.vis.ui.widget.toast.ToastTable;

/**
 * Utility for displaying toast messages at corner of application screen (by default top right). Toasts can be closed by users or they
 * can automatically disappear after a period of time. Typically only one instance of ToastManager is used per
 * application window.
 * <p>
 * To properly support window resize {@link #resize()} must be called when application resize has occurred.
 * <p>
 * Most show methods are taking {@link VisTable} however {@link ToastTable} should be preferred because it's provides
 * access to enclosing {@link Toast} instance.
 * @author Kotcrab
 * @see Toast
 * @see ToastTable
 * @see MessageToast
 * @since 1.1.0
 */
public class ToastManager {
	public static final int UNTIL_CLOSED = -1;

	protected final Group root;

	protected int screenPaddingX = 20;
	protected int screenPaddingY = 20;
	protected int messagePadding = 5;
	protected int alignment = Align.topRight;

	protected Array<Toast> toasts = new Array<Toast>();
	protected ObjectMap<Toast, Timer.Task> timersTasks = new ObjectMap<Toast, Timer.Task>();

	/** Toast manager will create own group to host toasts and put it into the stage root. */
	public ToastManager (Stage stage) {
		WidgetGroup widgetGroup = new WidgetGroup();
		widgetGroup.setFillParent(true);
		widgetGroup.setTouchable(Touchable.childrenOnly);
		stage.addActor(widgetGroup);
		this.root = widgetGroup;
	}

	/** @param root Toast manager will use this group as a host for toast actors. */
	public ToastManager (Group root) {
		this.root = root;
	}

	/** Displays basic toast with provided text as message. Toast will be displayed until it is closed by user. */
	public void show (String text) {
		show(text, UNTIL_CLOSED);
	}

	/** Displays basic toast with provided text as message. Toast will be displayed for given amount of seconds. */
	public void show (String text, float timeSec) {
		VisTable table = new VisTable();
		table.add(text).grow();
		show(table, timeSec);
	}

	/** Displays toast with provided table as toast's content. Toast will be displayed until it is closed by user. */
	public void show (Table table) {
		show(table, UNTIL_CLOSED);
	}

	/** Displays toast with provided table as toast's content. Toast will be displayed for given amount of seconds. */
	public void show (Table table, float timeSec) {
		show(new Toast(table), timeSec);
	}

	/**
	 * Displays toast with provided table as toast's content. If this toast was already displayed then it reuses
	 * stored {@link Toast} instance.
	 * Toast will be displayed until it is closed by user.
	 */
	public void show (ToastTable toastTable) {
		show(toastTable, UNTIL_CLOSED);
	}

	/**
	 * Displays toast with provided table as toast's content. If this toast was already displayed then it reuses
	 * stored {@link Toast} instance.
	 * Toast will be displayed for given amount of seconds.
	 */
	public void show (ToastTable toastTable, float timeSec) {
		Toast toast = toastTable.getToast();
		if (toast != null) {
			show(toast, timeSec);
		} else {
			show(new Toast(toastTable), timeSec);
		}
	}

	/** Displays toast. Toast will be displayed until it is closed by user. */
	public void show (Toast toast) {
		show(toast, UNTIL_CLOSED);
	}

	/** Displays toast. Toast will be displayed for given amount of seconds. */
	public void show (final Toast toast, float timeSec) {
		Table toastMainTable = toast.getMainTable();
		if (toastMainTable.getStage() != null) {
			remove(toast);
		}
		toasts.add(toast);

		toast.setToastManager(this);
		toast.fadeIn();
		toastMainTable.pack();
		root.addActor(toastMainTable);

		updateToastsPositions();

		if (timeSec > 0) {
			Timer.Task fadeOutTask = new Timer.Task() {
				@Override
				public void run () {
					toast.fadeOut();
					timersTasks.remove(toast);
				}
			};
			timersTasks.put(toast, fadeOutTask);
			Timer.schedule(fadeOutTask, timeSec);
		}
	}

	/** Must be called after application window resize to properly update toast positions on screen. */
	public void resize () {
		updateToastsPositions();
	}

	/**
	 * Removes toast from screen.
	 * @return true when toast was removed, false otherwise
	 */
	public boolean remove (Toast toast) {
		boolean removed = toasts.removeValue(toast, true);
		if (removed) {
			toast.getMainTable().remove();
			Timer.Task timerTask = timersTasks.remove(toast);
			if (timerTask != null) timerTask.cancel();
			updateToastsPositions();
		}
		return removed;
	}

	public void clear () {
		for (Toast toast : toasts) {
			toast.getMainTable().remove();
		}
		toasts.clear();
		for (Timer.Task task : timersTasks.values()) {
			task.cancel();
		}
		timersTasks.clear();
		updateToastsPositions();
	}

	public void toFront () {
		root.toFront();
	}

	protected void updateToastsPositions () {
		boolean bottom = (alignment & Align.bottom) != 0;
		boolean left = (alignment & Align.left) != 0;
		boolean center = (alignment & Align.center) != 0;
		float y = bottom ? screenPaddingY : root.getHeight() - screenPaddingY;

		for (Toast toast : toasts) {
			Table table = toast.getMainTable();
			float x = left ? screenPaddingX
					: center ? (root.getWidth() - table.getWidth() - screenPaddingX) / 2f
					: /*right*/ root.getWidth() - table.getWidth() - screenPaddingX;
			table.setPosition(x, bottom ? y : y - table.getHeight());

			y += (table.getHeight() + messagePadding) * (bottom ? 1 : -1);
		}
	}

	/**
	 * @return returns current screen padding only if padding X is equals to padding Y.
	 * @throws IllegalStateException when current screen padding X is different than screen padding Y
	 * @deprecated this method was deprecated in VisUI 1.4.3. Use either {@link #getScreenPaddingX()} or
	 * {@link #getScreenPaddingY()}. This method will be removed in future versions.
	 */
	@Deprecated
	public int getScreenPadding () {
		if (screenPaddingX != screenPaddingY) {
			throw new IllegalStateException("Value of screen padding X is different than padding Y. " +
					"Use either getScreenPaddingX or getScreenPaddingY.");
		}
		return screenPaddingX;
	}

	public int getScreenPaddingX () {
		return screenPaddingX;
	}

	public int getScreenPaddingY () {
		return screenPaddingY;
	}

	/** Sets padding of a message from window corner (actual corner used depends on current alignment settings). */
	public void setScreenPadding (int screenPadding) {
		this.screenPaddingX = screenPadding;
		this.screenPaddingY = screenPadding;
		updateToastsPositions();
	}

	/** Sets padding of a message from window corner (actual corner used depends on current alignment settings). */
	public void setScreenPadding (int screenPaddingX, int screenPaddingY) {
		this.screenPaddingX = screenPaddingX;
		this.screenPaddingY = screenPaddingY;
		updateToastsPositions();
	}

	/** Sets padding of a message from window vertical edge (actual edge used depends on current alignment settings). */
	public void setScreenPaddingX (int screenPaddingX) {
		this.screenPaddingX = screenPaddingX;
		updateToastsPositions();
	}

	/** Sets padding of a message from window horizontal edge (actual edge used depends on current alignment settings). */
	public void setScreenPaddingY (int screenPaddingY) {
		this.screenPaddingY = screenPaddingY;
		updateToastsPositions();
	}

	public int getMessagePadding () {
		return messagePadding;
	}

	/** Sets padding between messages */
	public void setMessagePadding (int messagePadding) {
		this.messagePadding = messagePadding;
		updateToastsPositions();
	}

	public int getAlignment () {
		return alignment;
	}

	/**
	 * Sets toast messages screen alignment. By default toasts are displayed in application top right corner
	 * @param alignment one of {@link Align#topLeft}, {@link Align#topRight}, {@link Align#bottomLeft} or {@link Align#bottomRight},
	 */
	public void setAlignment (int alignment) {
		this.alignment = alignment;
		updateToastsPositions();
	}
}
