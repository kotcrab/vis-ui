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

package com.kotcrab.vis.ui.widget;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.InputValidator;

public class VisValidableTextField extends VisTextField {
	String previousText = "";
	private Array<InputValidator> validators = new Array<InputValidator>();
	private boolean validationEnabled = true;
	private boolean disregardInvalidInput = false;

	public VisValidableTextField () {
		super();
		init();
	}

	/**
	 * See {@link #setDisregardInvalidInput(boolean)}
	 */
	public VisValidableTextField (boolean disregardInvalidInput) {
		super();
		this.disregardInvalidInput = disregardInvalidInput;
		init();
	}

	public VisValidableTextField (String text) {
		super(text);
		init();
	}

	public VisValidableTextField (InputValidator validator) {
		super();
		addValidator(validator);
		init();
	}

	/**
	 * See {@link #setDisregardInvalidInput(boolean)}
	 */
	public VisValidableTextField (InputValidator validator, boolean disregardInvalidInput) {
		super();
		addValidator(validator);
		this.disregardInvalidInput = disregardInvalidInput;
		init();
	}

	public VisValidableTextField (InputValidator... validators) {
		super();
		for (InputValidator validator : validators)
			addValidator(validator);

		init();
	}

	private void init () {
		addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char character) {
				validateInput();
				fire(new ChangeListener.ChangeEvent());
				previousText = getText();
				return false;
			}
		});
	}

	@Override
	public void setText (String str) {
		super.setText(str);

		if (validators != null) validateInput();
		fire(new ChangeListener.ChangeEvent());

		previousText = text;
	}

	public void validateInput () {
		if (validationEnabled) {
			for (InputValidator validator : validators) {
				if (validator.validateInput(getText()) == false) {
					if (disregardInvalidInput)
						restorePreviousText();
					else
						setInputValid(false);

					return;
				}
			}
		}

		// validation not enabled or validators does not returned false (input was valid)
		setInputValid(true);
	}

	private void restorePreviousText () {
		int cursorPos = getCursorPosition() - 1;

		super.setText(previousText);

		if (cursorPos >= 0)
			setCursorPosition(cursorPos);
	}

	public void addValidator (InputValidator validator) {
		validators.add(validator);
		validateInput();
	}

	public Array<InputValidator> getValidators () {
		return validators;
	}

	/**
	 * Returns first validator, or null if there is no added validators. Please note that is this field has more than one
	 * validator, this method will always return first
	 */
	public InputValidator getValidator () {
		return validators.size == 0 ? null : validators.get(0);
	}

	public boolean isValidationEnabled () {
		return validationEnabled;
	}

	/**
	 * Enables or disables validation, after changing this setting validateInput() is called, if validationEnabled == false then
	 * field is marked as valid otherwise standard validation is performed
	 * @param validationEnabled enable or disable validation
	 */
	public void setValidationEnabled (boolean validationEnabled) {
		this.validationEnabled = validationEnabled;
		validateInput();
	}

	public boolean isDisregardInvalidInput () {
		return disregardInvalidInput;
	}

	/**
	 * Enables or disables input disregard, if true, user can't input something that is not valid,
	 * for example is field validator only allow to input number, trying to input letter or other
	 * non-number character won't do anything.
	 * <p/>
	 * Changing this does not affect already typed text
	 * @param disregardInvalidInput if true input disregard will be enalbed false otherwise
	 */
	public void setDisregardInvalidInput (boolean disregardInvalidInput) {
		this.disregardInvalidInput = disregardInvalidInput;
	}
}
