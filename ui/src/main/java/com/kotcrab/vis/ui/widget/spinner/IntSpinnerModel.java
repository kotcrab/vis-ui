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

package com.kotcrab.vis.ui.widget.spinner;

import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.IntDigitsOnlyFilter;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * Spinner models allowing to select int values.
 * @author Kotcrab
 * @see SimpleFloatSpinnerModel
 * @see FloatSpinnerModel
 * @since 1.0.2
 */
public class IntSpinnerModel extends AbstractSpinnerModel {
	private BoundsValidator boundsValidator = new BoundsValidator();
	private IntDigitsOnlyFilter textFieldFilter;

	private int max;
	private int min;
	private int step;
	private int current;

	public IntSpinnerModel (int initialValue, int min, int max) {
		this(initialValue, min, max, 1);
	}

	public IntSpinnerModel (int initialValue, int min, int max, int step) {
		super(false);
		if (min > max) throw new IllegalArgumentException("min can't be > max");
		if (step <= 0) throw new IllegalArgumentException("step must be > 0");

		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;
	}

	@Override
	public void bind (Spinner spinner) {
		super.bind(spinner);

		VisValidatableTextField valueText = spinner.getTextField();
		valueText.getValidators().clear();
		valueText.addValidator(boundsValidator);
		valueText.addValidator(Validators.INTEGERS);
		valueText.setTextFieldFilter(textFieldFilter = new IntDigitsOnlyFilter(true));

		textFieldFilter.setUseFieldCursorPosition(true);
		if (min >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

		spinner.notifyValueChanged(true);
	}

	@Override
	public void textChanged () {
		String text = spinner.getTextField().getText();
		if (text.equals("")) {
			current = min;
		} else if (checkInputBounds(text)) {
			current = Integer.parseInt(text);
		}
	}

	@Override
	public boolean incrementModel () {
		if (current + step > max) {
			if (current == max) {
				if (isWrap()) {
					current = min;
					return true;
				}

				return false;
			}
			current = max;
		} else {
			current += step;
		}

		return true;
	}

	@Override
	public boolean decrementModel () {
		if (current - step < min) {
			if (current == min) {
				if (isWrap()) {
					current = max;
					return true;
				}

				return false;
			}
			current = min;
		} else {
			current -= step;
		}

		return true;
	}

	@Override
	public String getText () {
		return String.valueOf(current);
	}

	public void setValue (int newValue) {
		setValue(newValue, spinner.isProgrammaticChangeEvents());
	}

	public void setValue (int newValue, boolean fireEvent) {
		if (newValue > max) {
			current = max;
		} else if (newValue < min) {
			current = min;
		} else {
			current = newValue;
		}

		spinner.notifyValueChanged(fireEvent);
	}

	public int getValue () {
		return current;
	}

	public int getMin () {
		return min;
	}

	/** Sets min value. If current is lesser than min, the current value is set to min value. */
	public void setMin (int min) {
		if (min > max) throw new IllegalArgumentException("min can't be > max");

		this.min = min;

		if (min >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

		if (current < min) {
			current = min;
			spinner.notifyValueChanged(spinner.isProgrammaticChangeEvents());
		}
	}

	public int getMax () {
		return max;
	}

	/** Sets max value. If current is greater than max, the current value is set to max value. */
	public void setMax (int max) {
		if (min > max) throw new IllegalArgumentException("min can't be > max");

		this.max = max;

		if (current > max) {
			current = max;
			spinner.notifyValueChanged(spinner.isProgrammaticChangeEvents());
		}
	}

	public int getStep () {
		return step;
	}

	public void setStep (int step) {
		if (step <= 0) throw new IllegalArgumentException("step must be > 0");

		this.step = step;
	}

	private boolean checkInputBounds (String input) {
		try {
			float x = Integer.parseInt(input);
			return x >= min && x <= max;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private class BoundsValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			return checkInputBounds(input);
		}
	}
}
