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

package com.kotcrab.vis.editor.assets.transaction.generator;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.assets.transaction.AssetTransaction;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.action.CopyFileAction;
import com.kotcrab.vis.editor.assets.transaction.action.DeleteFileAction;
import com.kotcrab.vis.editor.assets.transaction.action.UpdateReferencesAction;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/**
 * Basic {@link AssetTransactionGenerator} that can generate asset transaction for {@link PathAsset} of
 * TrueType fonts, music, sounds, and particles
 * @author Kotcrab
 */
public class BasicAssetTransactionGenerator implements AssetTransactionGenerator {
	private FileHandle transactionStorage;

	@Override
	public void setTransactionStorage (FileHandle transactionStorage) {
		this.transactionStorage = transactionStorage;
	}

	@Override
	public boolean isSupported (VisAssetDescriptor descriptor) {
		if (descriptor instanceof PathAsset == false) return false;

		PathAsset pathAsset = (PathAsset) descriptor;
		String path = pathAsset.getPath();
		if (path.startsWith("music") || path.startsWith("sound") || path.startsWith("particle"))
			return true;

		return false;
	}

	@Override
	public AssetTransaction analyze (ModuleInjector injector, VisAssetDescriptor descriptor, FileHandle source, FileHandle target, String relativeTargetPath) {
		AssetTransaction transaction = new AssetTransaction();

		transaction.add(new CopyFileAction(source, target));
		transaction.add(new UpdateReferencesAction(injector, descriptor, new PathAsset(relativeTargetPath)));
		transaction.add(new DeleteFileAction(source, transactionStorage));
		transaction.finalizeGroup();

		return transaction;
	}
}
