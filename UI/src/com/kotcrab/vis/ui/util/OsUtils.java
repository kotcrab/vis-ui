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

package com.kotcrab.vis.ui.util;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

/** @author Kotcrab
 * @author Simon Gerst */
public class OsUtils {
	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static final boolean windows = OS.contains("win");
	private static final boolean mac = OS.contains("mac");
	private static final boolean unix = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");

	/** @return {@code true} if the current OS is Windows */
	public static boolean isWindows () {
		return windows;
	}

	/** @return {@code true} if the current OS is Mac */
	public static boolean isMac () {
		return mac;
	}

	/** @return {@code true} if the current OS is Unix */
	public static boolean isUnix () {
		return unix;
	}

	/** @return {@code true} if the current OS is iOS */
	public static boolean isIos () {
		return Gdx.app.getType().equals(ApplicationType.iOS);
	}

	/** @return {@code true} if the current OS is Android */
	public static boolean isAndroid () {
		return Gdx.app.getType().equals(ApplicationType.Android);
	}

	/** Returns the Android API level it's basically the same as android.os.Build.VERSION.SDK_INT
	 * @return the API level. Returns 0 if the current OS isn't Android */
	public static int getAndroidApiLevel () {
		if (isAndroid()) {
			return Gdx.app.getVersion();
		} else {
			return 0;
		}
	}

}
