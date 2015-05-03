package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class ContentSet {
	Array<Content> content;

	public static class Content {
		String title;
		String description;
		String version;
		String compatibility;
		String path;
	}
}
