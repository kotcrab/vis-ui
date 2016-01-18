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
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.OpenSceneRequest;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.editor.util.vis.LaunchConfiguration;

import java.util.Optional;

/** @author Kotcrab */
@EventBusSubscriber
public class ProjectAutoLoader extends EditorModule {
	private static final String TAG = "ProjectAutoLoader";

	private ProjectIOModule projectIO;

	private Optional<FileHandle> projectFile;
	private Optional<FileHandle> sceneFile;

	private boolean firstLoading = true;

	@Override
	public void postInit () {
		LaunchConfiguration config = Editor.instance.getLaunchConfig();
		projectFile = getAbsoluteHandleIfExists(config.projectPath);
		sceneFile = getAbsoluteHandleIfExists(config.scenePath);

		if (projectFile.isPresent() == false) {
			if (config.projectPath != null) Log.warn(TAG, "Could not find project: " + config.projectPath);
			return;
		}

		try {
			projectIO.load(projectFile.get());
		} catch (EditorException e) {
			Log.exception(e);
		}
	}

	@Subscribe
	public void handleProjectStatusEvent (ProjectStatusEvent event) {
		if (firstLoading == false || projectFile == null || sceneFile == null) return;

		if (event.status == Status.Loaded) {
			if (sceneFile.isPresent()) {
				App.eventBus.post(new OpenSceneRequest(sceneFile.get()));
			}

			firstLoading = false;
		}
	}

	private Optional<FileHandle> getAbsoluteHandleIfExists (String path) {
		if (path == null) return Optional.empty();
		FileHandle file = Gdx.files.absolute(path);
		if (file.exists() == false) return Optional.empty();
		return Optional.of(file);
	}
}
