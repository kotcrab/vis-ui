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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents task that is executed asynchronously in another thread. AsyncTask and related classes are not available
 * on GWT.
 * @author Kotcrab
 * @see AsyncTaskListener
 * @see SteppedAsyncTask
 * @see AsyncTaskProgressDialog
 */
public abstract class AsyncTask {
	private String threadName;
	private Status status = Status.PENDING;
	private Array<AsyncTaskListener> listeners = new Array<AsyncTaskListener>();

	public AsyncTask (String threadName) {
		this.threadName = threadName;
	}

	public void execute () {
		if (status == Status.RUNNING) throw new IllegalStateException("Task is already running.");
		if (status == Status.FINISHED)
			throw new IllegalStateException("Task has been already executed and can't be reused.");
		status = Status.RUNNING;
		new Thread(new Runnable() {
			@Override
			public void run () {
				executeInBackground();
			}
		}, threadName).start();
	}

	private void executeInBackground () {
		try {
			doInBackground();
		} catch (Exception e) {
			failed(e);
		}

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for (AsyncTaskListener listener : listeners) {
					listener.finished();
				}
				status = Status.FINISHED;
			}
		});
	}

	/**
	 * Called when this task should execute some action in background. This is always called from non-main thread.
	 * From this method only {@link #setProgressPercent(int)}, {@link #setMessage(String)}, {@link #failed(String)},
	 * {@link #failed(Exception)}, {@link #failed(String, Exception)} should be called.
	 */
	protected abstract void doInBackground () throws Exception;

	protected void failed (String message) {
		failed(message, new IllegalStateException(message));
	}

	protected void failed (Exception exception) {
		failed(exception.getMessage(), exception);
	}

	protected void failed (final String message, final Exception exception) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for (AsyncTaskListener listener : listeners) {
					listener.failed(message, exception);
				}
			}
		});
	}

	protected void setProgressPercent (final int progressPercent) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for (AsyncTaskListener listener : listeners) {
					listener.progressChanged(progressPercent);
				}
			}
		});
	}

	protected void setMessage (final String message) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				for (AsyncTaskListener listener : listeners) {
					listener.messageChanged(message);
				}
			}
		});
	}

	/**
	 * Executes runnable on main GDX thread. This methods blocks until runnable has finished executing. Note that this
	 * runnable will also block main render thread.
	 */
	protected void executeOnGdx (final Runnable runnable) {
		final CountDownLatch latch = new CountDownLatch(1);

		final AtomicReference<Exception> exceptionAt = new AtomicReference<Exception>();

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				try {
					runnable.run();
				} catch (Exception e) {
					exceptionAt.set(e);
				} finally {
					latch.countDown();
				}
			}
		});

		try {
			latch.await();

			final Exception e = exceptionAt.get();
			if (e != null) {
				failed(e);
			}
		} catch (InterruptedException e) {
			failed(e);
		}
	}

	public void addListener (AsyncTaskListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener (AsyncTaskListener listener) {
		return listeners.removeValue(listener, true);
	}

	public String getThreadName () {
		return threadName;
	}

	public Status getStatus () {
		return status;
	}

	enum Status {
		PENDING, RUNNING, FINISHED
	}
}
