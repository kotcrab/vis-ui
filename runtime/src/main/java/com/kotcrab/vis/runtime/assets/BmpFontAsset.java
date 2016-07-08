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

import com.badlogic.gdx.assets.loaders.BitmapFontLoader.BitmapFontParameter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * References asset used by {@link BitmapFont}. This only applies to fonts created from fnt and png files.
 * @author Kotcrab
 * @see TtfFontAsset
 */
public class BmpFontAsset extends PathAsset {
	private BitmapFontParameter fontParameter;

	@Deprecated
	public BmpFontAsset () {
	}

	public BmpFontAsset (String relativePath, BitmapFontParameter fontParameter) {
		super(relativePath);
		this.fontParameter = fontParameter;
	}

	public BitmapFontParameter getFontParameter () {
		return fontParameter;
	}
}
