/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;

import java.util.concurrent.CountDownLatch;

/**
 * Task that can be executed on another thread.
 * @author Kotcrab
 * @see AsyncTaskProgressDialog
 * @see AsyncTaskListener
 */
public abstract class AsyncTask {
	private Thread thread;
	private Runnable runnable;

	private int progressPercent;
	private String message;

	private AsyncTaskListener listener;

	public AsyncTask (String threadName) {
		runnable = () -> {
			try {
				execute();
			} catch (Exception e) {
				failed(e.getMessage(), e);
			}

			if (listener != null) listener.finished();
		};
		thread = new Thread(runnable, threadName);
	}

	public void start () {
		thread.start();
	}

	public void failed (String reason) {
		if (listener != null) listener.failed(reason);
	}

	public void failed (String reason, Exception ex) {
		if (listener != null) listener.failed(reason, ex);
	}

	public abstract void execute () throws Exception;

	/** Executes runnable on OpenGL thread. This methods blocks until runnable finished executing */
	protected void executeOnOpenGL (final Runnable runnable) {
		final CountDownLatch latch = new CountDownLatch(1);

		Gdx.app.postRunnable(() -> {
			runnable.run();
			latch.countDown();
		});

		try {
			latch.await();
		} catch (InterruptedException e) {
			Log.exception(e);
		}
	}

	public int getProgressPercent () {
		return progressPercent;
	}

	public void setProgressPercent (int progressPercent) {
		this.progressPercent = progressPercent;
		if (listener != null) listener.progressChanged(progressPercent);
	}

	public void setRunnable (Runnable runnable) {
		this.runnable = runnable;
	}

	public String getMessage () {
		return message;
	}

	public void setMessage (String message) {
		this.message = message;
		if (listener != null) listener.messageChanged(message);
	}

	public void setListener (AsyncTaskListener listener) {
		this.listener = listener;
	}
}
