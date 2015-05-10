package com.kotcrab.vis.usl;

public class Token {
	public Type type;
	public String content;

	public Token (Type type) {
		this.type = type;
	}

	public Token (Type type, String content) {
		this.type = type;
		this.content = content;
	}

	public enum Type{
		LCURL, RCURL,
		STYLE_BLOCK, STYLE_BLOCK_EXTENDS,
		GLOBAL_STYLE, PACKAGE,
		IDENTIFIER, IDENTIFIER_CONTENT,
		INHERITS, INHERITS_NAME,
		META_STYLE
	}
}
