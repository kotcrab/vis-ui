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
import com.kotcrab.vis.editor.module.project.ShaderCacheModule;
import com.kotcrab.vis.runtime.assets.ShaderAsset;
import com.kotcrab.vis.runtime.component.Shader;
import com.kotcrab.vis.runtime.util.autotable.ATSelectFileHandler;

/** @author Kotcrab */
public class ShaderATSelectFileHandler implements ATSelectFileHandler {
	private FileAccessModule fileAccess;
	private ShaderCacheModule shaderCache;

	@Override
	public void applyChanges (Entity entity, FileHandle file) {
		Shader shader = entity.getComponent(Shader.class);

		String vert = null;
		String frag = null;

		if (file.extension().equals("vert")) {
			vert = fileAccess.relativizeToAssetsFolder(file);
			frag = fileAccess.relativizeToAssetsFolder(file.sibling(file.nameWithoutExtension() + ".frag"));
		}

		if (file.extension().equals("frag")) {
			frag = fileAccess.relativizeToAssetsFolder(file);
			vert = fileAccess.relativizeToAssetsFolder(file.sibling(file.nameWithoutExtension() + ".vert"));
		}

		shader.asset = new ShaderAsset(vert, frag);
		shader.shader = shaderCache.get(shader.asset);
	}

	@Override
	public String getLabelValue (Entity entity) {
		ShaderAsset asset = entity.getComponent(Shader.class).asset;
		if (asset == null) return "<no shader>";
		return asset.getPathWithoutExtension();
	}

	@Override
	public String getAssetDirectoryDescriptorId () {
		return null;
	}
}
