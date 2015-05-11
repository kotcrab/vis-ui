package com.kotcrab.vis.usl;

public class Token {
	public String usl;
	public int i;

	public Type type;
	public String content;

	public Token (String usl, int i, Type type) {
		this.usl = usl;
		this.i = i;
		this.type = type;
	}

	public Token (String usl, int i, Type type, String content) {
		this.usl = usl;
		this.i = i;
		this.type = type;
		this.content = content;
	}

	public enum Type{
		LCURL, RCURL, // { }
		STYLE_BLOCK, STYLE_BLOCK_EXTENDS,
		GLOBAL_STYLE, PACKAGE,
		IDENTIFIER, IDENTIFIER_CONTENT,
		INHERITS, INHERITS_NAME,
		META_STYLE
	}
}
