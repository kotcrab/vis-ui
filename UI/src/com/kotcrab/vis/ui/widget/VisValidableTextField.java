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

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.InputValidator;

/** @author Kotcrab */
public class VisValidableTextField extends VisTextField {
	private Array<InputValidator> validators = new Array<InputValidator>();
	private boolean validationEnabled = true;
	private boolean programmaticChangeEvents = true;

	/**
	 * If true, a ChangeListener has been added to a field, tracking this is required for firing change events,
	 * if field does not have ChangeListener and ChangeEvent would be fired, field will lost focus
	 */
	private boolean changeListenerAdded;

	public VisValidableTextField () {
		super();
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
				if (changeListenerAdded) fire(new ChangeListener.ChangeEvent());
				return false;
			}
		});
	}

	@Override
	public void setText (String str) {
		super.setText(str);

		validateInput();
		if (programmaticChangeEvents) fire(new ChangeListener.ChangeEvent());
	}

	public void validateInput () {
		if (validationEnabled) {
			for (InputValidator validator : validators) {
				if (validator.validateInput(getText()) == false) {
					setInputValid(false);
					return;
				}
			}
		}

		// validation not enabled or validators does not returned false (input was valid)
		setInputValid(true);
	}

	public boolean addListener (EventListener listener) {
		//see changeListenerAdded comment
		if (listener instanceof ChangeListener) changeListenerAdded = true;
		return super.addListener(listener);
	}

	public boolean removeListener (EventListener listener) {
		//see changeListenerAdded comment
		boolean result = super.removeListener(listener);
		for (EventListener fieldListener : getListeners())
			if (fieldListener instanceof ChangeListener) return result;

		changeListenerAdded = false;
		return result;
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

	/**
	 * If false events font be fired when text was changed using {@link #setText(String)}
	 * @param programmaticChangeEvents enable or disable firing programmatic change events
	 */
	public void setProgrammaticChangeEvents (boolean programmaticChangeEvents) {
		this.programmaticChangeEvents = programmaticChangeEvents;
	}
}
