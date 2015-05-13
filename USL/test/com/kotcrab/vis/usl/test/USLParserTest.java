package com.kotcrab.vis.usl.test;

import com.kotcrab.vis.usl.USL;
import org.junit.Test;

public class USLParserTest {
	@Test
	public void testGdxUslParser () throws Exception {
		USL.parse(null, "include <gdx>");
	}

	@Test
	public void testVisUslParser () throws Exception {
		USL.parse(null, "include <visui>");
	}

	@Test
	public void testVisCustomUslParser () throws Exception {
		USL.parse(null, "include <visui> " +
				"^VisTextButtonStyle: {\n" +
				"custom inherits default: { over: button-down }\n" +
				"}");
	}
}
