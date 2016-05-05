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

/**
 * Classes implementing this interface represent model that can be used with {@link Spinner}. Model defines what is scrolled
 * in spinner (eg. int numbers, floats or some arbitrary strings), set-ups input validation and updates it's value if user
 * changed text in spinner value text field.
 * <p>
 * Classes wanting to implement this interface should inherit from {@link AbstractSpinnerModel} to simplify event handling.
 * @author Kotcrab
 * @see AbstractSpinnerModel
 * @see IntSpinnerModel
 * @see FloatSpinnerModel
 * @see SimpleFloatSpinnerModel
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
	 * invalid state then last valid value will be automatically restored. This should NOT call {@link Spinner#notifyValueChanged(boolean)}.
	 */
	void textChanged ();

	/**
	 * Steps model up by one. Depending of the implementation this could move model to next item or increment it's value by
	 * arbitrary amount. Implementation class MUST call {@link Spinner#notifyValueChanged(boolean)} with fireEvent param set to
	 * {@link Spinner#isProgrammaticChangeEvents()}
	 * <p>
	 * @return true when value was changed, false otherwise
	 */
	boolean increment ();

	/**
	 * Steps model up by one. Depending of the implementation this could move model to next item or increment it's value by
	 * arbitrary amount. Implementation class MUST call {@link Spinner#notifyValueChanged(boolean)} using fireEvent param as argument.
	 * <p>
	 * @return true when value was changed, false otherwise
	 */
	boolean increment (boolean fireEvent);

	/**
	 * Steps model down by one. Depending of the implementation this could move model to previous item or decrement it's value by
	 * arbitrary amount. Implementation class MUST call {@link Spinner#notifyValueChanged(boolean)} with fireEvent param set to
	 * {@link Spinner#isProgrammaticChangeEvents()}
	 * <p>
	 * @return true when value was changed, false otherwise
	 */
	boolean decrement ();

	/**
	 * Steps model down by one. Depending of the implementation this could move model to previous item or decrement it's value by
	 * arbitrary amount. Implementation class MUST call {@link Spinner#notifyValueChanged(boolean)} using fireEvent param as argument.
	 * <p>
	 * @return true when value was changed, false otherwise
	 */
	boolean decrement (boolean fireEvent);

	/**
	 * Allows to enable model wrapping: if last element of model is reached and {@link #decrement()} was called then it
	 * will be looped to first element. Same applies for last element and {@link #increment()}
	 * @param wrap whether to wrap this model or not
	 */
	void setWrap (boolean wrap);

	/** @return true if model wrapping is enabled, false otherwise. See {@link #setWrap(boolean)} */
	boolean isWrap ();

	/** @return text representation of current model value */
	String getText ();
}
