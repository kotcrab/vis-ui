package com.kotcrab.vis.usl;

import java.util.ArrayList;
import java.util.List;

public class Context {
	private StringBuilder out = new StringBuilder();

	List<Token> tokens = new ArrayList<Token>();

	int curliesLevel = 0;

	public Context () {
	}

	private void writeBegin () {
		out.append('{');
		out.append('\n');
	}

	private void wirteEnd () {
		out.append('\n');
		out.append('}');
	}

	public String getJson () {
//		for (Token t : tokens)
//			System.out.println(t.type + " " + (t.content == null ? "" : t.content));

		return out.toString();
	}

	public void write (char c) {
		out.append(c);
	}
}
