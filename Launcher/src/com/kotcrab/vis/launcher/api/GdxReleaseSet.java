package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class GdxReleaseSet {
	Array<GdxRelease> gdx;

	public static class GdxRelease {
		String version;
		String path;
	}
}
