package com.kotcrab.vis.usl;

import java.io.File;

public class USL {
	public static String parse (File uslFile) {
		return parse(uslFile.getParentFile(), Utils.readFile(uslFile));
	}

	/**
	 * @param workingDirectory used for finding included files, may be null only if parsed string does not contains
	 * any file include directive. Howewer 'include <gdx>' or 'include <visui>' is allowed
	 */
	public static String parse (File workingDirectory, String usl) {
		LexerContext context = new LexerContext(workingDirectory);
		Lexer.lexUsl(context, usl);
		return new Parser().getJson(context.tokens);
	}
}
