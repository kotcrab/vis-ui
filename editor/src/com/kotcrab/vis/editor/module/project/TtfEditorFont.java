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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * TTF font provider that allows to get font for user provided size. TtfEditorFont can only provide fonts for one pixelsPerUnits value.
 * @author Kotcrab
 */
public class TtfEditorFont {
	private final float pixelsPerUnit;

	private FreeTypeFontGenerator generator;
	private ObjectMap<Integer, BitmapFont> bitmapFonts = new ObjectMap<>();

	public TtfEditorFont (FileHandle file, float pixelsPerUnit) {
		this.pixelsPerUnit = pixelsPerUnit;
		generator = new FreeTypeFontGenerator(file);
		get(FontCacheModule.DEFAULT_FONT_SIZE);
	}

	public BitmapFont get () {
		return get(FontCacheModule.DEFAULT_FONT_SIZE);
	}

	public BitmapFont get (int size) {
		BitmapFont font = bitmapFonts.get(size);

		if (font == null) {
			font = generator.generateFont(getParameterForSize(size));
			font.setUseIntegerPositions(false);
			font.getData().setScale(1f / pixelsPerUnit);
			bitmapFonts.put(size, font);
		}

		return font;
	}

	private FreeTypeFontParameter getParameterForSize (int size) {
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.minFilter = TextureFilter.Linear;
		parameter.magFilter = TextureFilter.Linear;
		return parameter;
	}

	public void dispose () {
		for (BitmapFont font : bitmapFonts.values())
			font.dispose();

		generator.dispose();
	}
}
