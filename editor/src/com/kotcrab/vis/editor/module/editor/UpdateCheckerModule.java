/*
 * Copyright 2014-2016 See AUTHORS file.
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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.vis.UpdateChannelType;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Update checker module that checks one of three update channels: stable, beta and cutting edge.
 */
public class UpdateCheckerModule extends EditorModule {
	private static final String TAG = "UpdateChecker";
	private static final Pattern BUILD_TIMESTAMP_PATTERN = Pattern.compile("[0-9]{6}.*-[0-9]");

	private GeneralSettingsModule settings;
	private ToastModule toastModule;
	private MenuBarModule menuBar;

	@Override
	public void postInit () {
		if (settings.isCheckForUpdates() == false || App.isBuildTimestampValid() == false) {
			Log.info(TAG, "Update check skipped");
			return;
		}

		UpdateChannelType updateChannel = settings.getUpdateChannel();

		getReleases(updateChannel, new ReleasesListener() {
			@Override
			public void result (Array<EditorBuild> builds) {
				String parts[] = App.getBuildTimestamp().split("-");
				int currentBuildDate = Integer.parseInt(parts[0]);
				int currentBuildNumber = Integer.parseInt(parts[1]);

				for (EditorBuild build : builds) {
					String timestampParts[] = build.timestamp.split("-");
					int buildDate = Integer.parseInt(timestampParts[0]);
					int buildNumber = Integer.parseInt(timestampParts[1]);

					if (buildDate > currentBuildDate) {
						notifyUpdate(updateChannel, build.timestamp, build.url);
						return;
					} else if (buildDate == currentBuildDate && buildNumber > currentBuildNumber) {
						notifyUpdate(updateChannel, build.timestamp, build.url);
						return;
					}
				}

				Log.info(TAG, "No updates found");
			}

			@Override
			public void failed (Exception ex) {
				toastModule.show(new DetailsToast("Checking for updates failed", ex), 3);
			}
		});

	}

	private void notifyUpdate (UpdateChannelType updateChannel, String timestamp, String url) {
		Log.info(TAG, "Editor update found: " + updateChannel.toPrettyString() + " " + timestamp);

		Gdx.app.postRunnable(() -> {
			VisTable table = new VisTable(true);

			switch (updateChannel) {
				case STABLE:
					table.add("New stable build is available! (" + timestamp + ")");
					break;
				case EDGE:
					table.add("New cutting edge build is available! (" + timestamp + ")");
					break;
			}

			LinkLabel hide = new LinkLabel("Hide");
			table.add(new LinkLabel("Download", url));
			table.add(hide);
			menuBar.setUpdateInfoTableContent(table);

			hide.setListener(labelUrl -> menuBar.setUpdateInfoTableContent(null));
		});
	}

	public void getReleases (UpdateChannelType updateChannel, ReleasesListener listener) {
		new Thread(() -> {
			try {
				Document doc = null;
				doc = Jsoup.connect(updateChannel.getStorageURL()).get();
				Elements links = doc.select("a[href]");

				Array<EditorBuild> builds = new Array<>();

				for (int i = 1; i < links.size(); i++) { //first link is ../ so we skip it
					String url = links.get(i).absUrl("href");
					Matcher matcher = BUILD_TIMESTAMP_PATTERN.matcher(url);
					if (matcher.find()) {
						String timestamp = matcher.group();
						builds.add(new EditorBuild(timestamp, url));
					}
				}

				listener.result(builds);
			} catch (Exception e) {
				Log.exception(e);
				listener.failed(e);
			}
		}, "VisWebReleaseListGetter").start();
	}

	private interface ReleasesListener {
		void result (Array<EditorBuild> builds);

		void failed (Exception e);
	}

	/**
	 * Describes single editor build
	 * @author Kotcrab
	 */
	private static class EditorBuild {
		public String timestamp;
		public String url;

		public EditorBuild (String timestamp, String url) {
			this.timestamp = timestamp;
			this.url = url;
		}
	}

}
