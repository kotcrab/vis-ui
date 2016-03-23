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

package com.kotcrab.vis.ui.util.form;

import com.kotcrab.vis.ui.util.InputValidator;

/**
 * Base class for all validators used in {@link SimpleFormValidator}. Implementing custom {@link FormInputValidator} doesn't
 * differ from creating standard {@link InputValidator}. You just need to supply error message which will be displayed
 * when form validation failed on this validator. Because implementing custom {@link FormInputValidator} does not require
 * any more changes you can use {@link ValidatorWrapper} for existing {@link InputValidator}s.
 * @author Kotcrab
 * @see InputValidator
 * @see ValidatorWrapper
 */
public abstract class FormInputValidator implements InputValidator {
	private String errorMsg;
	private boolean result;
	private boolean hideErrorOnEmptyInput = false;

	public FormInputValidator (String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public final boolean validateInput (String input) {
		result = validate(input);
		return result;
	}

	/**
	 * Called by FormInputValidator when input should be validated, for proper validator behaviour this must be used
	 * instead of {@link #validateInput(String)}.
	 * Last result of this function will be stored because it is required by FromValidator.
	 * @param input that should be validated.
	 * @return if input is valid, false otherwise.
	 */
	protected abstract boolean validate (String input);

	/**
	 * When called, error message of this validator won't be displayed if input field is empty, however from still will
	 * be treated as invalid (confirm button won't be enabled). This is UX improvement feature, simply don't display
	 * error before user typed in something.
	 */
	public FormInputValidator hideErrorOnEmptyInput () {
		hideErrorOnEmptyInput = true;
		return this;
	}

	/** @see #hideErrorOnEmptyInput() */
	public void setHideErrorOnEmptyInput (boolean hideErrorOnEmptyInput) {
		this.hideErrorOnEmptyInput = hideErrorOnEmptyInput;
	}

	public boolean isHideErrorOnEmptyInput () {
		return hideErrorOnEmptyInput;
	}

	public void setErrorMsg (String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg () {
		return errorMsg;
	}

	boolean getLastResult () {
		return result;
	}
}
