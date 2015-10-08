/*
 * Copyright 2014-2015 See AUTHORS file.
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

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

/**
 * {@link TextFieldFilter} that only allows digits for float values.
 * @author Kotcrab
 */
public class FloatDigitsOnlyFilter implements TextFieldFilter {
	private boolean acceptNegativeValues;

	public FloatDigitsOnlyFilter (boolean acceptNegativeValues) {
		this.acceptNegativeValues = acceptNegativeValues;
	}

	@Override
	public boolean acceptChar (VisTextField textField, char c) {
		if (c == '.') return true;
		if (c == '-' && acceptNegativeValues) return true;
		return Character.isDigit(c);
	}
}
