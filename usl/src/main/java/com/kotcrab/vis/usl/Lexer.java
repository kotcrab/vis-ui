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

import com.kotcrab.vis.usl.Token.Type;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

/** USL Lexer that turns USL file intro stream of tokens */
public class Lexer {
	private static final String INCLUDE = "include";
	private static final String EXTENDS = "extends";
	private static final String INHERITS = "inherits";
	private static final String PACKAGE = "package";

	private static final Pattern globalStyleRegex = Pattern.compile("^\\.[a-zA-Z0-9-_]+:.*$", Pattern.DOTALL);
	private static final Pattern metaStyleRegex = Pattern.compile("^-[a-zA-Z0-9-_ ]+:.*$", Pattern.DOTALL);

	private static IncludeLoader includeLoader = new IncludeLoader();

	static void lexUsl (LexerContext ctx, String usl) {
		usl = usl.replace("\r\n", "\n");
		usl = removeComments(usl);

		for (int i = 0; i < usl.length(); ) {
			char ch = usl.charAt(i);

			if (Character.isWhitespace(ch)) { //white space
				i++;

			} else if (usl.startsWith(INCLUDE + " ", i)) { //include <type> <path> directive
				i = parseAndLexInclude(ctx, usl, i + INCLUDE.length() + 1);

			} else if (usl.startsWith(PACKAGE + " ", i)) { //package <path> directive
				i = lexPackage(ctx, usl, i + PACKAGE.length() + 1);

			} else if (ch == '#') { //block style definition
				i = lexStyleBlock(ctx, usl, i + 1);

			} else if (ch == '^') { //block style override block definition
				i = lexStyleBlockOverride(ctx, usl, i + 1);

			} else if (ch == '.') { //global block style definition
				if (globalStyleRegex.matcher(usl.substring(i)).matches() == false)
					Utils.throwException("Unexpected '.' or invalid global style block declaration", usl, i);

				i = lexGlobalStyleDeclaration(ctx, usl, i + 1);

			} else if (ch == '-' && peek(ctx.tokens).type != Type.IDENTIFIER) { //meta style definition
				if (metaStyleRegex.matcher(usl.substring(i)).matches() == false) {
					Utils.throwException("Unexpected '-'", usl, i);
				}

				//put meta token and continue, lexIdentifier will be called in next loop
				ctx.tokens.add(new Token(usl, i, Type.META_STYLE));
				i++;

			} else if (ch == '{') {
				ctx.curliesLevel++;
				ctx.tokens.add(new Token(usl, i, Type.LCURL));
				i++;
			} else if (ch == '}') {
				ctx.curliesLevel--;
				if (ctx.curliesLevel < 0) Utils.throwException("Unexpected '}'", usl, i);
				ctx.tokens.add(new Token(usl, i, Type.RCURL));
				i++;
			} else if (ch == ',') {
				if (ctx.curliesLevel > 1 && peek(ctx.tokens, 2).type == Type.IDENTIFIER_CONTENT) //',' will be valid if we are currently inside style definition
					i++;
				else
					Utils.throwException("Unexpected ','", usl, i);
			} else if (peek(ctx.tokens).type == Type.IDENTIFIER) { //identifier content: someName: content
				i = lexIdentifierContent(ctx, usl, i);
			} else if (checkIdentifierDef(usl, i)) { // identifier: someName: content
				i = lexIdentifier(ctx, usl, i);
			} else {
				Utils.throwException("Unrecognized symbol '" + usl.substring(i, usl.indexOf(" ", i)) + "'", usl, i);
			}
		}
	}

	private static String removeComments (String usl) {
		StringBuilder newUsl = new StringBuilder(usl.length());
		boolean insideComment = false;
		for (int i = 0; i < usl.length(); i++) {
			char ch = usl.charAt(i);

			if (usl.startsWith("//", i)) {
				insideComment = true;
			}

			if (ch == '\n' && insideComment) {
				insideComment = false;
			}

			if (insideComment == false) {
				newUsl.append(ch);
			}
		}

		return newUsl.toString();
	}

	private static int lexPackage (LexerContext ctx, String usl, int i) {
		int curlyIndex = usl.indexOf('{', i);
		String packageName = usl.substring(i, curlyIndex);
		ctx.tokens.add(new Token(usl, i, Type.PACKAGE, packageName.replace(" ", "")));
		return curlyIndex - 1;
	}

	private static int lexIdentifier (LexerContext ctx, String usl, int i) {
		int idDefEnd = usl.indexOf(":", i);

		String idDef = usl.substring(i, idDefEnd);

		if (idDef.contains(" ")) { //blocks definition contains inherits
			if (idDef.contains(INHERITS) == false) Utils.throwException("Expected inherits", usl, i);
			String parts[] = idDef.split(" ", 3);
			if (parts.length != 3) Utils.throwException("Invalid inherits format", usl, i);

			ctx.tokens.add(new Token(usl, i, Type.IDENTIFIER, parts[0]));
			ctx.tokens.add(new Token(usl, i, Type.INHERITS));
			lexInherits(usl, i, ctx, parts[2]);
		} else {
			ctx.tokens.add(new Token(usl, i, Type.IDENTIFIER, idDef));
		}

		return idDefEnd + 1; //+1 for : at the end of id definition
	}

