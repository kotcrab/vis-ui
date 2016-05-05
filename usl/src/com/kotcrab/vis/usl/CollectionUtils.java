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

package com.kotcrab.vis.usl;

import java.util.*;

/** CollectionUtils copied from Apache Commons */
public class CollectionUtils {

	/**
	 * Returns <tt>true</tt> iff the given {@link Collection}s contain
	 * exactly the same elements with exactly the same cardinality.
	 * <p>
	 * That is, iff the cardinality of <i>e</i> in <i>a</i> is
	 * equal to the cardinality of <i>e</i> in <i>b</i>,
	 * for each element <i>e</i> in <i>a</i> or <i>b</i>.
	 */
	public static boolean isEqualCollection (final Collection a, final Collection b) {
		if (a.size() != b.size()) {
			return false;
		} else {
			Map mapa = getCardinalityMap(a);
			Map mapb = getCardinalityMap(b);
			if (mapa.size() != mapb.size()) {
				return false;
			} else {
				Iterator it = mapa.keySet().iterator();
				while (it.hasNext()) {
					Object obj = it.next();
					if (getFreq(obj, mapa) != getFreq(obj, mapb)) {
						return false;
					}
				}
				return true;
			}
		}
	}

	private static final int getFreq (final Object obj, final Map freqMap) {
		try {
			return ((Integer) (freqMap.get(obj))).intValue();
		} catch (NullPointerException e) {
			// ignored
		} catch (NoSuchElementException e) {
			// ignored
		}
		return 0;
	}

	/**
	 * Returns a {@link Map} mapping each unique element in
	 * the given {@link Collection} to an {@link Integer}
	 * representing the number of occurances of that element
	 * in the {@link Collection}.
	 * An entry that maps to <tt>null</tt> indicates that the
	 * element does not appear in the given {@link Collection}.
	 */
	public static Map getCardinalityMap (final Collection col) {
		HashMap count = new HashMap();
		Iterator it = col.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			Integer c = (Integer) (count.get(obj));
			if (null == c) {
				count.put(obj, new Integer(1));
			} else {
				count.put(obj, new Integer(c.intValue() + 1));
			}
		}
		return count;
	}
}
