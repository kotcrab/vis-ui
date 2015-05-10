package com.kotcrab.vis.usl;

import com.kotcrab.vis.usl.Token.Type;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class USL {
	private static final String INCLUDE = "include";
	private static final String CLASSPATH = "classpath";
	private static final String INTERNAL = "internal";
	private static final String EXTENDS = "extends";
	private static final String INHERITS = "inherits";
	private static final String PACKAGE = "package";

	private static final Pattern globalStyleRegex = Pattern.compile("^\\.[a-zA-Z0-9-_]+:.*$", Pattern.DOTALL);
	private static final Pattern metaStyleRegex = Pattern.compile("^-[a-zA-Z0-9-_ ]+:.*$", Pattern.DOTALL);

	public static String parse (File uslFile) {
		return parse(readFile(uslFile));
	}

	public static String parse (String usl) {
		Context ctx = new Context();
		lexUsl(ctx, usl);
		return ctx.getJson();
	}

	private static void lexUsl (Context ctx, String usl) {
		for (int i = 0; i < usl.length(); ) {
			char ch = usl.charAt(i);

			if (Character.isWhitespace(ch)) { //white space
				i++;

			} else if (usl.startsWith("//", i)) { //line comment
				i = skipLineComment(usl, i);

			} else if (usl.startsWith(INCLUDE + " ", i)) { //include <type> <path> directive
				i = parseAndLexInclude(ctx, usl, i + INCLUDE.length() + 1);

			} else if (usl.startsWith(PACKAGE + " ", i)) { //package <path> directive
				i = lexPackage(ctx, usl, i + PACKAGE.length() + 1);

			} else if (ch == '#') { //block style definition
				i = lexStyleBlock(ctx, usl, i + 1);

			} else if (ch == '.') { //global block style definition
				if (globalStyleRegex.matcher(usl.substring(i)).matches() == false)
					throwException("Unexpected '.' or invalid global style block declaration", usl, i);

				i = leaGlobalStyleDeclaration(ctx, usl, i + 1);

			} else if (ch == '-') { //meta style definition
				if (metaStyleRegex.matcher(usl.substring(i)).matches() == false)
					throwException("Unexpected '-'", usl, i);

				//put meta token and continue, lexIdentifier will be called in next loop
				ctx.tokens.add(new Token(Type.META_STYLE));
				i++;

			} else if (ch == '{') {
				ctx.curliesLevel++;
				ctx.tokens.add(new Token(Type.LCURL));
				i++;
			} else if (ch == '}') {
				ctx.curliesLevel--;
				if (ctx.curliesLevel < 0) throwException("Unexpected '}'", usl, i);
				ctx.tokens.add(new Token(Type.RCURL));
				i++;
			} else if (ch == ',') {
				throwException("Unexpected ','", usl, i);
			} else if (peek(ctx.tokens).type == Type.IDENTIFIER) { //identifier content: someName: content
				i = lexIdentifierContent(ctx, usl, i);
			} else if (checkIdentifierDef(usl, i)) { // identifier: someName: content
				i = lexIdentifier(ctx, usl, i);
			} else {
				throwException("Unrecognized token '" + usl.substring(i, usl.indexOf(" ", i)) + "'", usl, i);
			}
		}
	}

	private static int lexPackage (Context ctx, String usl, int i) {
		int curlyIndex = usl.indexOf('{', i);
		String packageName = usl.substring(i, curlyIndex);
		ctx.tokens.add(new Token(Type.PACKAGE, packageName));
		return curlyIndex - 1;
	}

	private static int lexIdentifier (Context ctx, String usl, int i) {
		int idDefEnd = usl.indexOf(":", i);

		String idDef = usl.substring(i, idDefEnd);

		if (idDef.contains(" ")) { //blocks definition contains inherits
			if (idDef.contains(INHERITS) == false) throwException("Expected inherits", usl, i);
			String parts[] = idDef.split(" ", 3);
			if (parts.length != 3) throwException("Invalid inherits format", usl, i);

			ctx.tokens.add(new Token(Type.IDENTIFIER, parts[0]));
			ctx.tokens.add(new Token(Type.INHERITS));
			lexInherits(ctx, parts[2]);
		} else {
			ctx.tokens.add(new Token(Type.IDENTIFIER, idDef));
		}

		return idDefEnd + 1; //+1 for : at the end of id definition
	}

	private static int lexIdentifierContent (Context ctx, String usl, int i) {
		int commaIndex = usl.indexOf(',', i);
		int curlyIndex = usl.indexOf('}', i);
		//int spaceIndex = usl.indexOf(' ', i);

		if (commaIndex == -1) commaIndex = Integer.MAX_VALUE;
		if (curlyIndex == -1) curlyIndex = Integer.MAX_VALUE;
		//if (spaceIndex == -1) spaceIndex = Integer.MAX_VALUE;

		//int end = Math.min(commaIndex, Math.min(curlyIndex, spaceIndex));
		int end = Math.min(commaIndex, curlyIndex);
		if (end == -1) throwException("Identifier content end could not be found", usl, i);

		String content = usl.substring(i, end);
		content.replace(" ", "");
		ctx.tokens.add(new Token(Type.IDENTIFIER_CONTENT, content));

		i = i + content.length();
		if (usl.charAt(i) == ',') i++;
		return i;
	}

	private static int leaGlobalStyleDeclaration (Context ctx, String usl, int i) {
		int end = usl.indexOf(':', i);
		if (end == -1) throwException("Global style definition end could not be found", usl, i);

		String declaration = usl.substring(i, end);

		if (declaration.contains(" ")) { //global style definition contains inherits
			if (declaration.contains(INHERITS) == false) throwException("Expected inherits", usl, i);
			String parts[] = declaration.split(" ", 3);
			if (parts.length != 3) throwException("Invalid inherits format", usl, i);

			ctx.tokens.add(new Token(Type.GLOBAL_STYLE, parts[0]));
			ctx.tokens.add(new Token(Type.INHERITS));
			lexInherits(ctx, parts[2]);
		} else {
			ctx.tokens.add(new Token(Type.GLOBAL_STYLE, declaration));
		}

		return end + 1;
	}

	private static void lexInherits (Context ctx, String inheritString) {
		inheritString = inheritString.replace(" ", "");
		String[] inherits = inheritString.split(",");

		for (String inherit : inherits)
			ctx.tokens.add(new Token(Type.INHERITS_NAME, inherit));
	}

	private static boolean checkIdentifierDef (String usl, int i) {
		//checks if this is possible identifier definition
		int colonIndex = usl.indexOf(':', i);
		int spaceIndex = usl.indexOf(' ', i);
		if (colonIndex == -1) return false;
		if (colonIndex < spaceIndex)
			return true;
		else {
			if (usl.substring(spaceIndex + 1, colonIndex).startsWith(INHERITS))
				return true;
		}

		return false;
	}

	private static int lexStyleBlock (Context ctx, String usl, int i) {
		int blockDefEnd = usl.indexOf(":", i);
		if (blockDefEnd == -1) throwException("Expected block definition end", usl, i);

		String blockDef = usl.substring(i, blockDefEnd);

		if (blockDef.contains(" ")) { //blocks definition contains extends
			if (blockDef.contains(EXTENDS) == false) throwException("Expected extends", usl, i);
			String parts[] = blockDef.split(" ");
			if (parts.length != 3) throwException("Invalid extends format", usl, i);

			ctx.tokens.add(new Token(Type.STYLE_BLOCK, parts[0]));
			ctx.tokens.add(new Token(Type.STYLE_BLOCK_EXTENDS, parts[2]));
		} else {
			ctx.tokens.add(new Token(Type.STYLE_BLOCK, blockDef));
		}

		return blockDefEnd + 1; //+1 for : at the end of style definition
	}

	private static int parseAndLexInclude (Context ctx, String usl, int i) {
		if (usl.startsWith("<", i)) {
			int includeEnd = usl.indexOf(">", i);
			if (includeEnd == -1) throwException("Invalid include format", usl, i);

			String content = null;
			String includeName = usl.substring(i + 1, includeEnd);

			if (includeName.equals("gdx"))
				content = streamToString(USL.class.getResourceAsStream("gdx.usl"));
			else if (includeName.equals("visui"))
				content = streamToString(USL.class.getResourceAsStream("visui.usl"));
			else
				throwException("Invalid internal include file: " + includeName, usl, i);

			lexUsl(ctx, content);

			return includeEnd + 1;
		} else if (usl.startsWith("\"", i)) {
			int includeEnd = usl.indexOf("\"", i + 1);
			if (includeEnd == -1) throwException("Invalid include format", usl, i);

			String path = usl.substring(i + 1, includeEnd);
			File file = new File(path);

			if (file.exists() == false) throwException("Include file does not exist", usl, i);
			lexUsl(ctx, readFile(file));
			return includeEnd + 1;
		} else
			throwException("Invalid include format", usl, i);

		return -1;
	}

	private static int skipLineComment (String usl, int i) {
		for (; i < usl.length(); i++)
			if (usl.charAt(i) == '\n') break;

		return i;
	}

	private static void throwException (String exception, String usl, int i) {
		throw new USLException(exception + " " + "(line " + countLines(usl.substring(0, i)) + ")");
	}

	private static int countLines (String str) {
		if (str == null || str.isEmpty())
			return 0;

		int lines = 1;
		int pos = 0;
		while ((pos = str.indexOf("\n", pos) + 1) != 0)
			lines++;

		return lines;
	}

	private static String readFile (File file) {
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Reading file '" + file.getPath() + "' failed!", e);
		}
	}

	private static <T> T peek (List<T> list) {
		if (list != null && !list.isEmpty()) {
			return list.get(list.size() - 1);
		}

		return null;
	}

	private static String streamToString (InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
