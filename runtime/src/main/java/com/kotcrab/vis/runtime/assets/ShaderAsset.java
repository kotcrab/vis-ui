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

import com.badlogic.gdx.assets.AssetManager;

/**
 * Asset descriptor for shader programs. Each shader consists of vertex and fragment programs.
 * @author Kotcrab
 */
public class ShaderAsset implements VisAssetDescriptor {
	private String relativeVertPath;
	private String relativeFragPath;

	@Deprecated
	public ShaderAsset () {
	}

	/** Both files must be in the same folder */
	public ShaderAsset (String relativeVertPath, String relativeFragPath) {
		this.relativeVertPath = relativeVertPath.replace("\\", "/");
		this.relativeFragPath = relativeFragPath.replace("\\", "/");
	}

	public String getVertPath () {
		return relativeVertPath;
	}

	public String getFragPath () {
		return relativeFragPath;
	}

	/** Returns path without file extension that is required by {@link AssetManager} */
	public String getPathWithoutExtension () {
		return relativeFragPath.substring(0, relativeFragPath.length() - 5);
	}

	@Override
	public boolean compare (VisAssetDescriptor asset) {
		if (asset instanceof ShaderAsset == false) return false;
		if (relativeVertPath.equals(((ShaderAsset) asset).getVertPath()) == false) return false;
		return relativeFragPath.equals(((ShaderAsset) asset).getFragPath());
	}

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShaderAsset asset = (ShaderAsset) o;

		if (!relativeVertPath.equals(asset.relativeVertPath)) return false;
		return relativeFragPath.equals(asset.relativeFragPath);

	}

	@Override
	public int hashCode () {
		int result = relativeVertPath.hashCode();
		result = 31 * result + relativeFragPath.hashCode();
		return result;
	}

	@Override
	public String toString () {
		return "ShaderAsset: " + relativeFragPath;
	}
}
