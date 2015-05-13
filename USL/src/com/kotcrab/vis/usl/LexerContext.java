package com.kotcrab.vis.usl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LexerContext {
	final File workingDirectory;

	final List<Token> tokens = new ArrayList<Token>();
	int curliesLevel = 0;

	public LexerContext (File workingDirectory) {
		this.workingDirectory = workingDirectory;
	}
}
