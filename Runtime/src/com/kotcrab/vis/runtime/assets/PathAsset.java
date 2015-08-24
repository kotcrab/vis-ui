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

package com.kotcrab.vis.runtime.assets;

import com.kotcrab.vis.runtime.util.annotation.VisTag;

/**
 * References assets only by its path, for example used to reference music, sound or particles.
 * @author Kotcrab
 */
public class PathAsset implements VisAssetDescriptor {
	@VisTag(0) private String relativePath;

	protected PathAsset () {
	}

	public PathAsset (String relativePath) {
		this.relativePath = relativePath.replace("\\", "/");
	}

	public String getPath () {
		return relativePath;
	}

	@Override
	public boolean compare (VisAssetDescriptor asset) {
		if (asset instanceof PathAsset == false) return false;
		return relativePath.equals(((PathAsset) asset).getPath());
	}
}
