package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class NewsSet {
	Array<News> news;

	public static class News {
		String title;
		String text;
		String more;
	}
}
