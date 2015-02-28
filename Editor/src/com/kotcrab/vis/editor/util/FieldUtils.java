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

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidableTextField;

public class FieldUtils {
	public static float getFloat (VisTextField field, float valueIfError) {
		try {
			return Float.parseFloat(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}

	public static int getInt (VisTextField field, int valueIfError) {
		try {
			return Integer.valueOf(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}

	public static float getFloat (VisValidableTextField field, float valueIfError) {
		if (field.isInputValid() == false) return valueIfError;

		try {
			return Float.parseFloat(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}

	public static int getInt (VisValidableTextField field, int valueIfError) {
		if (field.isInputValid() == false) return valueIfError;

		try {
			return Integer.valueOf(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}
}
