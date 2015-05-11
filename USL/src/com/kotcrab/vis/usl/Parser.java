package com.kotcrab.vis.usl;

import com.kotcrab.vis.usl.Token.Type;

import java.util.List;

public class Parser {
	private StringBuilder out;
	private List<Token> tokens;
	private int i = 0;

	private String currentPackage;
	private int packageEnd;

	private String currentStyleBlock;

	public String getJson (List<Token> tokens) {
		out = new StringBuilder();
		this.tokens = tokens;

		writeBegin();

		for (; i < tokens.size(); ) {
			Token t = tokens.get(i);
			//System.out.println(t.type + " " + (t.content == null ? "" : t.content));

			if (t.type == Type.STYLE_BLOCK) {

			}

			if (t.type == Type.PACKAGE) {
				if (currentPackage != null) Utils.throwException("Packages cannot be nested", t);
				currentPackage = t.content;
				i++;
				findPackageEnd();
				i++;
			}

			if (t.type == Type.LCURL) {
				writeLeftCurl();
				writeNewLine();
				i++;
			}

			if (t.type == Type.RCURL) {
				if (i != packageEnd) {
					writeRightCurl();
					writeNewLine();
				}

				i++;
			}

			Utils.throwException("Parser failed, invalid token", t);
		}

		writeEnd();

		return out.toString();
	}

	private void findPackageEnd () {
		int curliesLevel = 0;

		for (int j = i; j < tokens.size(); j++) {
			Token t = tokens.get(j);

			if (t.type == Type.LCURL) curliesLevel++;
			if (t.type == Type.RCURL) curliesLevel--;

			if (curliesLevel == 0) {
				packageEnd = j;
				break;
			}
		}
	}

	private void writeBegin () {
		out.append('{');
		out.append('\n');
	}

	private void writeEnd () {
		out.append('\n');
		out.append('}');
	}

	private void writeLeftCurl () {
		out.append('{');
	}

	private void writeRightCurl () {
		out.append('}');
	}

	private void writeNewLine () {
		out.append('\n');
	}

	private Token peekNext () {
		if (i + 1 > tokens.size())
			Utils.throwException("Unexpected EOF", tokens.get(i));

		return tokens.get(i + 1);
	}
}
