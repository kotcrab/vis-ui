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
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.OpenSceneRequest;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.util.vis.EditorException;

/**
 * The purpose of this module is to dramatically speed up development by auto loading project and test scene. To start using it do the
 * following things: open VisEditor, click About from Help menu, click open app folder. Create file with name debug.this that
 * will contain two lines: first is full project path and the second one is full scene path.
 * @author Kotcrab
 */
@EventBusSubscriber
public class DevelopmentSpeedupModule extends EditorModule {
	private static final String TAG = "DevelopmentSpeedupModule";

	private ProjectIOModule projectIO;

	private FileHandle projectFile;
	private FileHandle sceneFile;

	private boolean firstLoading = true;

	@Override
	public void postInit () {
		FileHandle debugFile = new FileHandle(App.APP_FOLDER_PATH).child("debug.this");

		if (debugFile.exists() == false) return;

		String[] lines = debugFile.readString().split("\\r?\\n");

		if (lines.length < 2) {
			Log.warn(TAG, "Debug file found but it's format is invalid, project won't be auto loaded");
			return;
		} else {
			Log.debug(TAG, "Debug file found, will auto load project defined in debug file");
		}

		projectFile = Gdx.files.absolute(lines[0]);
		sceneFile = Gdx.files.absolute(lines[1]);

		try {
			projectIO.load(projectFile);
		} catch (EditorException e) {
			Log.exception(e);
		}
	}

	@Subscribe
	public void handleProjectStatusEvent (ProjectStatusEvent event) {
		if (firstLoading == false || projectFile == null || sceneFile == null) return;

		if (event.status == Status.Loaded) {
			if (sceneFile.exists()) {
				App.eventBus.post(new OpenSceneRequest(sceneFile));
			}

			firstLoading = false;
		}
	}
}
