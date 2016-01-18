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
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * Various {@link Bag} related utils
 * @author Kotcrab
 */
public class VisBagUtils {
	public static <E> Array<E> toArray (ImmutableBag<E> bag) {
		Array<E> array = new Array<E>(bag.size());

		for (E element : bag)
			array.add(element);

		return array;
	}

	public static <E> ObjectSet<E> toSet (ImmutableBag<E> bag) {
		ObjectSet<E> array = new ObjectSet<E>(bag.size());

		for (E element : bag)
			array.add(element);

		return array;
	}

	public static <E> Bag<E> toBag (Array<E> array) {
		Bag<E> bag = new Bag<E>(array.size);

		for (E element : array)
			bag.add(element);

		return bag;
	}
}
