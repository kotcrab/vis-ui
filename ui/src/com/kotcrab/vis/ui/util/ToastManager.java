package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.toast.MessageToast;
import com.kotcrab.vis.ui.widget.toast.Toast;
import com.kotcrab.vis.ui.widget.toast.ToastTable;

/**
 * Utility for displaying toast messages at upper right corner of application screen. Toasts can be closed by users or they
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

	private Stage stage;

	private int screenPadding = 20;
	private int messagePadding = 5;
	private Array<Toast> toasts = new Array<Toast>();
	private ObjectMap<Toast, Timer.Task> timersTasks = new ObjectMap<Toast, Timer.Task>();

	public ToastManager (Stage stage) {
		this.stage = stage;
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
		stage.addActor(toastMainTable);

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
			Timer.Task timerTask = timersTasks.remove(toast);
			if (timerTask != null) timerTask.cancel();
			updateToastsPositions();
		}
		return removed;
	}

	public void clear () {
		toasts.clear();
		for (Timer.Task task : timersTasks.values()) {
			task.cancel();
		}
		timersTasks.clear();
	}

	private void updateToastsPositions () {
		float y = stage.getHeight() - screenPadding;

		for (Toast toast : toasts) {
			Table table = toast.getMainTable();
			table.setPosition(stage.getWidth() - table.getWidth() - screenPadding, y - table.getHeight());
			y = y - table.getHeight() - messagePadding;
		}
	}

	public int getScreenPadding () {
		return screenPadding;
	}

	/** Sets padding of message from window top right corner */
	public void setScreenPadding (int screenPadding) {
		this.screenPadding = screenPadding;
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
}
