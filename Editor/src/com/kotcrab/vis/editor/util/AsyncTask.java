/**
 * Copyright 2014-2015 Pawel Pastuszak
 * 
 * This file is part of VisEditor.
 * 
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util;

public abstract class AsyncTask {
	private Thread thread;
	private Runnable runnable;

	private int progressPercent;
	private String message;

	private AsyncTaskListener listener;

	public AsyncTask (String threadName) {
		runnable = new Runnable() {
			@Override
			public void run () {
				try {
					execute();
				} catch (Exception e) {
					failed(e.getMessage(), e);
				}

				if (listener != null) listener.finished();
			}
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

	public abstract void execute ();

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

	public void setMessage (String message) {
		this.message = message;
		if (listener != null) listener.messageChanged(message);
	}

	public String getMessage () {
		return message;
	}

	public void setListener (AsyncTaskListener listener) {
		this.listener = listener;
	}
}
