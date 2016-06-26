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

package com.kotcrab.vis.editor.util;

/**
 * Number related utils.
 * @author Kotcrab
 */
public class NumberUtils {
	/** @return float with 2 decimal places precision */
	public static String floatToString (float d) {
		//round to four decimal places //TODO: editor setting for choosing rounding precession
		d = Math.round(d * 10000);
		d = d / 10000;
		String s = String.valueOf(d);

		return s;
	}
}
