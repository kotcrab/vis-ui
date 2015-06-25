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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.ui.toast.ExceptionToast;
import com.kotcrab.vis.editor.webapi.EditorBuild;
import com.kotcrab.vis.editor.webapi.UpdateChannelType;
import com.kotcrab.vis.editor.webapi.WebAPIEditorVersionListener;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Update checker module that checks one of three update channels: stable, beta and cutting edge.
 */
public class UpdateCheckerModule extends EditorModule {
	private static final String TAG = "UpdateChecker";

	@InjectModule WebAPIModule webAPI;
	@InjectModule GeneralSettingsModule settings;
	@InjectModule ToastModule toastModule;
	@InjectModule MenuBarModule menuBar;

	@Override
	public void postInit () {
		if (settings.isCheckForUpdates() == false || App.buildTimestampValid == false) {
			Log.info(TAG, "Update check skipped");
			return;
		}

		UpdateChannelType updateChannel = settings.getUpdateChannel();

		webAPI.getReleases(updateChannel, new WebAPIEditorVersionListener() {
			@Override
			public void result (Array<EditorBuild> builds) {
				String parts[] = App.buildTimestamp.split("-");
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
				toastModule.show(new ExceptionToast("Checking for updates failed", ex), 3);
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
				case BETA:
					table.add("New beta build is available! (" + timestamp + ")");
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

	@Override
	public void dispose () {
	}
}
