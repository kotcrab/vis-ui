package com.kotcrab.vis.usl.test;

import com.kotcrab.vis.usl.USL;
import org.junit.Test;

public class USLParserTest {
	@Test
	public void testUSLParser () throws Exception {
		USL.parse("include <gdx>");
		USL.parse("include <visui>");
	}
}
