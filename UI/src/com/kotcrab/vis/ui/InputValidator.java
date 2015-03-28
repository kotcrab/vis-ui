/*
 * Copyright 2014-2015 Pawel Pastuszak
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

package com.kotcrab.vis.ui;

import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

/**
 * Interface implemented by classes that can validate whether user input is right or wrong, typically used by {@link VisValidableTextField}
 * and {@link DialogUtils} input dialog
 * @author Kotcrab
 */
public interface InputValidator {
	/**
	 * Called when input must be validated
	 * @param input text that should be validated
	 * @return true if input is valid, false otherwise
	 */
	boolean validateInput (String input);
}
