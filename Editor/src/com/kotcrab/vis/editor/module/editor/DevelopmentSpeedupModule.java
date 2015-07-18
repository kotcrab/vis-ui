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
import com.esotericsoftware.kryo.KryoException;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ProjectStatusEvent;
import com.kotcrab.vis.editor.event.ProjectStatusEvent.Status;
import com.kotcrab.vis.editor.event.bus.Event;
import com.kotcrab.vis.editor.event.bus.EventListener;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.project.ProjectModuleContainer;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.module.project.SceneTabsModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.vis.EditorException;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;

/**
 * The purpose of this module is to dramatically speed up development by auto loading project and test scene. To start using it do the
 * following things: open VisEditor, click About from Help menu, click open app folder. Create file with name debug.this that
 * will contain two lines: first is full project path and the second one is full scene path.
 * @author Kotcrab
 */
public class DevelopmentSpeedupModule extends EditorModule implements EventListener {
	private static final String TAG = "DevelopmentSpeedupModule";

	@InjectModule private ProjectIOModule projectIO;

	private FileHandle projectFile;
	private FileHandle sceneFile;

	private boolean firstLoading = true;
	private ProjectModuleContainer projectMC;

	public DevelopmentSpeedupModule (ProjectModuleContainer projectMC) {
		this.projectMC = projectMC;
	}

	@Override
	public void postInit () {
		App.eventBus.register(this);

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

	@Override
	public boolean onEvent (Event event) {
		if (firstLoading == false || sceneFile == null) return false;

		if (event instanceof ProjectStatusEvent) {
			ProjectStatusEvent statusEvent = (ProjectStatusEvent) event;
			if (statusEvent.status == Status.Loaded) {

				try {
					if (sceneFile.exists()) {
						EditorScene testScene = projectMC.get(SceneCacheModule.class).get(sceneFile);
						projectMC.get(SceneTabsModule.class).open(testScene);
					}
				} catch (KryoException e) {
					DialogUtils.showErrorDialog(Editor.instance.getStage(), "Failed to load scene due to corrupted file.", e);
					Log.exception(e);
				}

				firstLoading = false;
			}
		}

		return false;
	}
}
