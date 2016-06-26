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

package com.kotcrab.vis.editor.util;

import com.kotcrab.vis.editor.Log;

/**
 * Threads related utils.
 * @author Kotcrab
 */
public class ThreadUtils {
	/**
	 * Thread that executed this method will sleep for the specific number of milliseconds. If happen InterruptedException will be
	 * logged using {@link Log} class.
	 * @param millis length of time to sleep in milliseconds
	 */
	public static void sleep (long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Log.exception(e);
		}
	}
}
