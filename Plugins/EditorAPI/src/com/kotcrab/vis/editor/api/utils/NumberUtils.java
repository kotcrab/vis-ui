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

package com.kotcrab.vis.editor.api.utils;

public class NumberUtils {
	/** @return float with 2 decimal places precision */
	public static String floatToString (float d) {
		//fk this function
		if (d == (long) d) //if does not have decimal places
			return String.format("%d", (long) d);
		else {
			//round to two decimal places
			d = Math.round(d * 100);
			d = d / 100;
			String s = String.valueOf(d);

			//remove trailing zeros if exists
			return s.contains(".") ? s.replaceAll("0*$", "").replaceAll("\\.$", "") : s;
		}
	}
}
