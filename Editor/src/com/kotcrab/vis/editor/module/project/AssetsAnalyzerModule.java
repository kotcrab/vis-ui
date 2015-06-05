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
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.assets.BmpFontDescriptorProvider;
import com.kotcrab.vis.editor.assets.PathDescriptorProvider;
import com.kotcrab.vis.editor.assets.TextureDescriptorProvider;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

public class AssetsAnalyzerModule extends ProjectModule {
	public static final int USAGE_SEARCH_LIMIT = 100;

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private SceneCacheModule sceneCache;
	@InjectModule private TabsModule tabsModule;

	private Array<AssetDescriptorProvider> providers = new Array<>();

	@Override
	public void init () {
		providers.add(new PathDescriptorProvider());
		providers.add(new TextureDescriptorProvider());
		providers.add(new BmpFontDescriptorProvider());
	}

	public boolean canAnalyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);
		return provideDescriptor(file, path) != null;
	}

	private VisAssetDescriptor provideDescriptor (FileHandle file, String relativePath) {
		for (AssetDescriptorProvider provider : providers) {
			VisAssetDescriptor desc = provider.provide(file, relativePath);
			if (desc != null) return desc;
		}

		for (ObjectSupport support : supportModule.getSupports()) {
			AssetDescriptorProvider provider = support.getAssetDescriptorProvider();

			if (provider != null) {
				VisAssetDescriptor desc = provider.provide(file, relativePath);
				if (desc != null) return desc;
			}
		}

		return null;
	}

	public AssetsUsages analyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file.path());

		AssetsUsages usages = new AssetsUsages(file);

		for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
			EditorScene scene = sceneCache.get(sceneFile);

			Array<EditorObject> sceneUsagesList = new Array<>();

			for (EditorObject entity : scene.entities) {
				boolean used = false;

				if (entity.getAssetDescriptor() != null) {
					if (entity.getAssetDescriptor().compare(provideDescriptor(file, path))) used = true;
				}

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

	//TODO add support for plugin manipulators
	public void replacePaths (String oldRelativePath, FileHandle newRelativePath) {

//		if (tabsModule.getDirtyTabCount() > 0)
//			Editor.instance.getStage().addActor(new UnsavedResourcesDialog(tabsModule, new WindowListener() {
//				@Override
//				public void finished () {
//					doReplace(oldRelativePath, newRelativePath);
//				}
//
//			}).fadeIn());
//		else
//			doReplace(oldRelativePath, newRelativePath);

//		Array<FileHandle> sceneFiles = fileAccess.getSceneFiles();
//
//		for (FileHandle sceneFile : sceneFiles) {
//			EditorScene scene = sceneCache.get(sceneFile);
//
//			Array<EditorObject> sceneUsagesList = new Array<>();
//
//			for (EditorObject entity : scene.entities) {
//				boolean used = false;
//
//				if (entity.getAssetDescriptor() != null) {
//
//					if(entity.getAssetDescriptor().getClass() == VisAssetDescriptor.class)
//					{
//
//					}
//					else
//					{
//
//					}
//				}
//			}
//		}
	}
}
