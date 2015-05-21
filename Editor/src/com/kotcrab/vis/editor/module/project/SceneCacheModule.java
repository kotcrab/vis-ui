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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.scene.EditorScene;

public class SceneCacheModule extends ProjectModule {
	@InjectModule private SceneIOModule sceneIO;

	private ObjectMap<FileHandle, EditorScene> scenes = new ObjectMap<>();

	public EditorScene get (FileHandle fullPath) {
		EditorScene scene = scenes.get(fullPath);

		if(scene == null) {
			scene = sceneIO.load(fullPath);
			scenes.put(fullPath, scene);
		}

		return scene;
	}
}
