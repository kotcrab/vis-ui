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

import com.kotcrab.vis.ui.util.OsUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Jar related utils.
 * @author Kotcrab
 */
public class JarUtils {
	public static String getJarPath (Class<?> caller) {
		try {
			URL url = caller.getProtectionDomain().getCodeSource().getLocation();
			String path = URLDecoder.decode(url.getFile(), "UTF-8");

			// remove jar name from path
			if (OsUtils.isWindows())
				path = path.substring(1, path.lastIndexOf('/')); // cut first '/' for Windows
			else
				path = path.substring(0, path.lastIndexOf('/'));

			if (path.endsWith("target/classes")) //launched from ide, remove classes from path
				path = path.substring(0, path.length() - "/classes".length());

			path = path.replace("/", File.separator);
			return path + File.separator;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Failed to get jar path due to unsupported encoding!", e);
		}
	}
}
