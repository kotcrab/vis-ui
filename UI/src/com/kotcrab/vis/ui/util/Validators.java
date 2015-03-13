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

package com.kotcrab.vis.ui.util;

import com.kotcrab.vis.ui.InputValidator;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

/**
 * Provides premade validators that can be used with {@link VisValidableTextField} or {@link DialogUtils} when displaying
 * input dialogs
 * @author Kotcrab
 */
public class Validators {
	public static final IntegerValidator INTEGERS = new IntegerValidator();
	public static final FloatValidator FLOATS = new FloatValidator();

	public static class IntegerValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			try {
				Integer.parseInt(input);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

	public static class FloatValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			try {
				Float.parseFloat(input);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

	public static class LesserThanValidator implements InputValidator {
		private float lesserThan;
		private boolean equals;

		public LesserThanValidator (float lesserThan) {
			this.lesserThan = lesserThan;
		}

		public LesserThanValidator (float lesserThan, boolean equals) {
			this.lesserThan = lesserThan;
			this.equals = equals;
		}

		@Override
		public boolean validateInput (String input) {
			try {
				float value = Float.valueOf(input);
				return equals ? value <= lesserThan : value < lesserThan;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
	}

	public static class GreaterThanValidator implements InputValidator {
		private float greaterThan;
		private boolean equals;

		public GreaterThanValidator (float greaterThan) {
			this.greaterThan = greaterThan;
		}

		public GreaterThanValidator (float greaterThan, boolean equals) {
			this.greaterThan = greaterThan;
			this.equals = equals;
		}

		@Override
		public boolean validateInput (String input) {
			try {
				float value = Float.valueOf(input);
				return equals ? value >= greaterThan : value > greaterThan;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
	}
}
