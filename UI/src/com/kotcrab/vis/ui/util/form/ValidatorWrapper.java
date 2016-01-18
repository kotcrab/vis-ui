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
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * Allows standard {@link InputValidator} to be used with {@link SimpleFormValidator#custom(VisValidatableTextField, FormInputValidator)}
 * Wraps standard input validator and adds error message.
 * @author Kotcrab
 */
public class ValidatorWrapper extends FormInputValidator {
	private InputValidator validator;

	public ValidatorWrapper (String errorMsg, InputValidator validator) {
		super(errorMsg);
		this.validator = validator;
	}

	@Override
	protected boolean validate (String input) {
		return validator.validateInput(input);
	}
}
