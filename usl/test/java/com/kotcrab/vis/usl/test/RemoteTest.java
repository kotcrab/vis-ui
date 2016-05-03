package com.kotcrab.vis.usl.test;

import com.kotcrab.vis.usl.USL;
import org.junit.Ignore;
import org.junit.Test;

public class RemoteTest {
	@Ignore
	@Test
	public void testRemote () throws Exception {
		USL.parse(null, "include <visui-0.7.7>");
		USL.parse(null, "include <visui-0.8.0>");
		USL.parse(null, "include <visui-0.8.1>");
	}
}
