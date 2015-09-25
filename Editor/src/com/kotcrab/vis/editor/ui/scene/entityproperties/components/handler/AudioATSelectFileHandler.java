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

package com.kotcrab.vis.editor.ui.scene.entityproperties.components.handler;

import com.artemis.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.component.AssetComponent;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;

/** @author Kotcrab */
public abstract class AudioATSelectFileHandler implements ATSelectFileHandler {
	private FileAccessModule fileAccess;

	@Override
	public void applyChanges (Entity entity, FileHandle file) {
		AssetComponent asset = entity.getComponent(AssetComponent.class);
		asset.asset = new PathAsset(fileAccess.relativizeToAssetsFolder(file));
	}

	@Override
	public String getLabelValue (Entity entity) {
		PathAsset asset = (PathAsset) entity.getComponent(AssetComponent.class).asset;
		return asset.getPath().substring((getAudioRoot() + "/").length());
	}

	protected abstract String getAudioRoot();
}
