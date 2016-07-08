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

import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * References ttf font file used to generate {@link BitmapFont}.
 * @author Kotcrab
 * @see BmpFontAsset
 */
public class TtfFontAsset extends PathAsset {
	private int fontSize;

	@Deprecated
	public TtfFontAsset () {
	}

	public TtfFontAsset (String relativePath, int fontSize) {
		super(relativePath);
		this.fontSize = fontSize;
	}

	public int getFontSize () {
		return fontSize;
	}

	public String getArbitraryFontName () {
		return String.valueOf(fontSize) + "." + getPath();
	}
}
