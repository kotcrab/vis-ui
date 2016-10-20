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

/**
 * Allows to listen to events occurring in {@link AsyncTask}.
 * @author Kotcrab
 */
public interface AsyncTaskListener {
	/** Called when task status message has changed. */
	void messageChanged (String message);

	/** Called when task progress has changed. */
	void progressChanged (int newProgressPercent);

	/**
	 * Called when task has finished executing. Finished will always called, even if some exception occurred during task
	 * execution.
	 */
	void finished ();

	/** Called when some error occurred during task execution. */
	void failed (String message, Exception exception);
}
