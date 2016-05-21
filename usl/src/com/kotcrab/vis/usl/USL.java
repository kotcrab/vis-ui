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

/** Allows to use USL from code. */
public class USL {
	private static final String USER_HOME_PATH = System.getProperty("user.home") + File.separator;
	public static final String USL_FOLDER_PATH = USER_HOME_PATH + ".usl" + File.separator;
	public static final String CACHE_FOLDER_PATH = USL_FOLDER_PATH + "cache" + File.separator;
	public static final String TMP_FOLDER_PATH = USL_FOLDER_PATH + "tmp" + File.separator;

	public static String parse (File uslFile) {
		return parse(uslFile.getParentFile(), Utils.readFile(uslFile));
	}

	/**
	 * @param workingDirectory used for finding included files, may be null only if parsed string does not contains
	 * any file include directive. However 'include <gdx>' or 'include <visui>' is always allowed even when workingDirectory
	 * is null.
	 */
	public static String parse (File workingDirectory, String usl) {
		LexerContext context = new LexerContext(workingDirectory);
		Lexer.lexUsl(context, usl);
		return new Parser().getJson(context.tokens);
	}
}
