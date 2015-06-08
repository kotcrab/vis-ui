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
		if (str == null || str.isEmpty())
			return 0;

		int lines = 1;
		int pos = 0;
		while ((pos = str.indexOf("\n", pos) + 1) != 0)
			lines++;

		return lines;
	}
}
