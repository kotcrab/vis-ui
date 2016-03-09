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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ProjectMenuBarEvent;
import com.kotcrab.vis.editor.event.ProjectMenuBarEventType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.ClonerModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.GsonModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.scene.NewSceneDialog;
import com.kotcrab.vis.editor.util.vis.EditorRuntimeException;
import com.kotcrab.vis.runtime.scene.SceneViewport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Allows to load VisEditor scenes. This API should not be used directly, see {@link SceneCacheModule}
 * @author Kotcrab
 * @see SceneCacheModule
 */
@SuppressWarnings("rawtypes")
@EventBusSubscriber
public class SceneIOModule extends ProjectModule {
	private static final String TAG = "SceneIOModule";

	private Stage stage;
	private GsonModule gsonModule;
	private ExtensionStorageModule extensionStorage;
	private ClonerModule cloner;

	private FileAccessModule fileAccessModule;

	private Gson gson;

	private FileHandle assetsFolder;
	private FileHandle sceneBackupFolder;

	@Override
	public void init () {
		assetsFolder = fileAccessModule.getAssetsFolder();
		sceneBackupFolder = fileAccessModule.getModuleFolder(".sceneBackup");

		gson = gsonModule.getCommonGson();
	}

	@Subscribe
	public void handleProjectMenuBarEvent (ProjectMenuBarEvent event) {
		if (event.type == ProjectMenuBarEventType.SHOW_NEW_SCENE_DIALOG) {
			stage.addActor(new NewSceneDialog(projectContainer).fadeIn());
		}
	}

	public EditorScene load (FileHandle fullPathFile) {
		try {
			if (fullPathFile.length() == 0) throw new EditorRuntimeException("Scene file does not contain any data");

			BufferedReader reader = new BufferedReader(new FileReader(fullPathFile.file()));
			EditorScene scene = gson.fromJson(reader, EditorScene.class);
			scene.path = fileAccessModule.relativizeToAssetsFolder(fullPathFile);
			reader.close();

			scene.onDeserialize();

			return scene;
		} catch (IOException e) {
			throw new IllegalStateException("There was an IO error during scene loading", e);
		}
	}

	public boolean save (EditorScene scene) {
		try {
			FileWriter writer = new FileWriter(getFileHandleForScene(scene).file());
			gson.toJson(scene, writer);
			writer.close();
			return true;
		} catch (Exception e) {
			Log.exception(e);
		}

		return false;
	}

	public void create (FileHandle relativeScenePath, SceneViewport viewport, float width, float height, int pixelsPerUnit) {
		EditorScene scene = new EditorScene(relativeScenePath, viewport, width, height, pixelsPerUnit);
		save(scene);
	}

	public FileHandle getSceneBackupFolder () {
		return sceneBackupFolder;
	}

	public FileHandle getFileHandleForScene (EditorScene scene) {
		return assetsFolder.child(scene.path);
	}
}
