/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.util;

import com.kotcrab.vis.ui.InputValidator;

public class Validators {
	public static final InputValidator integers = new IntegerValidator();

	public static class IntegerValidator implements InputValidator {
		@Override
		public boolean validateInput (String input) {
			if (input.equals("")) return true;

			try {
				Integer.valueOf(input);
			} catch (NumberFormatException ex) {
				return false;
			}
			return true;
		}
	}

	public static class GreaterThanValidator implements InputValidator {
		private float greaterThan;

		public GreaterThanValidator (float greaterThan) {
			this.greaterThan = greaterThan;
		}

		@Override
		public boolean validateInput (String input) {
			try {
				int value = Integer.valueOf(input);
				if (value > greaterThan)
					return true;
				else
					return false;
			} catch (NumberFormatException ex) {
				return false;
			}
		}
	}
}
