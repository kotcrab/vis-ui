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

package com.kotcrab.vis.ui.util.form;

import com.kotcrab.vis.ui.InputValidator;

/** @author Kotcrab */
public abstract class FormInputValidator implements InputValidator {
	private String errorMsg;
	private boolean result;

	public FormInputValidator (String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg () {
		return errorMsg;
	}

	protected boolean getLastResult () {
		return result;
	}

	/** Should not be overridden by child class, use {@link #validate(String)} */
	@Override
	public boolean validateInput (String input) {
		result = validate(input);
		return result;
	}

	/**
	 * Called by FormInputValidator when input should be validated, for proper validator working must be used instead of {@link #validateInput(String)}.
	 * Last result of this function will be stored because it is required by FromValidator
	 * @param input that should be validated
	 * @return if input is valid, false otherwise
	 */
	protected abstract boolean validate (String input);
}
