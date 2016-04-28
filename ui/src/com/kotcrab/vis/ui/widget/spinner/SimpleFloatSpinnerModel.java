package com.kotcrab.vis.ui.widget.spinner;

import com.kotcrab.vis.ui.util.*;
import com.kotcrab.vis.ui.widget.NumberSelector;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import java.math.BigDecimal;

/**
 * Spinner models allowing to select float values. Uses float to store values, good for small numbers
 * with low precession. If high precession is required or very big numbers are used then {@link FloatSpinnerModel} should be used.
 * If only ints are needed then {@link IntSpinnerModel} should be used.
 * <p>
 * Implements the same behaviour as deprecated {@link NumberSelector}.
 * @author Kotcrab
 * @see FloatSpinnerModel
 * @see IntSpinnerModel
 * @since 1.0.2
 */
public class SimpleFloatSpinnerModel implements SpinnerModel {
	private Spinner spinner;

	private InputValidator boundsValidator = new BoundsValidator();
	private NumberDigitsTextFieldFilter textFieldFilter;

	private float max;
	private float min;
	private float step;
	private float current;
	private int precision = 0;

	public SimpleFloatSpinnerModel (float initialValue, float min, float max) {
		this(initialValue, min, max, 1, 1);
	}

	public SimpleFloatSpinnerModel (float initialValue, float min, float max, float step) {
		this(initialValue, min, max, step, 1);
	}

	public SimpleFloatSpinnerModel (float initialValue, float min, float max, float step, int precision) {
		if (min > max) throw new IllegalArgumentException("min can't be > max");
		if (step <= 0) throw new IllegalArgumentException("step must be > 0");
		if (precision < 0) throw new IllegalArgumentException("precision must be >= 0");

		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;
		this.precision = precision;
	}

	@Override
	public void bind (Spinner spinner) {
		if (this.spinner != null)
			throw new IllegalStateException("FastFloatSpinnerModel can be only used by single instance of Spinner");
		this.spinner = spinner;
		setPrecision(precision);
		spinner.notifyValueChanged(true);
	}

	@Override
	public void textChanged () {
		String text = spinner.getTextField().getText();
		if (text.equals("")) {
			current = min;
		} else if (checkInputBounds(text)) {
			current = Float.parseFloat(text);
		}
	}

	@Override
	public void increment () {
		if (current + step > max) {
			this.current = max;
		} else {
			this.current += step;
		}
	}

	@Override
	public void decrement () {
		if (current - step < min) {
			this.current = min;
		} else {
			this.current -= step;
		}
	}

	@Override
	public String getText () {
		if (precision >= 1) {
			//dealing with float rounding errors
			BigDecimal bd = new BigDecimal(String.valueOf(current));
			bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
			return String.valueOf(bd.floatValue());
		} else {
			return String.valueOf((int) current);
		}
	}

	public int getPrecision () {
		return precision;
	}

	/**
	 * Sets precision of this selector. Precision defines how many digits after decimal point can be entered. By default
	 * this is set to 0, meaning that only integers are allowed. Setting precision to 1 would allow 0.0, precision = 2 would
	 * allow 0.00 and etc.
	 */
	public void setPrecision (final int precision) {
		if (precision < 0) throw new IllegalStateException("Precision can't be < 0");
		this.precision = precision;

		VisValidatableTextField valueText = spinner.getTextField();
		valueText.getValidators().clear();
		valueText.addValidator(boundsValidator); //Both need the bounds check
		if (precision == 0) {
			valueText.addValidator(Validators.INTEGERS);
			valueText.setTextFieldFilter(textFieldFilter = new IntDigitsOnlyFilter(true));
		} else {
			valueText.addValidator(Validators.FLOATS);
			valueText.addValidator(new InputValidator() {
				@Override
				public boolean validateInput (String input) {
					int dotIndex = input.indexOf('.');
					if (dotIndex == -1) return true;
					return input.length() - input.indexOf('.') - 1 <= precision;
				}
			});
			valueText.setTextFieldFilter(textFieldFilter = new FloatDigitsOnlyFilter(true));
		}

		textFieldFilter.setUseFieldCursorPosition(true);
		if (min >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

	}

	public void setValue (float newValue) {
		setValue(newValue, spinner.isProgrammaticChangeEvents());
	}

	public void setValue (float newValue, boolean fireEvent) {
		if (newValue > max) {
			current = max;
		} else if (newValue < min) {
			current = min;
		} else {
			current = newValue;
		}

		spinner.notifyValueChanged(fireEvent);
	}

	public float getValue () {
		return current;
	}

	public float getMin () {
		return min;
	}

	/** Sets min value, if current is lesser than min, the current value is set to min value */
	public void setMin (float min) {
		if (min > max) throw new IllegalArgumentException("min can't be > max");

		this.min = min;

		if (min >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

		if (current < min) {
			current = min;
			spinner.notifyValueChanged(true);
		}
	}

	public float getMax () {
		return max;
	}

	/** Sets max value. If current is greater than max, the current value is set to max value. */
	public void setMax (float max) {
		if (min > max) throw new IllegalArgumentException("min can't be > max");

		this.max = max;

		if (current > max) {
			current = max;
			spinner.notifyValueChanged(true);
		}
	}

	public float getStep () {
		return step;
	}

	public void setStep (float step) {
		if (step <= 0) throw new IllegalArgumentException("step must be > 0");

		this.step = step;
	}

	private boolean checkInputBounds (String input) {
		try {
			float x = Float.parseFloat(input);
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
