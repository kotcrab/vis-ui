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

package com.kotcrab.vis.editor.util.gdx;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Array.ArrayIterable;
import com.kotcrab.vis.editor.util.CancelableConsumer;
import com.kotcrab.vis.runtime.util.ImmutableArray;

import java.util.function.Consumer;

/** @author Kotcrab */
public class ArrayUtils {
	public static boolean has (Array<?> array, Class<?> clazz) {
		for (Object obj : array) {
			if (clazz.isInstance(obj)) {
				return true;
			}
		}

		return false;
	}

	public static boolean has (ImmutableArray<?> array, Class<?> clazz) {
		for (Object obj : array) {
			if (clazz.isInstance(obj)) {
				return true;
			}
		}

		return false;
	}

	public static <BaseType> void stream (Array<BaseType> array, Consumer<BaseType> consumer) {
		for (BaseType obj : new ArrayIterable<>(array)) {
			consumer.accept(obj);
		}
	}

	public static <BaseType, RequiredType> void stream (Array<BaseType> array, Class<RequiredType> classFilter, Consumer<RequiredType> consumer) {
		for (BaseType obj : new ArrayIterable<>(array)) {
			if (classFilter.isInstance(obj)) {
				consumer.accept((RequiredType) obj);
			}
		}
	}

	public static <BaseType> void cancelableStream (Array<BaseType> array, CancelableConsumer<BaseType> consumer) {
		for (BaseType obj : new ArrayIterable<>(array)) {
			if (consumer.accept(obj))
				break;
		}
	}

	public static <BaseType, RequiredType> void cancelableStream (Array<BaseType> array, Class<RequiredType> classFilter, CancelableConsumer<RequiredType> consumer) {
		for (BaseType obj : new ArrayIterable<>(array)) {
			if (classFilter.isInstance(obj)) {
				if (consumer.accept((RequiredType) obj))
					break;
			}
		}
	}
}
