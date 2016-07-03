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

package com.kotcrab.vis.runtime.util;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Used to simplify extracting sprite sheet animation data from texture atlas based sprite sheet.
 * @author Kotcrab
 * @since 0.3.3
 */
public class SpriteSheetHelper {
	private TextureAtlas atlas;

	private ImmutableArray<String> animationsList;
	private ObjectMap<String, Array<TextureRegion>> animations = new ObjectMap<String, Array<TextureRegion>>();

	public SpriteSheetHelper (TextureAtlas atlas) {
		this.atlas = atlas;
		animationsList = createAnimationList();
	}

	public Array<TextureRegion> getAnimationRegions (String name) {
		if (name == null) throw new IllegalArgumentException("animation name can't be null");
		Array<TextureRegion> regions = animations.get(name);
		if (regions != null) return regions;

		Array<TextureAtlas.AtlasRegion> atlasRegions = atlas.findRegions(name);
		regions = new Array<TextureRegion>(atlasRegions.size);
		for (TextureAtlas.AtlasRegion atlasRegion : atlasRegions) {
			regions.add(atlasRegion);
		}
		animations.put(name, regions);
		return regions;
	}

	public ImmutableArray<String> getAnimationsList () {
		return animationsList;
	}

	private ImmutableArray<String> createAnimationList () {
		final Array<String> list = new Array<String>();
		for (TextureAtlas.AtlasRegion atlasRegion : atlas.getRegions()) {
			if (atlasRegion.index == -1) continue;
			if (list.contains(atlasRegion.name, false)) continue;
			list.add(atlasRegion.name);
		}

		return new ImmutableArray<String>(list);
	}
}
