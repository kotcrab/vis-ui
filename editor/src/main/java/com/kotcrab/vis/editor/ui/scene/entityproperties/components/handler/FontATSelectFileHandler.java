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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler;

import com.artemis.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.FontCacheModule;
import com.kotcrab.vis.editor.module.scene.SceneAccessModule;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisText;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;

/** @author Kotcrab */
public class FontATSelectFileHandler implements ATSelectFileHandler {
	private FileAccessModule fileAccess;

	private SceneAccessModule sceneAccess;
	private FontCacheModule fontCache;

	@Override
	public void applyChanges (Entity entity, FileHandle file) {
		VisText text = entity.getComponent(VisText.class);
		AssetReference assetRef = entity.getComponent(AssetReference.class);
		VisAssetDescriptor asset = assetRef.asset;

		VisAssetDescriptor newAsset = null;

		if (asset instanceof BmpFontAsset) {
			BmpFontAsset fontAsset = (BmpFontAsset) asset;
			newAsset = new BmpFontAsset(fileAccess.relativizeToAssetsFolder(file), fontAsset.getFontParameter());
		} else if (asset instanceof TtfFontAsset) {
			TtfFontAsset fontAsset = (TtfFontAsset) asset;
			newAsset = new TtfFontAsset(fileAccess.relativizeToAssetsFolder(file), fontAsset.getFontSize());
		} else
			throw new UnsupportedAssetDescriptorException(asset);

		text.setFont(fontCache.getGeneric(newAsset, sceneAccess.getScene().pixelsPerUnit));
		assetRef.asset = newAsset;
	}

	@Override
	public String getLabelValue (Entity entity) {
		PathAsset asset = (PathAsset) entity.getComponent(AssetReference.class).getAsset();
		return asset.getPath();
	}

	@Override
	public String getAssetDirectoryDescriptorId () {
		return null;
	}
}
