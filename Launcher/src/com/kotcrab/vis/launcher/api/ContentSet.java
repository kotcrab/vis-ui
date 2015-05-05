package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class ContentSet {
	Array<Content> content = new Array<>();

	public static class Content {
		String title;
		String description;
		String version;
		String compatibility;
		String path;
	}
}
