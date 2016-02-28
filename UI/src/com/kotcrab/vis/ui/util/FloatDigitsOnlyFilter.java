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

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

/**
 * {@link TextFieldFilter} that only allows digits for float values.
 * @author Kotcrab
 */
public class FloatDigitsOnlyFilter extends IntDigitsOnlyFilter {
	public FloatDigitsOnlyFilter (boolean acceptNegativeValues) {
		super(acceptNegativeValues);
	}

	@Override
	public boolean acceptChar (VisTextField field, char c) {
		int selectionStart = field.getSelectionStart();
		int cursorPos = field.getCursorPosition();
		String text;
		if (field.isTextSelected()) { //issue #131
			String beforeSelection = field.getText().substring(0, Math.min(selectionStart, cursorPos));
			String afterSelection = field.getText().substring(Math.max(selectionStart, cursorPos));
			text = beforeSelection + afterSelection;
		} else {
			text = field.getText();
		}

		if (c == '.' && text.contains(".") == false) return true;
		return super.acceptChar(field, c);
	}
}
