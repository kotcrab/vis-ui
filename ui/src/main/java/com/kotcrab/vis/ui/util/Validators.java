/*
 * Copyright 2014-2017 See AUTHORS file.
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

import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * Provides premade validators that can be used with for example with {@link VisValidatableTextField} or {@link Dialogs}
 * when displaying input dialogs.
 * @author Kotcrab
 */
public class Validators {
	/** Shared static instance of {@link IntegerValidator}. Can be safely reused. */
	public static final IntegerValidator INTEGERS = new IntegerValidator();
	/** Shared static instance of {@link FloatValidator}. Can be safely reused. */
	public static final FloatValidator FLOATS = new FloatValidator();

	/** Validates whether input is an integer number. You should use shared instance {@link Validators#INTEGERS}. */
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

	/** Validates whether input is a float number. You should use shared instance {@link Validators#FLOATS}. */
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

	/** Validates whether input is lesser (alternatively lesser or equal) than provided number. */
	public static class LesserThanValidator implements InputValidator {
		private float lesserThan;
		private boolean equals;

		public LesserThanValidator (float lesserThan) {
			this.lesserThan = lesserThan;
		}

		/** @param inputCanBeEqual if true &lt;= comparison will be used, if false &lt; will be used. */
		public LesserThanValidator (float lesserThan, boolean inputCanBeEqual) {
			this.lesserThan = lesserThan;
			this.equals = inputCanBeEqual;
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

		/*** @param equals if true &lt;= comparison will be used, if false &lt; will be used. */
		public void setUseEquals (boolean equals) {
			this.equals = equals;
		}

		public void setLesserThan (float lesserThan) {
			this.lesserThan = lesserThan;
		}
	}

	/** Validates whether input is greater (alternatively greater or equal) than provided number. */
	public static class GreaterThanValidator implements InputValidator {
		private float greaterThan;
		private boolean useEquals;

		public GreaterThanValidator (float greaterThan) {
			this.greaterThan = greaterThan;
		}

		/** @param inputCanBeEqual if true &gt;= comparison will be used, if false &gt; will be used. */
		public GreaterThanValidator (float greaterThan, boolean inputCanBeEqual) {
			this.greaterThan = greaterThan;
			this.useEquals = inputCanBeEqual;
		}

		@Override
		public boolean validateInput (String input) {
			try {
				float value = Float.valueOf(input);
				return useEquals ? value >= greaterThan : value > greaterThan;
			} catch (NumberFormatException ex) {
				return false;
			}
		}

		/*** @param useEquals if true &gt;= comparison will be used, if false &gt; will be used. */
		public void setUseEquals (boolean useEquals) {
			this.useEquals = useEquals;
		}

		public void setGreaterThan (float greaterThan) {
			this.greaterThan = greaterThan;
		}
	}
}
