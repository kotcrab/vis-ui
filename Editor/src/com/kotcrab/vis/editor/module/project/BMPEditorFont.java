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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.kotcrab.vis.editor.util.FileUtils;

/**
 * Bitmap font provider
 * @author Kotcrab
 */
public class BMPEditorFont extends EditorFont {
	private BitmapFont font;

	public BMPEditorFont (FileHandle file, String relativePath) {
		super(file, relativePath);

		Texture texture = new Texture(FileUtils.sibling(file, "png"), true);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

		font = new BitmapFont(file, new TextureRegion(texture), false);
	}

	@Override
	public BitmapFont get () {
		return font;
	}

	@Override
	public BitmapFont get (int size) {
		return get();
	}

	@Override
	public void dispose () {
		font.dispose();
	}
}
