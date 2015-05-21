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
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.FileUtils;

import java.util.Iterator;

public class AssetsUsageAnalyzerModule extends ProjectModule {
	public static final int USAGE_SEARCH_LIMIT = 100;

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private SceneCacheModule sceneCache;

	private FileHandle sceneDir;

	@Override
	public void init () {
		sceneDir = fileAccess.getSceneFolder();
	}

	public boolean canAnalyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);
		if (path.startsWith("gfx")) return true;
		if (path.startsWith("font") || path.startsWith("bmpfont")) return true;
		if (path.startsWith("particle")) return true;
		if (path.startsWith("music")) return true;

		for (ObjectSupport support : supportModule.getSupports())
			if (support.canAnalyze(file, path)) return true;

		return false;
	}

	public AssetsUsages analyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file.path());

		Array<FileHandle> sceneFiles = getSceneFiles();

		AssetsUsages usages = new AssetsUsages();
		usages.file = file;

		for (FileHandle sceneFile : sceneFiles) {
			EditorScene	scene = sceneCache.get(sceneFile);

			Array<EditorObject> sceneUsagesList = new Array<>();

			for (EditorObject entity : scene.entities) {
				boolean used = false;

				if (entity.getAssetDescriptor() != null)
					if (entity.getAssetPath().equals(path)) used = true;

				if (used) {
					usages.count++;
					sceneUsagesList.add(entity);
				}

				if (usages.count == USAGE_SEARCH_LIMIT) {
					usages.limitExceeded = true;
					break;
				}
			}

			if (sceneUsagesList.size > 0)
				usages.list.put(scene, sceneUsagesList);
		}

		return usages;
	}

	public Array<FileHandle> getSceneFiles () {
		Array<FileHandle> files = FileUtils.listRecursive(sceneDir);

		Iterator<FileHandle> it = files.iterator();

		while (it.hasNext())
			if (it.next().extension().equals("scene") == false) it.remove();

		files.sort((o1, o2) -> o1.path().toLowerCase().compareTo(o2.path().toLowerCase()));

		return files;
	}

}
