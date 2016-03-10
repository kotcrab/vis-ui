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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;
import com.kotcrab.vis.editor.module.scene.SceneMetadata;

/**
 * Providers and manages scene metadata (last camera position and zoom)
 * @author Kotcrab
 */
public class SceneMetadataModule extends ProjectModule {
	private FileAccessModule fileAccess;

	private FileHandle metadataFile;

	private Json json;

	private SceneMetadataList metadata;

	@Override
	public void init () {
		json = new Json();

		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);

		FileHandle moduleFolder = fileAccess.getModuleFolder(".metadata");
		metadataFile = moduleFolder.child("sceneMetadata");

		if (metadataFile.exists())
			metadata = json.fromJson(SceneMetadataList.class, metadataFile);
		else {
			metadata = new SceneMetadataList();
			metadata.map = new ObjectMap<>();
		}

		//clear no longer existing scenes
		Keys<String> it = metadata.map.keys().iterator();
		while (it.hasNext()) {
			String path = it.next();

			if (fileAccess.getAssetsFolder().child(path).exists() == false)
				it.remove();
		}
	}

	@Override
	public void dispose () {
		save();
	}

	public ObjectMap<String, SceneMetadata> getMap () {
		return metadata.map;
	}

	public void save () {
		json.toJson(metadata, metadataFile);
	}

	public static class SceneMetadataList {
		public ObjectMap<String, SceneMetadata> map;

		public SceneMetadataList () {
		}
	}
}
