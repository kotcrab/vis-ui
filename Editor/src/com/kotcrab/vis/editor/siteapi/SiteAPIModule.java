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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.module.editor.EditorModule;
import com.kotcrab.vis.editor.siteapi.SiteAPIClient.SiteAPICallback;

public class SiteAPIModule extends EditorModule {
	private SiteAPIClient siteApiClient;

	private Json json;

	private ContentSet content;
	private GdxReleaseSet gdx;
	private VersionSet version;

	private FileHandle contentCacheFile;
	private FileHandle gdxCacheFile;
	private FileHandle versionCacheFile;

	private boolean refreshInProgress;

	@Override
	public void init () {
		siteApiClient = new SiteAPIClient();

		FileHandle apiCache = Gdx.files.absolute(App.APP_FOLDER_PATH).child("cache").child("api");
		apiCache.mkdirs();

		contentCacheFile = apiCache.child("content.json");
		gdxCacheFile = apiCache.child("gdx.json");
		versionCacheFile = apiCache.child("version.json");

		content = new ContentSet();
		gdx = new GdxReleaseSet();
		version = new VersionSet();

		json = new Json();
		json.setIgnoreUnknownFields(true);

		try {
			if (contentCacheFile.exists())
				content = json.fromJson(ContentSet.class, contentCacheFile);
		} catch (SerializationException ignored) { //no big deal if cache can't be loaded
		}

		try {
			if (gdxCacheFile.exists())
				gdx = json.fromJson(GdxReleaseSet.class, gdxCacheFile);
		} catch (SerializationException ignored) {
		}

		try {
			if (versionCacheFile.exists())
				version = json.fromJson(VersionSet.class, versionCacheFile);
		} catch (SerializationException ignored) {
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

			siteApiClient.readGdx(new SiteAPICallback<GdxReleaseSet>() {
				@Override
				public void reload (GdxReleaseSet set) {
					gdx = set;
					json.toJson(gdx, gdxCacheFile);
				}
			});

			siteApiClient.readVersion(new SiteAPICallback<VersionSet>() {
				@Override
				public void reload (VersionSet set) {
					version = set;
					json.toJson(version, versionCacheFile);
				}
			});

			refreshInProgress = false;
		}, "APIClient").start();
	}
}
