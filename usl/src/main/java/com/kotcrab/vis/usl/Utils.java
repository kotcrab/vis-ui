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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/** Various utilises */
public class Utils {
	static String readFile (File file) {
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Reading file '" + file.getPath() + "' failed!", e);
		}
	}

	static void throwException (String exception, Token token) {
		throwException(exception, token.usl, token.i);
	}

	static void throwException (String exception, String usl, int i) {
		throw new USLException(exception + " " + "(line " + Utils.countLines(usl.substring(0, i)) + ")");
	}

	static int countLines (String str) {
		if (str == null || str.isEmpty()) return 0;

		int lines = 1;
		int pos = 0;
		while ((pos = str.indexOf("\n", pos) + 1) != 0) {
			lines++;
		}

		return lines;
	}
}
