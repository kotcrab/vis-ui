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

package com.kotcrab.vis.runtime.font;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.data.TextData;

/**
 * FreeType font provider. This is not enabled by default because FreeType dependencies may not be available is user doesn't add them.
 * To enable FreeType fonts you must call: {@code visAssetManger.enableFreeType(new FreeTypeFontProvider());}
 */
public class FreeTypeFontProvider implements FontProvider {
	@Override
	public void load (Array<AssetDescriptor> dependencies, TextData data) {
		FreeTypeFontLoaderParameter params = new FreeTypeFontLoaderParameter();

		PathAsset pathAsset = (PathAsset) data.assetDescriptor;

		params.fontFileName = pathAsset.getPath();
		params.fontParameters.size = data.fontSize;

		dependencies.add(new AssetDescriptor(data.arbitraryFontName, BitmapFont.class, params));
	}

	@Override
	public void setLoaders (AssetManager assetManager) {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
	}
}
