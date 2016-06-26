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

package com.kotcrab.vis.ui.building.utilities;

/**
 * Provides static utilities for nullable objects to avoid NullPointerExceptions. Java 6 compatible, although
 * some methods might be quite useful for lambdas.
 * @author MJ
 */
public class Nullables {
	private Nullables () {
	}

	/** A simple null-check. */
	public static boolean isNull (final Object nullable) {
		return nullable == null;
	}

	/** A simple not-null-check. */
	public static boolean isNotNull (final Object nullable) {
		return nullable != null;
	}

	/**
	 * @param nullable probable null.
	 * @param alternative will be return if nullable is null.
	 */
	public static <Type> Type getOrElse (final Type nullable, final Type alternative) {
		return nullable == null ? alternative : nullable;
	}

	/**
	 * @param nullable probable null.
	 * @param command will be executed only if nullable object exists.
	 */
	public static void executeIfNotNull (final Object nullable, final Runnable command) {
		if (nullable != null) {
			command.run();
		}
	}

	/** @return true if objects are equal (using equals method) or if both are null. */
	public static boolean areEqual (final Object first, final Object second) {
		return first == second || first != null && first.equals(second);
	}

	/**
	 * @param nullables nullable objects.
	 * @return true if any of the objects is null.
	 */
	public static boolean isAnyNull (final Object... nullables) {
		for (final Object object : nullables) {
			if (object == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param nullables nullable objects.
	 * @return true if all passed objects are null.
	 */
	public static boolean areAllNull (final Object... nullables) {
		for (final Object object : nullables) {
			if (object != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param nullables nullable objects.
	 * @return true if any of the objects is not null.
	 */
	public static boolean isAnyNotNull (final Object... nullables) {
		for (final Object object : nullables) {
			if (object != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param nullables nullable objects.
	 * @return true if all passed objects are not null.
	 */
	public static boolean areAllNotNull (final Object... nullables) {
		for (final Object object : nullables) {
			if (object == null) {
				return false;
			}
		}
		return true;
	}
}
