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
 * {@link AsyncTask} that performs fixed numbers of steps, provides convenient methods to calculate and update task progress.
 * @author Kotcrab
 */
public abstract class SteppedAsyncTask extends AsyncTask {
	private int step;
	private int totalSteps;

	public SteppedAsyncTask (String threadName) {
		super(threadName);
	}

	/**
	 * Sets total numbers ot steps this task will have to perform, usually called at the beginning of {@link #doInBackground()}.
	 * @see #nextStep()
	 */
	protected void setTotalSteps (int totalSteps) {
		this.totalSteps = totalSteps;
		this.step = 0;
		setProgressPercent(0);
	}

	/** Advances task to next step and updates its percent progress. */
	protected void nextStep () {
		setProgressPercent(++step * 100 / totalSteps);
	}
}
