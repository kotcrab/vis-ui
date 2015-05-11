package com.kotcrab.vis.usl;

import java.io.File;

public class USL {
	public static String parse (File uslFile) {
		return parse(Utils.readFile(uslFile));
	}

	public static String parse (String usl) {
		LexerContext context = new LexerContext();
		Lexer.lexUsl(context, usl);
		return new Parser().getJson(context.tokens);
	}
}
