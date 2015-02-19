/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.module.scene.SceneMetadata;

public class SceneMetadataModule extends ProjectModule {
	private FileHandle configFile;

	private Json json;

	private SceneMetadataList metadata;

	@Override
	public void init () {
		json = new Json();

		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);

		FileHandle moduleFolder = fileAccess.getModuleFolder(".metadata");
		configFile = moduleFolder.child("sceneMetadata");

		if (configFile.exists())
			metadata = json.fromJson(SceneMetadataList.class, configFile);
		else {
			metadata = new SceneMetadataList();
			metadata.map = new ObjectMap<>();
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
		json.toJson(metadata, configFile);
	}

	public static class SceneMetadataList {
		public ObjectMap<String, SceneMetadata> map;

		public SceneMetadataList () {
		}
	}
}
