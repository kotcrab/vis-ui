package com.kotcrab.vis.ui.widget.spinner;

import com.kotcrab.vis.ui.util.*;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

import java.math.BigDecimal;

/**
 * Spinner model allowing to select float values. Uses {@link BigDecimal} to support high precession and support large numbers.
 * Consider using {@link SimpleFloatSpinnerModel} when such high precision is not needed as it will be faster and simpler to use.
 * @author Kotcrab
 * @see FloatSpinnerModel
 * @see IntSpinnerModel
 * @since 1.0.2
 */
public class FloatSpinnerModel implements SpinnerModel {
	private Spinner spinner;

	private InputValidator boundsValidator = new BoundsValidator();
	private NumberDigitsTextFieldFilter textFieldFilter;

	private BigDecimal max;
	private BigDecimal min;
	private BigDecimal step;
	private BigDecimal current;
	private int scale = 0;

	public FloatSpinnerModel (String initialValue, String min, String max) {
		this(initialValue, min, max, "1", 1);
	}

	public FloatSpinnerModel (String initialValue, String min, String max, String step) {
		this(initialValue, min, max, step, 1);
	}

	public FloatSpinnerModel (String initialValue, String min, String max, String step, int scale) {
		this(new BigDecimal(initialValue), new BigDecimal(min), new BigDecimal(max), new BigDecimal(step), scale);
	}

	public FloatSpinnerModel (BigDecimal initialValue, BigDecimal min, BigDecimal max, BigDecimal step, int scale) {
		this.current = initialValue;
		this.max = max;
		this.min = min;
		this.step = step;
		this.scale = scale;

		if (this.min.compareTo(this.max) > 0) throw new IllegalArgumentException("min can't be > max");
		if (this.step.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("step must be > 0");
		if (scale < 0) throw new IllegalArgumentException("scale must be >= 0");
	}

	@Override
	public void bind (Spinner spinner) {
		if (this.spinner != null)
			throw new IllegalStateException("FastFloatSpinnerModel can be only used by single instance of Spinner");
		this.spinner = spinner;
		setScale(scale);
		spinner.notifyValueChanged(true);
	}

	@Override
	public void textChanged () {
		String text = spinner.getTextField().getText();
		if (text.equals("")) {
			current = min.setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else if (checkInputBounds(text)) {
			current = new BigDecimal(text);
		}
	}

	@Override
	public void increment () {
		if (current.add(step).compareTo(max) > 0) {
			current = max.setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else {
			current = current.add(step);
		}
	}

	@Override
	public void decrement () {
		if (current.subtract(step).compareTo(min) < 0) {
			current = min.setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else {
			current = current.subtract(step);
		}
	}

	@Override
	public String getText () {
		return current.toPlainString();
	}

	public int getScale () {
		return scale;
	}

	/**
	 * Sets scale of this selector. Scale defines how many digits after decimal point can be entered. By default
	 * this is set to 0, meaning that only integers are allowed. Setting scale to 1 would allow 0.0, scale = 2 would
	 * allow 0.00 and etc.
	 */
	public void setScale (final int scale) {
		if (scale < 0) throw new IllegalStateException("Scale can't be < 0");
		this.scale = scale;
		current = current.setScale(scale, BigDecimal.ROUND_HALF_UP);

		VisValidatableTextField valueText = spinner.getTextField();
		valueText.getValidators().clear();
		valueText.addValidator(boundsValidator); //Both need the bounds check
		if (scale == 0) {
			valueText.addValidator(Validators.INTEGERS);
			valueText.setTextFieldFilter(textFieldFilter = new IntDigitsOnlyFilter(true));
		} else {
			valueText.addValidator(Validators.FLOATS);
			valueText.addValidator(new InputValidator() {
				@Override
				public boolean validateInput (String input) {
					int dotIndex = input.indexOf('.');
					if (dotIndex == -1) return true;
					return input.length() - input.indexOf('.') - 1 <= scale;
				}
			});
			valueText.setTextFieldFilter(textFieldFilter = new FloatDigitsOnlyFilter(true));
		}

		textFieldFilter.setUseFieldCursorPosition(true);
		if (min.compareTo(BigDecimal.ZERO) >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

	}

	public void setValue (BigDecimal newValue) {
		setValue(newValue, spinner.isProgrammaticChangeEvents());
	}

	public void setValue (BigDecimal newValue, boolean fireEvent) {
		if (newValue.compareTo(max) > 0) {
			current = max.setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else if (newValue.compareTo(min) < 0) {
			current = min.setScale(scale, BigDecimal.ROUND_HALF_UP);
		} else {
			current = newValue.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}

		spinner.notifyValueChanged(fireEvent);
	}

	public BigDecimal getValue () {
		return current;
	}

	public BigDecimal getMin () {
		return min;
	}

	/** Sets min value. If current is lesser than min, the current value is set to min value */
	public void setMin (BigDecimal min) {
		if (min.compareTo(max) > 0) throw new IllegalArgumentException("min can't be > max");

		this.min = min;

		if (min.compareTo(BigDecimal.ZERO) >= 0) {
			textFieldFilter.setAcceptNegativeValues(false);
		} else {
			textFieldFilter.setAcceptNegativeValues(true);
		}

		if (current.compareTo(min) < 0) {
			current = min.setScale(scale, BigDecimal.ROUND_HALF_UP);
			spinner.notifyValueChanged(true);
		}
	}

	public BigDecimal getMax () {
		return max;
	}

	/** Sets max value. If current is greater than max, the current value is set to max value. */
	public void setMax (BigDecimal max) {
		if (min.compareTo(max) > 0) throw new IllegalArgumentException("min can't be > max");

		this.max = max;

		if (current.compareTo(max) > 0) {
			current = max.setScale(scale, BigDecimal.ROUND_HALF_UP);
			spinner.notifyValueChanged(true);
		}
	}

	public BigDecimal getStep () {
		return step;
	}

	public void setStep (BigDecimal step) {
		if (step.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("step must be > 0");
		this.step = step;
	}

	private boolean checkInputBounds (String input) {
		try {
			BigDecimal x = new BigDecimal(input);
			return x.compareTo(min) >= 0 && x.compareTo(max) <= 0;
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
