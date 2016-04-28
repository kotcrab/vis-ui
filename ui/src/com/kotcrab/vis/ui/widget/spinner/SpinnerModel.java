package com.kotcrab.vis.ui.widget.spinner;

/**
 * Classes implementing this interface represent model that can be used with {@link Spinner}. Model defines what is scrolled
 * in spinner (eg. int numbers, floats or some arbitrary strings), set-ups input validation and updates it's value if user
 * changed text in spinner value text field.
 * @author Kotcrab
 * @see IntSpinnerModel
 * @see FloatSpinnerModel
 * @see ArraySpinnerModel
 * @since 1.0.2
 */
public interface SpinnerModel {
	/**
	 * Called when model is assigned to {@link Spinner}. When this is called Spinner has been initialised so it's safe to
	 * do operation on it such as adding custom validators to text field.
	 * <p>
	 * If this model can't be reused then in this function it should verify that it is not being bound for the second time.
	 * <p>
	 * After model has finished it's setup it should call {@link Spinner#notifyValueChanged(boolean)} with true to perform
	 * first update and set initial spinner value.
	 * @param spinner that this model was assigned to
	 */
	void bind (Spinner spinner);

	/**
	 * Called when spinner text has changed. Usually this is the moment when model has to update it's current value variable.
	 * If input is invalid when this it called then it should simply be ignored. If field loses focus while it is in
	 * invalid state then last valid value will be automatically restored.
	 */
	void textChanged ();

	/**
	 * Steps model up by one. Depending of the implementation this could move model to next item or increment it's value by
	 * arbitrary amount. There is no need to call {@link Spinner#notifyValueChanged(boolean)} as this is handled by spinner itself.
	 * @return true when value was changed, false otherwise
	 */
	boolean increment ();

	/**
	 * Steps model down by one. Depending of the implementation this could move model to previous item or decrement it's value by
	 * arbitrary amount. There is no need to call {@link Spinner#notifyValueChanged(boolean)} as this is handled by spinner itself.
	 * @return true when value was changed, false otherwise
	 */
	boolean decrement ();

	/** @return text representation of current model value */
	String getText ();
}
