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

package com.kotcrab.vis.runtime.util;

import com.artemis.utils.Bag;

import java.util.Arrays;

/**
 * Various bag related utilities.
 * @author Daan van Yperen
 */
public class BagUtils {

	/**
	 * Sort bag.
	 * Entities must implement Sortable.
	 * <p>
	 * Be aware that bags require a resort when you
	 * remove entries.
	 */
	public static void sort (Bag bag) {
		if (!bag.isEmpty()) {
			Arrays.sort(bag.getData(), 0, bag.size());
		}
	}

}
