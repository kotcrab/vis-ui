package com.kotcrab.vis.usl.lang;

public class BasicIdentifier extends Identifier {
	public String content;

	public BasicIdentifier () {
	}

	public BasicIdentifier (String name, String content) {
		super(name);
		this.content = content;
	}
}
