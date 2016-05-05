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
 * Basic implementation of {@link SpinnerModel} simplifying event handling for custom models.
 * @author Kotcrab
 * @see SpinnerModel
 * @see IntSpinnerModel
 * @see FloatSpinnerModel
 * @see SimpleFloatSpinnerModel
 * @see ArraySpinnerModel
 * @since 1.0.2
 */
public abstract class AbstractSpinnerModel implements SpinnerModel {
	protected Spinner spinner;

	private boolean allowRebind;
	private boolean wrap;

	public AbstractSpinnerModel (boolean allowRebind) {
		this.allowRebind = allowRebind;
	}

	@Override
	public void bind (Spinner spinner) {
		if (this.spinner != null && allowRebind == false)
			throw new IllegalStateException("this spinner model can't be reused");
		this.spinner = spinner;
	}

	/**
	 * Step model up by one. Event and spinner update will be handled by {@link AbstractSpinnerModel}.
	 * @return true if value was changed, false otherwise.
	 */
	protected abstract boolean incrementModel ();

	/**
	 * Step model down by one. Event and spinner update will be handled by {@link AbstractSpinnerModel}.
	 * @return true if value was changed, false otherwise.
	 */
	protected abstract boolean decrementModel ();

	@Override
	public final boolean increment () {
		return increment(spinner.isProgrammaticChangeEvents());
	}

	@Override
	public final boolean increment (boolean fireEvent) {
		boolean valueChanged = incrementModel();
		if (valueChanged) spinner.notifyValueChanged(fireEvent);
		return valueChanged;
	}

	@Override
	public final boolean decrement () {
		return decrement(spinner.isProgrammaticChangeEvents());
	}

	@Override
	public final boolean decrement (boolean fireEvent) {
		boolean valueChanged = decrementModel();
		if (valueChanged) spinner.notifyValueChanged(fireEvent);
		return valueChanged;
	}

	@Override
	public boolean isWrap () {
		return wrap;
	}

	@Override
	public void setWrap (boolean wrap) {
		this.wrap = wrap;
	}

	/** @return true if this model can be reused with different spinner, false otherwise */
	public boolean isAllowRebind () {
		return allowRebind;
	}

	protected void setAllowRebind (boolean allowRebind) {
		this.allowRebind = allowRebind;
	}
}
