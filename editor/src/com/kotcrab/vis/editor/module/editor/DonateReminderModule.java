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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.kotcrab.vis.ui.widget.LinkLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * After x runs show notification encouraging to donate.
 * @author Kotcrab
 */
public class DonateReminderModule extends EditorModule {
	private static final String DONATE_URL = "http://vis.kotcrab.com/donate.html";

	private AppFileAccessModule fileAccess;
	private MenuBarModule menuBar;

	@Override
	public void init () {
		FileHandle storage = fileAccess.getMetadataFolder();
		FileHandle storageFile = storage.child("donateReminder.json");

		Json json = new Json();
		json.setIgnoreUnknownFields(true);
		json.addClassTag("EditorRunCounter", EditorRunCounter.class);

		EditorRunCounter runCounter;
		try {
			if (storageFile.exists()) {
				runCounter = json.fromJson(EditorRunCounter.class, storageFile);
			} else
				runCounter = new EditorRunCounter();
		} catch (SerializationException ignored) {
			runCounter = new EditorRunCounter();
		}

		runCounter.counter++;

		if (runCounter.counter % 50 == 0) {
			VisTable table = new VisTable(true);

			table.add("If you like VisEditor please consider").spaceRight(3);
			table.add(new LinkLabel("donating.", DONATE_URL));
			LinkLabel hide = new LinkLabel("Hide");
			table.add(hide);
			menuBar.setUpdateInfoTableContent(table);

			hide.setListener(labelUrl -> menuBar.setUpdateInfoTableContent(null));
		}

		json.toJson(runCounter, storageFile);
	}

	private static class EditorRunCounter {
		public int counter;
	}
}
