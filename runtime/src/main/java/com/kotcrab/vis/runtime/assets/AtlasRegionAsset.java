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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * References single region from {@link TextureAtlas}. RegionName is ignored during assets comparison.
 * @author Kotcrab
 */
public class AtlasRegionAsset extends PathAsset implements TextureAssetDescriptor {
	private String regionName; //ignored in descriptor compassion

	@Deprecated
	public AtlasRegionAsset () {
	}

	/** @param regionName name of region from atlas, if null the first texture of atlas itself would be used */
	public AtlasRegionAsset (String relativePath, String regionName) {
		super(relativePath);
		this.regionName = regionName;
	}

	public String getRegionName () {
		return regionName;
	}
}
