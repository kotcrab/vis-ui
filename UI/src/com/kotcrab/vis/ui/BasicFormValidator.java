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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.util.Validators.GreaterThanValidator;
import com.kotcrab.vis.ui.util.Validators.LesserThanValidator;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

/**
 * BasicFormValidator is GWT compatible and does not provide fileExists methods, if you are not using GWT use
 * {@link FormValidator}
 * @author Pawel Pastuszak
 * @see {@link FormValidator}
 */
public class BasicFormValidator {
	private ChangeSharedListener changeListener = new ChangeSharedListener();
	private Array<VisValidableTextField> fields = new Array<VisValidableTextField>();

	private Button button;
	private Label errorMsgLabel;

	public BasicFormValidator (Button buttonToDisable, Label errorMsgLabel) {
		this.button = buttonToDisable;
		this.errorMsgLabel = errorMsgLabel;
	}

	public void notEmpty (VisValidableTextField field, String errorMsg) {
		field.addValidator(new EmptyInputValidator(errorMsg));
		add(field);
	}

	public void integerNumber (VisValidableTextField field, String errorMsg) {
		field.addValidator(new IntegerFormInputValidator(errorMsg));
		add(field);
	}

	public void floatNumber (VisValidableTextField field, String errorMsg) {
		field.addValidator(new FloatFormInputValidator(errorMsg));
		add(field);
	}


	public void valueGreaterThan (VisValidableTextField field, String errorMsg, float value) {
		valueGreaterThan(field, errorMsg, value, false);
	}

	public void valueLesserThan (VisValidableTextField field, String errorMsg, float value) {
		valueLesserThan(field, errorMsg, value, false);
	}

	public void valueGreaterThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		field.addValidator(new GreaterThanFormInputValidator(errorMsg, value, equals));
		add(field);
	}

	public void valueLesserThan (VisValidableTextField field, String errorMsg, float value, boolean equals) {
		field.addValidator(new LesserThanFormInputValidator(errorMsg, value, equals));
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

					if (validator.getResult() == false) {
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
		public boolean validateInput (String input) {
			setResult(!input.isEmpty());
			return super.validateInput(input);
		}
	}

	public static class IntegerFormInputValidator extends FormInputValidator {
		public IntegerFormInputValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		public boolean validateInput (String input) {
			setResult(Validators.integers.validateInput(input));
			return super.validateInput(input);
		}
	}

	public static class FloatFormInputValidator extends FormInputValidator {
		public FloatFormInputValidator (String errorMsg) {
			super(errorMsg);
		}

		@Override
		public boolean validateInput (String input) {
			setResult(Validators.floats.validateInput(input));
			return super.validateInput(input);
		}
	}

	public static class GreaterThanFormInputValidator extends FormInputValidator {
		private GreaterThanValidator validator;

		public GreaterThanFormInputValidator (String errorMsg, float value, boolean equals) {
			super(errorMsg);
			validator = new GreaterThanValidator(value, equals);
		}

		@Override
		public boolean validateInput (String input) {
			setResult(validator.validateInput(input));
			return super.validateInput(input);
		}
	}

	public static class LesserThanFormInputValidator extends FormInputValidator {
		private LesserThanValidator validator;

		public LesserThanFormInputValidator (String errorMsg, float value, boolean equals) {
			super(errorMsg);
			validator = new LesserThanValidator(value, equals);
		}

		@Override
		public boolean validateInput (String input) {
			setResult(validator.validateInput(input));
			return super.validateInput(input);
		}
	}

	private class ChangeSharedListener extends ChangeListener {
		@Override
		public void changed (ChangeEvent event, Actor actor) {
			checkAll();
		}
	}
}
