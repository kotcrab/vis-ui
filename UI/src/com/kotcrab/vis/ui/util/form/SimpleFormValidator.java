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

	public SimpleFormValidator (Button buttonToDisable, Label errorMsgLabel) {
		this.button = buttonToDisable;
		this.errorMsgLabel = errorMsgLabel;
	}

	public void notEmpty (VisValidableTextField field, String errorMsg) {
		field.addValidator(new EmptyInputValidator(errorMsg));
		add(field);
	}

	public void integerNumber (VisValidableTextField field, String errorMsg) {
		field.addValidator(new ValidatorWrapper(errorMsg, Validators.INTEGERS));
		add(field);
	}

	public void floatNumber (VisValidableTextField field, String errorMsg) {
		field.addValidator(new ValidatorWrapper(errorMsg, Validators.FLOATS));
		add(field);
	}

	public void valueGreaterThan (VisValidableTextField field, String errorMsg, float value) {
		valueGreaterThan(field, errorMsg, value, false);
	}

	public void valueLesserThan (VisValidableTextField field, String errorMsg, float value) {
		valueLesserThan(field, errorMsg, value, false);
	}

	public void valueGreaterThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		field.addValidator(new ValidatorWrapper(errorMsg, new GreaterThanValidator(value, equals)));
		add(field);
	}

	public void valueLesserThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		field.addValidator(new ValidatorWrapper(errorMsg, new LesserThanValidator(value, equals)));
		add(field);
	}

	public void custom (VisValidableTextField field, FormInputValidator customValidator) {
		field.addValidator(customValidator);
		add(field);
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
