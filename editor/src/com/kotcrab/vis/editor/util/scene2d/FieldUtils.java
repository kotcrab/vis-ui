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

package com.kotcrab.vis.editor.util.scene2d;

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;

/**
 * {@link VisTextField}/{@link VisValidatableTextField} related utils.
 * @author Kotcrab
 */
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

	public static float getFloat (VisValidatableTextField field, float valueIfError) {
		if (field.isInputValid() == false) return valueIfError;

		try {
			return Float.parseFloat(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}

	public static int getInt (VisValidatableTextField field, int valueIfError) {
		if (field.isInputValid() == false) return valueIfError;

		try {
			return Integer.valueOf(field.getText());
		} catch (NumberFormatException ex) {
			return valueIfError;
		}
	}
}
