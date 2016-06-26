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

package com.kotcrab.vis.runtime.assets;

/**
 * References assets only by its path, for example used to reference music, sound or particles. This class is abstract,
 * and must be extended, eg to create. {@link MusicAsset}, {@link SoundAsset} even if they do not provide any additional
 * fields or methods.
 * @author Kotcrab
 */
public abstract class PathAsset implements VisAssetDescriptor {
	private String relativePath;

	@Deprecated
	public PathAsset () {
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

	@Override
	public String toString () {
		return "PathAsset: " + relativePath;
	}
}
