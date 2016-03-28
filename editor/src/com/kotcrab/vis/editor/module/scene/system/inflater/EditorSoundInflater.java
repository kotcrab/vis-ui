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

package com.kotcrab.vis.editor.module.scene.system.inflater;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.scene.AssetsLoadingMonitorModule;
import com.kotcrab.vis.runtime.assets.SoundAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.component.AssetReference;
import com.kotcrab.vis.runtime.component.VisSound;
import com.kotcrab.vis.runtime.component.proto.ProtoVisSound;
import com.kotcrab.vis.runtime.system.inflater.InflaterSystem;

/** @author Kotcrab */
public class EditorSoundInflater extends InflaterSystem {
	private FileAccessModule fileAccessModule;
	private AssetsLoadingMonitorModule loadingMonitor;

	private ComponentMapper<AssetReference> assetCm;
	private ComponentMapper<ProtoVisSound> protoCm;
	private ComponentMapper<VisSound> soundCm;

	public EditorSoundInflater () {
		super(Aspect.all(ProtoVisSound.class, AssetReference.class));
	}

	@Override
	public void inserted (int entityId) {
		VisAssetDescriptor assetDescriptor = assetCm.get(entityId).asset;

		if (assetDescriptor instanceof SoundAsset) {
			String path = ((SoundAsset) assetDescriptor).getPath();
			boolean exists = fileAccessModule.getAssetsFolder().child(path).exists();
			if (exists == false) {
				String errorMsg = "Sound file does not exist: " + path;
				Log.fatal(errorMsg);
				loadingMonitor.addFailedResource(assetDescriptor, new IllegalStateException(errorMsg));
			}
		} else {
			throw new IllegalStateException("Unsupported asset descriptor for sound inflater: " + assetDescriptor);
		}

		soundCm.create(entityId);
		protoCm.remove(entityId);
	}
}
