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

package com.kotcrab.vis.ui.util;

import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

/**
 * Base class for number digits text field filters. Filters extending this class must handle disabling entering
 * negative number values and using cursor position to prevent typing minus in wrong place.
 * @author Kotcrab
 * @see IntDigitsOnlyFilter
 * @see FloatDigitsOnlyFilter
 */
public abstract class NumberDigitsTextFieldFilter implements TextFieldFilter {
	private boolean acceptNegativeValues;
	private boolean useFieldCursorPosition;

	public NumberDigitsTextFieldFilter (boolean acceptNegativeValues) {
		this.acceptNegativeValues = acceptNegativeValues;
	}

	public boolean isAcceptNegativeValues () {
		return acceptNegativeValues;
	}

	public void setAcceptNegativeValues (boolean acceptNegativeValues) {
		this.acceptNegativeValues = acceptNegativeValues;
	}

	public boolean isUseFieldCursorPosition () {
		return useFieldCursorPosition;
	}

	/**
	 * @param useFieldCursorPosition if true this filter will use current field cursor position to prevent typing minus sign
	 * in wrong place. This is disabled by default. If you enable this feature you must ensure that field cursor position is
	 * set to 0 when you change text programmatically. Non zero cursor position can happen when you are changing text when
	 * field still has user focus.
	 */
	public void setUseFieldCursorPosition (boolean useFieldCursorPosition) {
		this.useFieldCursorPosition = useFieldCursorPosition;
	}
}
