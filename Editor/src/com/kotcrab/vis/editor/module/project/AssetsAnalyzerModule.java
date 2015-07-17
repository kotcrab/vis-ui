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
import com.kotcrab.vis.editor.assets.*;
import com.kotcrab.vis.editor.assets.transaction.AssetProviderResult;
import com.kotcrab.vis.editor.assets.transaction.AssetTransaction;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionException;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.generator.*;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.QuickAccessModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.module.project.AssetsUsages.SceneUsages;
import com.kotcrab.vis.editor.module.scene.AssetsUsageAnalyzerSystem;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.plugin.EditorEntitySupport;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.ui.tab.CloseTabWhenMovingResources;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.EntityEngine;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;

/**
 * Allows to analyze usages of file asset and performs asset transaction (moving or renaming asset file).
 * Not all assets type all supported, plugins can add custom {@link AssetDescriptorProvider} and
 * {@link AssetTransactionGenerator} to extend supported types.
 * @author Kotcrab
 */
public class AssetsAnalyzerModule extends ProjectModule {
	@InjectModule private ToastModule toastModule;
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private SupportModule supportModule;
	@InjectModule private TabsModule tabsModule;
	@InjectModule private SceneTabsModule sceneTabsModule;
	@InjectModule private QuickAccessModule quickAccessModule;
	@InjectModule private SceneCacheModule sceneCache;

	private Array<AssetDescriptorProvider> providers = new Array<>();
	private Array<AssetTransactionGenerator> transactionsGens = new Array<>();

	private FileHandle transactionBackupRoot;

	@Override
	public void init () {
		providers.add(new BmpFontDescriptorProvider());
		providers.add(new PathDescriptorProvider());
		providers.add(new TextureRegionDescriptorProvider());
		providers.add(new AtlasRegionDescriptorProvider());
		providers.add(new TtfFontDescriptorProvider());

		transactionsGens.add(new AtlasRegionAssetTransactionGenerator());
		transactionsGens.add(new BasicAssetTransactionGenerator());
		transactionsGens.add(new BmpFontAssetTransactionGenerator());
		transactionsGens.add(new TextureRegionAssetTransactionGenerator());
		transactionsGens.add(new TtfAssetTransactionGenerator());

		transactionBackupRoot = fileAccess.getModuleFolder(".transactionBackup");
	}

	public boolean canAnalyzeUsages (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);

		return provideDescriptor(file, path) != null;
	}

	private AssetProviderResult provideDescriptor (FileHandle file, String relativePath) {
		for (AssetDescriptorProvider provider : providers) {
			VisAssetDescriptor desc = provider.provide(file, relativePath);
			if (desc != null) return new AssetProviderResult(provider, desc);
		}

		for (EditorEntitySupport support : supportModule.getSupports()) {
			Array<AssetDescriptorProvider> providers = support.getAssetDescriptorProviders();

			if (providers != null) {
				for (AssetDescriptorProvider provider : providers) {
					VisAssetDescriptor desc = provider.provide(file, relativePath);
					if (desc != null) return new AssetProviderResult(provider, desc);
				}
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
		VisAssetDescriptor searchFor = provideDescriptor(file, path).descriptor;
		AssetsUsages usages = new AssetsUsages(file);

		for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
			EditorScene scene = sceneCache.get(sceneFile);
			SceneTab sceneTab = sceneTabsModule.getTabByScene(scene);

			EntityEngine engine;

			//TODO: analyze only current opened tabs
			if (sceneTab == null) {
				//scene is not loaded, manually prepare engine and populate it
				engine = new EntityEngine();
				SceneModuleContainer.createEssentialsSystems(engine);
				engine.initialize();
				SceneModuleContainer.populateEngine(engine, scene);
			} else {
				engine = sceneTab.getSceneMC().getEntityEngine();
			}

			SceneUsages sceneUsages = new SceneUsages(scene);
			engine.getSystem(AssetsUsageAnalyzerSystem.class).collectUsages(sceneUsages.ids, searchFor);
			if (sceneUsages.ids.size > 0) usages.list.add(sceneUsages);
		}

		return usages;
	}

	public boolean isSafeFileMoveSupported (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);
		return getTransactionGen(file, path) != null;
	}

	private AssetTransactionGenerator getTransactionGen (FileHandle file, String relativePath) {
		AssetProviderResult result = provideDescriptor(file, relativePath);
		if (result != null) {

			for (AssetTransactionGenerator gen : transactionsGens) {
				if (gen.isSupported(result.descriptor)) return gen;
			}

			for (EditorEntitySupport support : supportModule.getSupports()) {
				Array<AssetTransactionGenerator> gens = support.getAssetTransactionGenerators();
				if (gens != null) {
					for (AssetTransactionGenerator gen : gens) {
						if (gen.isSupported(result.descriptor)) return gen;
					}
				}
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

		Array<Tab> quickTabs = quickAccessModule.getTabs();
		for (int i = 0; i < quickTabs.size; i++) {
			Tab tab = quickTabs.get(i);
			if (tab instanceof CloseTabWhenMovingResources)
				quickAccessModule.removeTab(tab);
		}

		String path = fileAccess.relativizeToAssetsFolder(source);
		AssetTransactionGenerator gen = getTransactionGen(source, path);
		if (gen == null) throw new UnsupportedOperationException("Source file cannot be moved safely");
		gen.setTransactionStorage(getNewTransactionBackup());

		AssetTransaction transaction = null;

		try {
			AssetProviderResult result = provideDescriptor(source, path);
			transaction = gen.analyze(projectContainer, result, source, target, fileAccess.relativizeToAssetsFolder(target));
		} catch (AssetTransactionException e) {
			DialogUtils.showErrorDialog(Editor.instance.getStage(), "Error occurred during asset transaction preparation, nothing was changed.", e);
		}

		//TODO support for transaction execute undo
		if (transaction != null) transaction.execute();

		for (CloseTabWhenMovingResources tab : tabsToReOpen)
			tab.reopenSelfAfterAssetsUpdated();
	}

	private FileHandle getNewTransactionBackup () {
		FileHandle dir;

		do {
			dir = transactionBackupRoot.child("transaction-" + MathUtils.random(1000000000));
		} while (dir.exists());

		dir.mkdirs();

		return dir;
	}

}
