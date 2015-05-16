/*
 * Copyright 2014-2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotcrab.vis.editor.siteapi;

import com.badlogic.gdx.utils.Json;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class APIClient {
	private static final String API_PATH = "http://apps.kotcrab.com/vis/v1/";

	private static final String CONTENT = API_PATH + "content.json";
	private static final String NEWS = API_PATH + "news.json";
	private static final String GDX = API_PATH + "gdx.json";
	private static final String VERSION = API_PATH + "version.json";

	private Json json;

	public APIClient () {
		json = new Json();
		json.setIgnoreUnknownFields(true);
	}

	public void readContent (SetCallback<ContentSet> callback) {
		read(callback, ContentSet.class, CONTENT);
	}

	public void readNews (SetCallback<NewsSet> callback) {
		read(callback, NewsSet.class, NEWS);
	}

	public void readGdx (SetCallback<GdxReleaseSet> callback) {
		read(callback, GdxReleaseSet.class, GDX);
	}

	public void readVersion (SetCallback<VersionSet> callback) {
		read(callback, VersionSet.class, VERSION);
	}

	private <T> void read (SetCallback<T> callback, Class<T> clazz, String url) {
		try {
			callback.reload(json.fromJson(clazz, readFromUrl(url)));
		} catch (IOException e) {
			callback.failed(e);
		}
	}

	private String readFromUrl (String url) throws IOException {
		InputStream in = new URL(url).openStream();

		try {
			return IOUtils.toString(in);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public interface SetCallback<T> {
		default void failed (Throwable cause) {

		}

		default void reload (T set) {

		}
	}
}
