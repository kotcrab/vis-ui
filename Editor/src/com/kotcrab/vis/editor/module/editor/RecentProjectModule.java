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
import com.kotcrab.vis.editor.event.bus.Event;
import com.kotcrab.vis.editor.event.bus.EventListener;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.project.Project;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Iterator;

/**
 * Creates and provides list of recently used projects
 * @author Kotcrab
 */
public class RecentProjectModule extends EditorModule implements EventListener {
	private Json json = new Json();

	private Array<RecentProjectEntry> recentProjects;
	private FileHandle storageFile;

	@Override
	public void init () {
		App.eventBus.register(this);

		FileHandle storage = Gdx.files.absolute(App.METADATA_FOLDER_PATH);
		storage.mkdirs();

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

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event event) {
		if (event instanceof ProjectStatusEvent) {
			ProjectStatusEvent projectEvent = (ProjectStatusEvent) event;

			if (projectEvent.status == Status.Loaded) {
				Project project = projectEvent.project;

				RecentProjectEntry entry = new RecentProjectEntry(
						project.getRecentProjectDisplayName(),
						project.getVisDirectory().child(ProjectIOModule.PROJECT_FILE).path());

				if (recentProjects.contains(entry, false)) return false;
				recentProjects.add(entry);

				json.toJson(recentProjects, storageFile);
			}
		}

		return false;
	}

	public Array<RecentProjectEntry> getRecentProjects () {
		return recentProjects;
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

			RecentProjectEntry entry = (RecentProjectEntry) o;

			return new EqualsBuilder()
					.append(name, entry.name)
					.append(projectPath, entry.projectPath)
					.isEquals();
		}

		@Override
		public int hashCode () {
			return new HashCodeBuilder(17, 37)
					.append(name)
					.append(projectPath)
					.toHashCode();
		}
	}
}
