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
import com.kotcrab.vis.usl.lang.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** Converts stream of tokens created by {@link Lexer} into json string. */
public class Parser {
	private StringBuilder out;
	private List<Token> tokens;
	private int i = 0;

	private String currentPackage;
	private StyleBlock currentStyleBlock;
	private int currentPackageEnd;
	private int currentStyleBlockEnd;

	private ArrayList<StyleBlock> styleBlocks = new ArrayList<StyleBlock>();
	private ArrayList<StyleBlock> styleBlocksOverride = new ArrayList<StyleBlock>();
	private ArrayList<StyleIdentifier> globalStyles = new ArrayList<StyleIdentifier>();

	private Stack<GroupIdentifier> identifiers = new Stack<GroupIdentifier>();

	public String getJson (List<Token> tokens) {
		out = new StringBuilder();
		this.tokens = tokens;

		for (; i < tokens.size(); ) {
			Token t = tokens.get(i);
			//System.out.println(t.type + " " + (t.content == null ? "" : t.content));

			if (t.type == Type.PACKAGE) {
				if (currentPackage != null) Utils.throwException("Packages cannot be nested", t);
				if (t.content.endsWith(".")) Utils.throwException("Package name cannot end with dot", t);
				currentPackage = t.content;
				i++;
				currentPackageEnd = findBlockEnd();
				continue;
			}

			if (t.type == Type.STYLE_BLOCK) {
				if (currentStyleBlock != null) Utils.throwException("Style cannot be nested", t);
				currentStyleBlock = new StyleBlock();
				styleBlocks.add(currentStyleBlock);
				i++;
				currentStyleBlockEnd = findBlockEnd();

				if (currentPackage != null)
					currentStyleBlock.fullName = currentPackage + "." + t.content;
				else
					currentStyleBlock.fullName = t.content;

				continue;
			}

			if (t.type == Type.STYLE_BLOCK_OVERRIDE) {
				if (currentStyleBlock != null) Utils.throwException("Style cannot be nested", t);
				currentStyleBlock = new StyleBlock();
				styleBlocksOverride.add(currentStyleBlock);
				i++;
				currentStyleBlockEnd = findBlockEnd();

				currentStyleBlock.fullName = findMatchingStyle(t, t.content).fullName;
				continue;
			}

			if (t.type == Type.STYLE_BLOCK_EXTENDS) {
				if (currentStyleBlock == null)
					Utils.throwException("Unexpected extends", t);
				if (currentStyleBlock.extendsStyle != null)
					Utils.throwException("Style block can only extend one style", t);

				boolean isDefinedOnly = t.content.startsWith("~");

				currentStyleBlock.extendsStyle = findMatchingStyle(t, isDefinedOnly ? t.content.substring(1) : t.content);
				currentStyleBlock.extendsInheritOnlyDefinedStyles = isDefinedOnly;

				i++;
				continue;
			}

			if (t.type == Type.GLOBAL_STYLE) {
				StyleIdentifier globalId = new StyleIdentifier();
				globalId.name = "." + t.content;
				globalStyles.add(globalId);
				identifiers.push(globalId);
				i++;
				continue;
			}

			if (t.type == Type.IDENTIFIER) {
				if (identifiers.size() == 0) {
					StyleIdentifier id = new StyleIdentifier();
					if (peekPrev().type == Type.META_STYLE) id.metaStyle = true;
					id.name = t.content;

					currentStyleBlock.styles.add(id);
					identifiers.push(id);
					i++;
					continue;
				} else {

					if (peekNext().type == Type.IDENTIFIER_CONTENT) {
						identifiers.peek().content.add(new BasicIdentifier(t.content, peekNext().content));
						i += 2;
						continue;
					}

					if (peekNext().type == Type.INHERITS || peekNext().type == Type.LCURL) {
						GroupIdentifier id = new GroupIdentifier();
						identifiers.peek().content.add(id);
						identifiers.push(id);

						id.name = t.content;

						i++;
						continue;
					}

				}
			}

			//handles case where JSON alias is used (IDENTIFIER_CONTENT occurs right after IDENTIFIER)
			if (t.type == Type.IDENTIFIER_CONTENT && identifiers.size() == 1) {
				identifiers.pop().content.add(new AliasIdentifier(t.content));
				i++;
				continue;
			}

			if (t.type == Type.INHERITS || t.type == Type.IDENTIFIER_CONTENT || t.type == Type.LCURL || t.type == Type.META_STYLE) {
				i++;
				continue;
			}

			if (t.type == Type.INHERITS_NAME) {
				identifiers.peek().inherits.add(t.content);
				i++;
				continue;
			}

			if (t.type == Type.RCURL) {
				if (i == currentPackageEnd) {
					currentPackage = null;
					currentPackageEnd = -1;
				} else if (i == currentStyleBlockEnd) {
					currentStyleBlock = null;
					currentStyleBlockEnd = -1;

				} else {
					identifiers.pop();
				}

				i++;
				continue;
			}

			Utils.throwException("Parser failed, invalid token: " + t.type, t);
		}

		postCheck();

		ArrayList<StyleBlock> mergedStyleBlocks = new StyleMerger(globalStyles, styleBlocks, styleBlocksOverride).merge();
		return new USLJsonWriter(mergedStyleBlocks).getJson();
	}

