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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/**
 * Generic font provider interface. This allow to abstract loading FreeType fonts which may not be available if user don't
 * have FreeType dependency in his project.
 * @author Kotcrab
 * @see BitmapFontProvider
 * @see FreeTypeFontProvider
 */
public interface FontProvider {
	/** Called when font provider should check TextData and add required font dependency into dependencies list. */
	void load (Array<AssetDescriptor> dependencies, VisAssetDescriptor asset);

	/** Called when FontProvider should add all required loaders into {@link AssetManager} */
	void setLoaders (AssetManager manager);
}
