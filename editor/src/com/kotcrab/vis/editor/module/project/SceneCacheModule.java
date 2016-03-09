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
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.vis.EditorException;

/**
 * Caches loaded scenes, so only one instance of each scene is loaded in editor.
 * @author Kotcrab
 */
public class SceneCacheModule extends ProjectModule implements WatchListener {
	private SceneIOModule sceneIO;
	private AssetsWatcherModule assetsWatcherModule;

	private Stage stage;

	private ObjectMap<FileHandle, EditorScene> scenes = new ObjectMap<>();

	public EditorScene get (FileHandle file) {
		EditorScene scene = scenes.get(file);

		if (scene == null) {
			scene = sceneIO.load(file);
			scenes.put(file, scene);
		}

		return scene;
	}

	public EditorScene getSafely (FileHandle file) throws EditorException {
		try {
			return get(file);
		} catch (Exception e) {
			throw new EditorException("Error occurred during scene loading", e);
		}
	}

	@Override
	public void init () {
		assetsWatcherModule.addListener(this);
	}

	@Override
	public void dispose () {
		assetsWatcherModule.removeListener(this);
	}

	@Override
	public void fileDeleted (FileHandle file) {
		scenes.remove(file);
	}
}
