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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.project.Project;

import java.util.Iterator;

/**
 * Creates and provides list of recently used projects
 * @author Kotcrab
 */
@EventBusSubscriber
public class RecentProjectModule extends EditorModule {
	private Json json = new Json();

	private AppFileAccessModule fileAccess;

	private Array<RecentProjectEntry> recentProjects;
	private FileHandle storageFile;

	@Override
	public void init () {
		FileHandle storage = fileAccess.getMetadataFolder();

		storageFile = storage.child("recentProjects.json");

		json = new Json();
		json.setIgnoreUnknownFields(true);
		json.addClassTag("RecentProjectEntry", RecentProjectEntry.class);

		try {

			if (storageFile.exists()) {
				recentProjects = json.fromJson(new Array<RecentProjectEntry>().getClass(), storageFile);

				Iterator<RecentProjectEntry> it = recentProjects.iterator();
				while (it.hasNext()) {
					RecentProjectEntry entry = it.next();

					if (Gdx.files.absolute(entry.projectPath).exists() == false) it.remove();
				}
			} else
				recentProjects = new Array<>();

		} catch (SerializationException ignored) { //no big deal if cache can't be loaded
			recentProjects = new Array<>();
		}
	}

	@Subscribe
	public void handleProjectStatusEvent (ProjectStatusEvent event) {
		if (event.status == Status.Loaded) {
			Project project = event.project;

			RecentProjectEntry entry = new RecentProjectEntry(
					project.getRecentProjectDisplayName(),
					project.getVisDirectory().child(ProjectIOModule.PROJECT_FILE).path());

			if (recentProjects.contains(entry, false)) return;
			recentProjects.add(entry);

			save();
		}
	}

	public Array<RecentProjectEntry> getRecentProjects () {
		return recentProjects;
	}

	public void clear () {
		recentProjects.clear();
		save();
	}

	public void remove (RecentProjectEntry entry) {
		recentProjects.removeValue(entry, true);
		save();
	}

	private void save () {
		json.toJson(recentProjects, storageFile);
	}

	public static class RecentProjectEntry {
		public String name;
		public String projectPath;

		public RecentProjectEntry () {
		}

		public RecentProjectEntry (String name, String projectPath) {
			this.name = name;
			this.projectPath = projectPath;
		}

		@Override
		public boolean equals (Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			RecentProjectEntry that = (RecentProjectEntry) o;

			if (!name.equals(that.name)) return false;
			return projectPath.equals(that.projectPath);

		}

		@Override
		public int hashCode () {
			int result = name.hashCode();
			result = 31 * result + projectPath.hashCode();
			return result;
		}
	}
}