	private StyleBlock findMatchingStyle (Token t, String name) {
		ArrayList<StyleBlock> matches = new ArrayList<StyleBlock>();

		//search for literal match
		for (StyleBlock block : styleBlocks) {
			if (block.fullName.equals(name)) {
				matches.add(block);
			}
		}

		//search for last $ match
		for (StyleBlock block : styleBlocks) {
			String parts[] = block.fullName.split("\\$");
			if (parts.length == 2 && parts[1].equals(name)) {
				matches.add(block);
			}
		}

		//search for last . match
		for (StyleBlock block : styleBlocks) {
			String parts[] = block.fullName.split("\\.");
			if (parts.length == 2 && parts[1].equals(name)) {
				matches.add(block);
			}
		}

		if (matches.size() == 0) Utils.throwException("Style block extends unknown undefined style: " + name, t);

		StyleBlock match = matches.get(0);

		if (matches.size() > 1)
			System.out.println("Warn: multiples matches found for name: '" + name + "', using: " + match.fullName);

		if (match == currentStyleBlock) Utils.throwException("Style block cannot extend itself", t);

		return match;
	}

	private int findBlockEnd () {
		int curliesLevel = 0;

		int firstLCurl;

		for (firstLCurl = i; firstLCurl < tokens.size(); firstLCurl++) {
			Token t = tokens.get(firstLCurl);
			if (t.type == Type.LCURL) break;
		}

		for (int j = firstLCurl; j < tokens.size(); j++) {
			Token t = tokens.get(j);

			if (t.type == Type.LCURL) curliesLevel++;
			if (t.type == Type.RCURL) curliesLevel--;

			if (curliesLevel == 0)
				return j;
		}

		Utils.throwException("Parser failed, end of block not found", tokens.get(i));
		return -1;
	}

	private Token peekPrev () {
		return tokens.get(i - 1);
	}

	private Token peekNext () {
		if (i + 1 > tokens.size())
			Utils.throwException("Unexpected EOF", tokens.get(i));

		return tokens.get(i + 1);
	}

	private void postCheck () {
		if (identifiers.size() > 0)
			System.out.println("Post check warning: identifier stack not empty after parsing. " +
					"Some identifiers not closed or internal parser error.");

		for (StyleIdentifier id : globalStyles) {
			postCheckStyleId(id);
		}

		for (StyleBlock block : styleBlocks) {
			for (StyleIdentifier id : block.styles)
				postCheckStyleId(id);
		}
	}

	private void postCheckStyleId (StyleIdentifier styleId) {
		if (styleId.name.contains(" ")) throwContainsSpaceException(styleId);

		for (Identifier id : styleId.content) {
			postCheckId(id);
		}
	}

	private void postCheckId (Identifier id) {
		if (id instanceof BasicIdentifier) {
			BasicIdentifier bid = (BasicIdentifier) id;
			if (bid.name.contains(" ") || bid.content.contains(" ")) throwContainsSpaceException(bid);
		}

		if (id instanceof GroupIdentifier) {
			GroupIdentifier gid = (GroupIdentifier) id;
			if (gid.name.contains(" ")) throwContainsSpaceException(gid);

			for (Identifier gidId : gid.content)
				postCheckId(gidId);
		}
	}

	private void throwContainsSpaceException (BasicIdentifier id) {
		throw new USLException("Identifier contains illegal space in name or content. Name: '" + id.name + "', content: '" + id.content + "'");
	}

	private void throwContainsSpaceException (GroupIdentifier groupId) {
		String content = null;
		for (Identifier id : groupId.content) {
			content += id.name + ":..., ";
		}
		throw new USLException("Identifier contains illegal space in name or content. Name: '" + groupId.name + "', content: '" + content + "'");
	}
}
