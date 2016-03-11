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

package com.kotcrab.vis.runtime.font;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.scene.SceneLoader;

/**
 * Bitmap font provider, allow to load distance field fonts. Enabled by default in {@link SceneLoader}.
 * @author Kotcrab
 */
public class BitmapFontProvider implements FontProvider {
	@Override
	public void load (Array<AssetDescriptor> dependencies, VisAssetDescriptor asset) {
		BmpFontAsset bmpAsset = (BmpFontAsset) asset;
		dependencies.add(new AssetDescriptor<BitmapFont>(bmpAsset.getPath(), BitmapFont.class, bmpAsset.getFontParameter()));
	}

	@Override
	public void setLoaders (AssetManager manager) {

	}
}