	private static int lexIdentifierContent (LexerContext ctx, String usl, int i) {
		int commaIndex = usl.indexOf(',', i);
		int curlyIndex = usl.indexOf('}', i);
		int endLineIndex = usl.indexOf('\r', i);
		if (endLineIndex == -1) endLineIndex = usl.indexOf('\n', i);

		if (commaIndex == -1) commaIndex = Integer.MAX_VALUE;
		if (curlyIndex == -1) curlyIndex = Integer.MAX_VALUE;
		if (endLineIndex == -1) endLineIndex = Integer.MAX_VALUE;

		int end = Math.min(commaIndex, Math.min(curlyIndex, endLineIndex));
		if (end == -1) Utils.throwException("Identifier content end could not be found", usl, i);

		String content = usl.substring(i, end);
		int origLength = content.length();
		if (content.endsWith(" ")) content = content.substring(0, content.length() - 1);
		ctx.tokens.add(new Token(usl, i, Type.IDENTIFIER_CONTENT, content));

		i = i + origLength;
		if (usl.charAt(i) == ',') i++;
		return i;
	}

	private static int lexGlobalStyleDeclaration (LexerContext ctx, String usl, int i) {
		int end = usl.indexOf(':', i);
		if (end == -1) Utils.throwException("Global style definition end could not be found", usl, i);

		String declaration = usl.substring(i, end);

		if (declaration.contains(" ")) { //global style definition contains inherits
			if (declaration.contains(INHERITS) == false) Utils.throwException("Expected inherits", usl, i);
			String parts[] = declaration.split(" ", 3);
			if (parts.length != 3) Utils.throwException("Invalid inherits format", usl, i);

			ctx.tokens.add(new Token(usl, i, Type.GLOBAL_STYLE, parts[0]));
			ctx.tokens.add(new Token(usl, i, Type.INHERITS));
			lexInherits(usl, i, ctx, parts[2]);
		} else {
			ctx.tokens.add(new Token(usl, i, Type.GLOBAL_STYLE, declaration));
		}

		return end + 1;
	}

	private static void lexInherits (String usl, int i, LexerContext ctx, String inheritString) {
		inheritString = inheritString.replace(" ", "");
		String[] inherits = inheritString.split(",");

		for (String inherit : inherits)
			ctx.tokens.add(new Token(usl, i, Type.INHERITS_NAME, inherit));
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

	private static int lexStyleBlock (LexerContext ctx, String usl, int i) {
		int blockDefEnd = usl.indexOf(":", i);
		if (blockDefEnd == -1) Utils.throwException("Expected block definition end", usl, i);

		String blockDef = usl.substring(i, blockDefEnd);

		if (blockDef.contains(" ")) { //blocks definition contains extends
			if (blockDef.contains(EXTENDS) == false) Utils.throwException("Expected extends", usl, i);
			String parts[] = blockDef.split(" ");
			if (parts.length != 3) Utils.throwException("Invalid extends format", usl, i);

			ctx.tokens.add(new Token(usl, i, Type.STYLE_BLOCK, parts[0]));
			ctx.tokens.add(new Token(usl, i, Type.STYLE_BLOCK_EXTENDS, parts[2]));
		} else
			ctx.tokens.add(new Token(usl, i, Type.STYLE_BLOCK, blockDef));

		return blockDefEnd + 1; //+1 for : at the end of style definition
	}

	private static int lexStyleBlockOverride (LexerContext ctx, String usl, int i) {
		int blockDefEnd = usl.indexOf(":", i);
		if (blockDefEnd == -1) Utils.throwException("Expected block definition end", usl, i);

		String blockDef = usl.substring(i, blockDefEnd);

		if (blockDef.contains(" ")) {
			if (blockDef.contains("extends"))
				Utils.throwException("Override style block cannot extend other style", usl, i);
			else
				Utils.throwException("Invalid block definition", usl, i);
		} else
			ctx.tokens.add(new Token(usl, i, Type.STYLE_BLOCK_OVERRIDE, blockDef));

		return blockDefEnd + 1; //+1 for : at the end of style definition
	}

	private static int parseAndLexInclude (LexerContext ctx, String usl, int i) {
		if (usl.startsWith("<", i)) {
			int includeEnd = usl.indexOf(">", i);
			if (includeEnd == -1) Utils.throwException("Invalid include format, '>` expected", usl, i);

			String includeName = usl.substring(i + 1, includeEnd);
			String content = includeLoader.loadInclude(includeName);
			lexUsl(ctx, content);

			return includeEnd + 1;
		} else if (usl.startsWith("\"", i)) {
			int includeEnd = usl.indexOf("\"", i + 1);
			if (includeEnd == -1) Utils.throwException("Invalid include format, '\"' expected", usl, i);

			String path = usl.substring(i + 1, includeEnd);
			File file = new File(path);

			if (file.exists() == false)
				Utils.throwException("Include file does not exist, file: " + file.getAbsolutePath(), usl, i);
			lexUsl(ctx, Utils.readFile(file));
			return includeEnd + 1;
		} else
			Utils.throwException("Invalid include format", usl, i);

		return -1;
	}

	public static void addIncludeSource (String path) {
		includeLoader.addIncludeSource(path);
	}

	private static <T> T peek (List<T> list) {
		return peek(list, 1);
	}

	private static <T> T peek (List<T> list, int i) {
		if (list != null && !list.isEmpty()) {
			return list.get(list.size() - i);
		}

		return null;
	}

}
