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
import com.kotcrab.vis.editor.scene.*;
import com.kotcrab.vis.editor.util.FileUtils;

import java.util.Iterator;

public class AssetsUsageAnalyzerModule extends ProjectModule {
	public static final int USAGE_SEARCH_LIMIT = 100;

	private FileAccessModule fileAccess;
	private SceneTabsModule sceneTabsModule;
	private SceneIOModule sceneIOModule;

	private FileHandle sceneDir;

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		sceneTabsModule = projectContainer.get(SceneTabsModule.class);
		sceneIOModule = projectContainer.get(SceneIOModule.class);

		sceneDir = fileAccess.getSceneFolder();
	}

	public boolean canAnalyze (FileHandle file) {
		return guessEntityType(fileAccess.relativizeToAssetsFolder(file)) != null;
	}

	public AssetsUsages analyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file.path());
		Class clazz = guessEntityType(path);

		if (clazz == null) throw new IllegalArgumentException("Cannot analyze usages of file: " + file.path());

		Array<FileHandle> sceneFiles = getSceneFiles();

		AssetsUsages usages = new AssetsUsages();
		usages.file = file;

		for (FileHandle sceneFile : sceneFiles) {
			EditorScene scene = sceneTabsModule.getSceneByPath(fileAccess.relativizeToAssetsFolder(sceneFile.path()));
			if (scene == null)
				scene = sceneIOModule.load(sceneFile);

			Array<EditorObject> sceneUsagesList = new Array<>();

			for (EditorObject entity : scene.entities) {

				if (clazz.isInstance(entity)) {
					boolean used = false;

					if (entity.getAssetDescriptor() != null)
						if (entity.getAssetPath().equals(path)) used = true;

					if (used) {
						usages.count++;
						sceneUsagesList.add(entity);
					}
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

	private Class guessEntityType (String path) {
		if (path.startsWith("gfx")) return SpriteObject.class;
		if (path.startsWith("font") || path.startsWith("bmpfont")) return TextObject.class;
		if (path.startsWith("particle")) return ParticleObject.class;
		if (path.startsWith("music")) return MusicObject.class;

		return null;
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
