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

package com.kotcrab.vis.ui.util.async;

import com.kotcrab.vis.ui.Locales.CommonText;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.util.async.AsyncTask.Status;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisWindow;

/**
 * Dialog used to display progress of {@link AsyncTask} as standard VisUI window. Shows progress bar and status
 * of currently executed task.
 * @author Kotcrab
 */
public class AsyncTaskProgressDialog extends VisWindow {
	private AsyncTask task;

	/**
	 * Creates new dialog, note that task will be automatically started. Created dialog must be manually added to stage,
	 * preferably with {@link VisWindow#fadeIn()} animation.
	 * @param title title used as window title
	 * @param task task to be executed
	 */
	public AsyncTaskProgressDialog (String title, AsyncTask task) {
		super(title);
		this.task = task;
		setModal(true);

		TableUtils.setSpacingDefaults(this);

		final VisLabel statusLabel = new VisLabel(CommonText.PLEASE_WAIT.get());
		final VisProgressBar progressBar = new VisProgressBar(0, 100, 1, false);

		defaults().padLeft(6).padRight(6);

		add(statusLabel).padTop(6).left().row();
		add(progressBar).width(300).padTop(6).padBottom(6);

		task.addListener(new AsyncTaskListener() {
			@Override
			public void progressChanged (int newProgressPercent) {
				progressBar.setValue(newProgressPercent);
			}

			@Override
			public void messageChanged (String message) {
				statusLabel.setText(message);
			}

			@Override
			public void finished () {
				fadeOut();
			}

			@Override
			public void failed (String message, Exception exception) {
				Dialogs.showErrorDialog(getStage(), exception.getMessage() == null ? CommonText.UNKNOWN_ERROR_OCCURRED.get() : exception.getMessage(), exception);
			}
		});

		pack();
		centerWindow();

		task.execute();
	}

	public AsyncTask getTask () {
		return task;
	}

	public void addListener (AsyncTaskListener listener) {
		task.addListener(listener);
	}

	public Status getStatus () {
		return task.getStatus();
	}
}
