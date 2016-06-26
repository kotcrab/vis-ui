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

/**
 * Path related utils.
 * @author Kotcrab
 */
public class PathUtils {
	/**
	 * Removes the first directory separator from a path
	 * <p>
	 * gdx/asset.png -> asset.png
	 * @param path path where the the first directory separator is removed from
	 * @return the path without the parts that are before the separator
	 */
	public static String removeFirstSeparator (String path) {
		return path.substring(path.indexOf('/') + 1);
	}

	public static String removeExtension (String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return filename;
		}
		return filename.substring(0, index);
	}

	public static int indexOfExtension (String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf('.');
		int lastSeparator = indexOfLastSeparator(filename);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	public static int indexOfLastSeparator (String filename) {
		if (filename == null) {
			return -1;
		}
		int lastUnixPos = filename.lastIndexOf('/');
		int lastWindowsPos = filename.lastIndexOf('\\');
		return Math.max(lastUnixPos, lastWindowsPos);
	}
}
