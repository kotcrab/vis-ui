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

package com.kotcrab.vis.editor.webapi;

import com.badlogic.gdx.utils.Json;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SiteAPIClient {
	private static final String API_PATH = "http://apps.kotcrab.com/vis/v1/";

	private static final String CONTENT = API_PATH + "content.json";

	private Json json;

	public SiteAPIClient () {
		json = new Json();
		json.setIgnoreUnknownFields(true);
	}

	public void readContent (SiteAPICallback<ContentSet> callback) {
		read(callback, ContentSet.class, CONTENT);
	}

	private <T> void read (SiteAPICallback<T> callback, Class<T> clazz, String url) {
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

	public interface SiteAPICallback<T> {
		default void failed (Throwable cause) {

		}

		default void reload (T set) {

		}
	}
}
