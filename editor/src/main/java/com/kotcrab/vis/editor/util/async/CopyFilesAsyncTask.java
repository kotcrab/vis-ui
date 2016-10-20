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

package com.kotcrab.vis.editor.util.async;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.ui.util.async.AsyncTask;
import com.kotcrab.vis.ui.util.dialog.ConfirmDialogListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import java.util.concurrent.CountDownLatch;

/**
 * Async task for copying multiple files.
 * @author Kotcrab
 */
public class CopyFilesAsyncTask extends AsyncTask implements ConfirmDialogListener<Integer> {
	private final Stage stage;
	private final Array<CopyFileTaskDescriptor> tasks;

	private static final int YES = 0;
	private static final int NO = 1;
	private static final int YES_TO_ALL = 2;
	private static final int NO_TO_ALL = 3;
	private static final int CANCEL = 4;

	private static final String[] dialogButtons = new String[]{"Yes", "No", "No to all", "Yes to all", "Cancel"};
	private static final Integer[] dialogReturns = new Integer[]{YES, NO, NO_TO_ALL, YES_TO_ALL, CANCEL};

	private enum OverwritePolicy {ASK, YES_TO_ALL, NO_TO_ALL, ALLOW_NEXT, DISALLOW_NEXT}

	private OverwritePolicy overwritePolicy = OverwritePolicy.ASK;

	private boolean canceled;
	private CountDownLatch latch;

	public CopyFilesAsyncTask (Stage stage, Array<CopyFileTaskDescriptor> tasks) {
		super("FileCopier");
		this.stage = stage;
		this.tasks = tasks;
	}

	@Override
	public void doInBackground () {
		try {

			for (int i = 0; i < tasks.size; i++) {
				CopyFileTaskDescriptor task = tasks.get(i);

				setMessage("Copying: " + task.file.name());
				setProgressPercent(i * 100 / tasks.size);

				boolean copy = false;

				if (task.overwrites) {

					if (overwritePolicy == OverwritePolicy.ASK) {
						latch = new CountDownLatch(1);
						Dialogs.showConfirmDialog(stage, "Overwrite?",
								"File '" + task.file.name() + "' already exists in target directory, overwrite it?", dialogButtons, dialogReturns, this);
						latch.await();
					}

					switch (overwritePolicy) {
						case ASK:
							throw new IllegalStateException("Illegal overwrite policy");
						case YES_TO_ALL:
							copy = true;
							break;
						case NO_TO_ALL:
							copy = false;
							break;
						case ALLOW_NEXT:
							copy = true;
							overwritePolicy = OverwritePolicy.ASK;
							break;
						case DISALLOW_NEXT:
							copy = false;
							overwritePolicy = OverwritePolicy.ASK;
							break;
					}
				} else
					copy = true;

				if (canceled) return;
				if (copy == false) continue;

				task.file.copyTo(task.target);
			}

		} catch (InterruptedException e) {
			Log.exception(e);
		}
	}

	@Override
	public void result (Integer result) {
		switch (result) {
			case YES:
				overwritePolicy = OverwritePolicy.ALLOW_NEXT;
				break;
			case NO:
				overwritePolicy = OverwritePolicy.DISALLOW_NEXT;
				break;
			case YES_TO_ALL:
				overwritePolicy = OverwritePolicy.YES_TO_ALL;
				break;
			case NO_TO_ALL:
				overwritePolicy = OverwritePolicy.NO_TO_ALL;
				break;
			case CANCEL:
				canceled = true;
				break;
		}

		latch.countDown();
	}
}
