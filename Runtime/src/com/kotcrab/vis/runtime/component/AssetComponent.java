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

package com.kotcrab.vis.runtime.component;

import com.artemis.Component;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.StoresAssetDescriptor;
import com.kotcrab.vis.runtime.util.annotation.VisTag;

/**
 * Holds entity asset descriptor
 * @author Kotcrab
 */
//TODO: support generic asset component to avoid casting?
public class AssetComponent extends Component implements StoresAssetDescriptor {
	@VisTag(0) public VisAssetDescriptor asset;

	private AssetComponent () {
	}

	public AssetComponent (VisAssetDescriptor asset) {
		this.asset = asset;
	}

	@Override
	public VisAssetDescriptor getAsset () {
		return asset;
	}
}
