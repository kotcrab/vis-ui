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
import com.kotcrab.vis.editor.assets.AssetUsageAnalyzer;
import com.kotcrab.vis.editor.assets.BasicAssetUsageAnalyzer;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;

//FIXME
public class AssetsUsageAnalyzerModule extends ProjectModule {
	public static final int USAGE_SEARCH_LIMIT = 100;

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private SceneCacheModule sceneCache;

	private Array<AssetUsageAnalyzer> analyzers = new Array<>();

	@Override
	public void init () {
		analyzers.add(new BasicAssetUsageAnalyzer());
	}

	public boolean canAnalyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);

		for (AssetUsageAnalyzer analyzer : analyzers) {
			//if (analyzer.canAnalyze(path)) return true;
		}

		for (ObjectSupport support : supportModule.getSupports()) {
			//if(support.getAssetsUsageAanalyzer() != null && support.getAssetsUsageAanalyzer().canAnalyze(path)) return true;
		}

		return false;
	}

	public AssetsUsages analyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file.path());

		Array<FileHandle> sceneFiles = fileAccess.getSceneFiles();

		AssetsUsages usages = new AssetsUsages();
		usages.file = file;

		for (FileHandle sceneFile : sceneFiles) {
			EditorScene scene = sceneCache.get(sceneFile);

			Array<EditorObject> sceneUsagesList = new Array<>();

			for (EditorObject entity : scene.entities) {
				boolean used = false;

				//AssetsUsageAnalyzer analyzer = findAnalyzer();
				if (entity.getAssetDescriptor() != null)
					if (entity.getAssetDescriptor().equals(path)) used = true;

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
}
