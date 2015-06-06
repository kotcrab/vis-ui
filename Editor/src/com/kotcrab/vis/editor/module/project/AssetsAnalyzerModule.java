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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.assets.BmpFontDescriptorProvider;
import com.kotcrab.vis.editor.assets.PathDescriptorProvider;
import com.kotcrab.vis.editor.assets.TextureDescriptorProvider;
import com.kotcrab.vis.editor.assets.transaction.AssetTransaction;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.BaseAssetTransactionGenerator;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ObjectSupportModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.plugin.ObjectSupport;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.ui.tab.CloseTabWhenMovingResources;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

public class AssetsAnalyzerModule extends ProjectModule {
	public static final int USAGE_SEARCH_LIMIT = 100;

	@InjectModule private ToastModule toastModule;
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ObjectSupportModule supportModule;
	@InjectModule private TabsModule tabsModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private SceneCacheModule sceneCache;

	private Array<AssetDescriptorProvider> providers = new Array<>();
	private Array<AssetTransactionGenerator> transactionsGens = new Array<>();

	private FileHandle transactionBackupRoot;

	@Override
	public void init () {
		providers.add(new PathDescriptorProvider());
		providers.add(new TextureDescriptorProvider());
		providers.add(new BmpFontDescriptorProvider());

		transactionsGens.add(new BaseAssetTransactionGenerator());

		transactionBackupRoot = fileAccess.getModuleFolder(".transactionBackup");
	}

	public boolean canAnalyzeUsages (FileHandle file) {
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

	@Override
	public void dispose () {
		for (FileHandle file : transactionBackupRoot.list()) {
			file.deleteDirectory();
		}
	}

	public AssetsUsages analyzeUsages (FileHandle file) {
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

	public boolean isSafeFileMoveSupported (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);
		return getTransactionGen(file, path) != null;
	}

	private AssetTransactionGenerator getTransactionGen (FileHandle file, String relativePath) {
		VisAssetDescriptor assetDescriptor = provideDescriptor(file, relativePath);
		if (assetDescriptor != null) {

			for (AssetTransactionGenerator gen : transactionsGens) {
				if (gen.isSupported(assetDescriptor)) return gen;
			}

			for (ObjectSupport support : supportModule.getSupports()) {
				AssetTransactionGenerator gen = support.getAssetTransactionGenerator();
				if (gen != null && gen.isSupported(assetDescriptor)) return gen;
			}
		}

		return null;
	}

	public void moveFileSafely (FileHandle source, FileHandle target) {
		toastModule.show("Some tabs must be reopened during refactoring", 3);

		if (tabsModule.getDirtyTabCount() > 0) {
			Editor.instance.getStage().addActor(new UnsavedResourcesDialog(tabsModule, () -> doFileMoving(source, target)).fadeIn());
		} else
			doFileMoving(source, target);
	}

	private void doFileMoving (FileHandle source, FileHandle target) {
		Array<Tab> tabs = tabsModule.getTabs();
		Array<CloseTabWhenMovingResources> tabsToReOpen = new Array<>();

		for (int i = 0; i < tabs.size; i++) {
			Tab tab = tabs.get(i);

			if (tab instanceof CloseTabWhenMovingResources) {
				tabsToReOpen.add((CloseTabWhenMovingResources) tab);
				tabsModule.removeTab(tab);
			}
		}

		String path = fileAccess.relativizeToAssetsFolder(source);
		AssetTransactionGenerator gen = getTransactionGen(source, path);
		if (gen == null) throw new UnsupportedOperationException("Source file cannot be moved safely");
		gen.setTransactionStorage(getNewTransactionBackup());
		AssetTransaction transaction = gen.analyze(projectContainer, provideDescriptor(source, path), source, target, fileAccess.relativizeToAssetsFolder(target));

		transaction.execute(); //TODO support for transaction execute undo

		for (CloseTabWhenMovingResources tab : tabsToReOpen)
			tab.reopenSelfAfterAssetsUpdated();
	}

	public FileHandle getNewTransactionBackup () {
		FileHandle dir;

		do {
			dir = transactionBackupRoot.child("transaction-" + MathUtils.random(1000000000));
		} while (dir.exists());

		dir.mkdirs();

		return dir;
	}
}
