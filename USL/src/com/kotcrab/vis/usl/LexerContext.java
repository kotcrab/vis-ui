package com.kotcrab.vis.usl;

import java.util.ArrayList;
import java.util.List;

public class LexerContext {
	List<Token> tokens = new ArrayList<Token>();
	int curliesLevel = 0;
}
