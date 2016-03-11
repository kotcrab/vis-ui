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
 * Useful for example when you have a variable that must be changed from anonymous class or lambadas. Normally such
 * variables can't be changed because they have to be final.
 * @author Kotcrab
 * @see BiHolder
 */
public class Holder<T> {
	public T value;

	private Holder () {
	}

	private Holder (T value) {
		this.value = value;
	}

	public static <T> Holder<T> empty () {
		return new Holder<>();
	}

	public static <T> Holder<T> of (T value) {
		return new Holder<>(value);
	}

	public T get () {
		return value;
	}
}
