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

package com.kotcrab.vis.editor.assets.transaction.action;

import com.artemis.Component;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.assets.AssetDescriptorProvider;
import com.kotcrab.vis.editor.assets.transaction.AssetProviderResult;
import com.kotcrab.vis.editor.entity.EntityScheme;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;

/**
 * Undoable action for updating assets references in Entities. This is generic case usage that replaces provided source {@link VisAssetDescriptor} with provided target.
 * @author Kotcrab
 */
public class UpdateReferencesAction implements UndoableAction {
	private SceneCacheModule sceneCache;
	private FileAccessModule fileAccess;

	private AssetDescriptorProvider assetProvider;
	private VisAssetDescriptor source;
	private VisAssetDescriptor target;

	public UpdateReferencesAction (ModuleInjector injector, AssetProviderResult providerResult, VisAssetDescriptor target) {
		injector.injectModules(this);
		this.assetProvider = providerResult.provider;
		this.source = providerResult.descriptor;
		this.target = target;
	}

	@Override
	public void execute () {
		swapAssets(source, target);
	}

	@Override
	public void undo () {
		swapAssets(target, source);
	}

	private void swapAssets (VisAssetDescriptor asset1, VisAssetDescriptor asset2) {
		for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
			EditorScene scene = sceneCache.get(sceneFile);

			for (EntityScheme scheme : scene.getSchemes()) {
				for (Component component : scheme.getComponents()) {
					if (component instanceof AssetReference) {
						AssetReference assetRef = (AssetReference) component;
						if (assetRef.asset.compare(asset1)) {
							assetRef.asset = assetProvider.parametrize(asset2, assetRef.asset);
						}
					}
				}
			}
		}
	}
}
