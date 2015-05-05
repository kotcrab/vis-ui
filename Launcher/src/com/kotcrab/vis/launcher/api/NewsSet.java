package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;

public class NewsSet {
	public Array<News> news;

	public static class News {
		public String title;
		public String text;
		public String more;
	}
}
