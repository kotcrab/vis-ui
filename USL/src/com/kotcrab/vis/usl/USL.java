package com.kotcrab.vis.usl;

import java.io.File;

/** Allows to use USL from code */
public class USL {
	private static final String USER_HOME_PATH = System.getProperty("user.home") + File.separator;
	public static final String USL_FOLDER_PATH = USER_HOME_PATH + ".usl" + File.separator;
	public static final String CACHE_FOLDER_PATH = USL_FOLDER_PATH + "cache" + File.separator;

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
