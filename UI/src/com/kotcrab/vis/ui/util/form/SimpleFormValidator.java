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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

/**
 * Makes validating forms easier
 * <p>
 * SimpleFormValidator is GWT compatible and does not provide fileExists methods, if you are not using GWT use
 * {@link FormValidator}
 * @author Kotcrab
 */
public class SimpleFormValidator {
	private ChangeSharedListener changeListener = new ChangeSharedListener();
	private Array<VisValidableTextField> fields = new Array<VisValidableTextField>();

	private Button button;
	private Label errorMsgLabel;

	private boolean hideErrorOnEmptyInput;

	public SimpleFormValidator (Button buttonToDisable, Label errorMsgLabel) {
		this.button = buttonToDisable;
		this.errorMsgLabel = errorMsgLabel;
	}

	public FormInputValidator notEmpty (VisValidableTextField field, String errorMsg) {
		EmptyInputValidator validator = new EmptyInputValidator(errorMsg);
		field.addValidator(validator);
		add(field);
		return validator;
	}

	public FormInputValidator integerNumber (VisValidableTextField field, String errorMsg) {
		ValidatorWrapper wrapper = new ValidatorWrapper(errorMsg, Validators.INTEGERS);
		field.addValidator(wrapper);
		add(field);
		return wrapper;
	}

	public FormInputValidator floatNumber (VisValidableTextField field, String errorMsg) {
		ValidatorWrapper wrapper = new ValidatorWrapper(errorMsg, Validators.FLOATS);
		field.addValidator(wrapper);
		add(field);
		return wrapper;
	}

	public FormInputValidator valueGreaterThan (VisValidableTextField field, String errorMsg, float value) {
		return valueGreaterThan(field, errorMsg, value, false);
	}

	public FormInputValidator valueLesserThan (VisValidableTextField field, String errorMsg, float value) {
		return valueLesserThan(field, errorMsg, value, false);
	}

	public FormInputValidator valueGreaterThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		ValidatorWrapper wrapper = new ValidatorWrapper(errorMsg, new GreaterThanValidator(value, equals));
		field.addValidator(wrapper);
		add(field);
		return wrapper;
	}

	public FormInputValidator valueLesserThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		ValidatorWrapper wrapper = new ValidatorWrapper(errorMsg, new LesserThanValidator(value, equals));
		field.addValidator(wrapper);
		add(field);
		return wrapper;
	}

	public FormInputValidator custom (VisValidableTextField field, FormInputValidator customValidator) {
		field.addValidator(customValidator);
		add(field);
		return customValidator;
	}

	protected void add (VisValidableTextField field) {
		fields.add(field);
		field.addListener(changeListener);
		checkAll();
	}

	private void checkAll () {
		button.setDisabled(false);
		errorMsgLabel.setText("");

		for (VisValidableTextField field : fields)
			field.validateInput();

		for (VisValidableTextField field : fields) {
			if (field.isInputValid() == false) {

				Array<InputValidator> validators = field.getValidators();
				for (InputValidator v : validators) {
					FormInputValidator validator = (FormInputValidator) v;

					if (validator.getLastResult() == false) {
						if (!(validator.isHideErrorOnEmptyInput() && field.getText().equals("")))
							errorMsgLabel.setText(validator.getErrorMsg());

						button.setDisabled(true);
						break;
					}
				}

				break;
			}
		}
	}

	public static class EmptyInputValidator extends FormInputValidator {
		public EmptyInputValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		public boolean validate (String input) {
			return !input.isEmpty();
		}
	}

	private class ChangeSharedListener extends ChangeListener {
		@Override
		public void changed (ChangeEvent event, Actor actor) {
			checkAll();
		}
	}
}
