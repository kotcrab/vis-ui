package com.kotcrab.vis.launcher.api;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.launcher.api.ContentSet.Content;
import com.kotcrab.vis.launcher.api.GdxSet.GdxRelease;
import com.kotcrab.vis.launcher.api.NewsSet.News;
import com.kotcrab.vis.launcher.api.VersionSet.Release;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class APIClient {
	private static final String API_PATH = "htpp://apps.kotcrab.com/vis/v1/";

	private static final String CONTENT = API_PATH + "content";
	private static final String NEWS = API_PATH + "news";
	private static final String GDX = API_PATH + "gdx";
	private static final String VERSION = API_PATH + "version";

	private Json json;

	public APIClient () {
		json = new Json();
		json.setIgnoreUnknownFields(true);
	}

	public void readContent (APICallback<Array<Content>> callback) {
		try {
			ContentSet set = json.fromJson(ContentSet.class, readFromUrl(CONTENT));
			callback.success(set.content);
		} catch (IOException e) {
			callback.failed(e);
		}
	}

	public void readNews (APICallback<Array<News>> callback) {
		try {
			NewsSet set = json.fromJson(NewsSet.class, readFromUrl(NEWS));
			callback.success(set.news);
		} catch (IOException e) {
			callback.failed(e);
		}
	}

	public void readGdx (APICallback<Array<GdxRelease>> callback) {
		try {
			GdxSet set = json.fromJson(GdxSet.class, readFromUrl(GDX));
			callback.success(set.gdx);
		} catch (IOException e) {
			callback.failed(e);
		}
	}

	public void readVersion (APICallback<Array<Release>> callback) {
		try {
			VersionSet set = json.fromJson(VersionSet.class, readFromUrl(VERSION));
			callback.success(set.versions);
		} catch (IOException e) {
			callback.failed(e);
		}
	}

	public String readFromUrl (String url) throws IOException {
		InputStream in = new URL(url).openStream();

		try {
			return IOUtils.toString(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	interface APICallback<T> {
		void failed (Throwable cause);

		void success (T result);
	}
}
