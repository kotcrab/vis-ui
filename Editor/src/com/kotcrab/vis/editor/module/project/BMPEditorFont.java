/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;


public class BMPEditorFont extends EditorFont {
	private BitmapFont font;

	public BMPEditorFont (FileHandle file, String relativePath) {
		super(file, relativePath);
		font = new BitmapFont(file);
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
