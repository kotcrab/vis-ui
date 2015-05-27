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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

//TODO add support for plugin manipulators
public class EntitiesPathsManipulatorModule extends ProjectModule {
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private SceneCacheModule sceneCache;

	public void replacePaths (String oldRelativePath, FileHandle newRelativePath) {
		Array<FileHandle> sceneFiles = fileAccess.getSceneFiles();

		for (FileHandle sceneFile : sceneFiles) {
			EditorScene scene = sceneCache.get(sceneFile);

			Array<EditorObject> sceneUsagesList = new Array<>();

			for (EditorObject entity : scene.entities) {
				boolean used = false;

				if (entity.getAssetDescriptor() != null) {

					if(entity.getAssetDescriptor().getClass() == VisAssetDescriptor.class)
					{

					}
					else
					{

					}
				}
			}
		}
	}
}
