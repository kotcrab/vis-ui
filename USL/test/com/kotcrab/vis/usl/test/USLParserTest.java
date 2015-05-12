package com.kotcrab.vis.usl.test;

import com.kotcrab.vis.usl.USL;
import org.junit.Test;

public class USLParserTest {
	@Test
	public void testGdxUslParser () throws Exception {
		USL.parse("include <gdx>");
	}

	@Test
	public void testVisUslParser () throws Exception {
		USL.parse("include <visui>");
	}

	@Test
	public void testVisCustomUslParser () throws Exception {
		String res = USL.parse("include <visui> " +
				"^VisTextButtonStyle: {\n" +
				"custom inherits default: { over: button-down }\n" +
				"}");

		System.out.println(res);
	}
}
