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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.WebAPIEditorVersionListener;
import com.kotcrab.vis.editor.webapi.ContentSet;
import com.kotcrab.vis.editor.webapi.EditorBuild;
import com.kotcrab.vis.editor.webapi.SiteAPIClient;
import com.kotcrab.vis.editor.webapi.SiteAPIClient.SiteAPICallback;
import com.kotcrab.vis.editor.webapi.UpdateChannelType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebAPIModule extends EditorModule {
	private static final Pattern BUILD_TIMESTAMP_PATERN = Pattern.compile("[0-9]{6}.*-[0-9]");

	private SiteAPIClient siteApiClient;

	private Json json;

	private ContentSet content;

	private FileHandle contentCacheFile;

	private boolean refreshInProgress;

	@Override
	public void init () {
		siteApiClient = new SiteAPIClient();

		FileHandle apiCache = Gdx.files.absolute(App.CACHE_FOLDER_PATH).child("api");
		apiCache.mkdirs();

		contentCacheFile = apiCache.child("content.json");

		content = new ContentSet();

		json = new Json();
		json.setIgnoreUnknownFields(true);

		try {
			if (contentCacheFile.exists())
				content = json.fromJson(ContentSet.class, contentCacheFile);
		} catch (SerializationException ignored) { //no big deal if cache can't be loaded
		}

		refresh();
	}

	public void refresh () {
		if (refreshInProgress) return;

		new Thread(() -> {
			refreshInProgress = true;

			siteApiClient.readContent(new SiteAPICallback<ContentSet>() {
				@Override
				public void reload (ContentSet set) {
					content = set;
					json.toJson(content, contentCacheFile);
				}
			});

			refreshInProgress = false;
		}, "VisAPIClient").start();
	}

	public void getReleases (UpdateChannelType updateChannel, WebAPIEditorVersionListener listener) {
		new Thread(() -> {
			try {
				Document doc = null;
				doc = Jsoup.connect(updateChannel.getStorageURL()).get();
				Elements links = doc.select("a[href]");

				Array<EditorBuild> builds = new Array<>();

				for (int i = 1; i < links.size(); i++) { //first link is ../ so we skip it
					String url = links.get(i).absUrl("href");
					Matcher matcher = BUILD_TIMESTAMP_PATERN.matcher(url);
					matcher.find();
					String timestamp = matcher.group();

					builds.add(new EditorBuild(timestamp, url));
				}

				listener.result(builds);
			} catch (Exception e) {
				Log.exception(e);
				listener.failed(e);
			}
		}, "VisWebReleaseListGetter").start();
	}
}
