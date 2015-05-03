package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class GdxSet {
	Array<GdxRelease> gdx;

	public static class GdxRelease {
		String version;
		String path;
	}
}
