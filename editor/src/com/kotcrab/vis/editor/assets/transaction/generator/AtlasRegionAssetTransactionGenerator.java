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

package com.kotcrab.vis.editor.assets.transaction.generator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.TextureAtlasData.Page;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.assets.transaction.AssetProviderResult;
import com.kotcrab.vis.editor.assets.transaction.AssetTransaction;
import com.kotcrab.vis.editor.assets.transaction.AssetTransactionGenerator;
import com.kotcrab.vis.editor.assets.transaction.action.CopyFileAction;
import com.kotcrab.vis.editor.assets.transaction.action.DeleteFileAction;
import com.kotcrab.vis.editor.assets.transaction.action.UpdateReferencesAction;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/** Asset transaction generator for {@link AtlasRegionAsset} */
public class AtlasRegionAssetTransactionGenerator implements AssetTransactionGenerator {
	private FileHandle transactionStorage;

	@Override
	public void setTransactionStorage (FileHandle transactionStorage) {
		this.transactionStorage = transactionStorage;
	}

	@Override
	public boolean isSupported (VisAssetDescriptor descriptor, FileHandle file) {
		return descriptor instanceof AtlasRegionAsset && ProjectPathUtils.isTextureAtlas(file);
	}

	@Override
	public AssetTransaction analyze (ModuleInjector injector, AssetProviderResult providerResult, FileHandle source, FileHandle target, String relativeTargetPath) {
		Array<FileHandle> sourcePngs = new Array<>();
		Array<FileHandle> targetPngs = new Array<>();

		TextureAtlasData data = new TextureAtlasData(source, source.parent(), false);
		for (int i = 0; i < data.getPages().size; i++) {
			Page page = data.getPages().get(i);

			sourcePngs.add(page.textureFile);

			if (i == 0) {
				targetPngs.add(target.parent().child(target.nameWithoutExtension() + ".png"));
			} else {
				targetPngs.add(target.parent().child(target.nameWithoutExtension() + i + ".png"));
			}
		}

		AssetTransaction transaction = new AssetTransaction();

		transaction.add(new CopyFileAction(source, target));

		for (int i = 0; i < sourcePngs.size; i++) {
			transaction.add(new CopyFileAction(sourcePngs.get(i), targetPngs.get(i)));
		}

		transaction.add(new UpdateReferencesAction(injector, providerResult, new AtlasRegionAsset(relativeTargetPath, null)));
		transaction.add(new DeleteFileAction(source, transactionStorage));
		transaction.add(new UndoableAction() { //update references in atlas file
			boolean updatingRefs = true;

			@Override
			public void execute () {
				try {
					BufferedReader file = new BufferedReader(new FileReader(target.file()));
					String line;
					String output = "";

					while ((line = file.readLine()) != null) {
						if (updatingRefs && line.contains(":")) updatingRefs = false;

						if (updatingRefs) {
							for (int i = 0; i < sourcePngs.size; i++) {
								if (line.equals(sourcePngs.get(i).name()))
									line = targetPngs.get(i).name();
							}
						}

						output += line + '\n';
					}

					file.close();

					FileOutputStream fileOut = new FileOutputStream(target.file());
					fileOut.write(output.getBytes());
					fileOut.close();
				} catch (IOException e) {
					Log.exception(e);
				}

			}

			@Override
			public void undo () {
				//do nothing, file will deleted when CopyFileAction will be undone
			}
		});

		for (int i = 0; i < sourcePngs.size; i++) {
			transaction.add(new DeleteFileAction(sourcePngs.get(i), transactionStorage));
		}

		transaction.finalizeGroup();

		return transaction;
	}
}
