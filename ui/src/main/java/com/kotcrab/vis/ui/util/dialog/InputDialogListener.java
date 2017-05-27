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

package com.kotcrab.vis.ui.util.dialog;

/**
 * Used to get events from {@link Dialogs} input dialog.
 * @author Kotcrab
 */
public interface InputDialogListener {
	/**
	 * Called when input dialog has finished.
	 * @param input text entered by user.
	 */
	void finished (String input);

	/**
	 * Called when user canceled dialog or pressed 'close' button. This won't be ever called if dialog is not
	 * cancelable.
	 */
	void canceled ();
}
