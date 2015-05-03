package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class VersionSet {
	Array<Release> versions;

	public enum ReleaseType {
		BETA, RELESAE
	}

	public static class Release {
		String title;
		String path;
		ReleaseType type;
	}
}
