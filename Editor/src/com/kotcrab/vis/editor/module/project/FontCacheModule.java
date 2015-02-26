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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.ui.util.DialogUtils;

public class FontCacheModule extends ProjectModule implements WatchListener {
	public static final int DEFAULT_FONT_SIZE = 20;
	public static final String DEFAULT_TEXT = "Quick fox jumps over the lazy dog";

	private AssetsWatcherModule watcherModule;
	private FileHandle fontDirectory;

	private Array<EditorFont> fonts = new Array<>();

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		watcherModule = projectContainer.get(AssetsWatcherModule.class);

		FileHandle assetsDirectory = fileAccess.getAssetsFolder();
		fontDirectory = assetsDirectory.child("gfx").child("font");

		watcherModule.addListener(this);

		buildFonts();
	}

	private void buildFonts () {
		FileHandle[] files = fontDirectory.list();

		for (FileHandle file : files) {
			if (file.extension().equals("ttf")) {
				fonts.add(new EditorFont(file));
			}
		}
	}

	private void refreshFont (FileHandle file) {
		EditorFont existingFont = null;

		for (EditorFont font : fonts) {
			if (font.file.equals(file)) {
				existingFont = font;
				break;
			}
		}

		if (existingFont != null) {
			existingFont.dispose();
			fonts.removeValue(existingFont, true);
		}

		fonts.add(new EditorFont(file));

		//TODO: post font reloaded event
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
//		refreshFont(file);
	}

	@Override
	public void fileDeleted (FileHandle file) {
		if (file.extension().equals("ttf")) {
			DialogUtils.showErrorDialog(Editor.instance.getStage(), "Font " + file.name() + " unexpectedly removed, please restore font file and press OK");
		}
	}

	@Override
	public void fileCreated (FileHandle file) {
	}

	public BitmapFont get (FileHandle file, int size) {
		for (EditorFont font : fonts) {
			if (font.file.equals(file))
				return font.get(size);
		}

		throw new IllegalStateException("Font not found");
	}

	private static class EditorFont implements Disposable {
		private FileHandle file;
		private FreeTypeFontGenerator generator;
		private ObjectMap<Integer, BitmapFont> bitmapFonts = new ObjectMap<>();

		public EditorFont (FileHandle file) {
			this.file = file;
			generator = new FreeTypeFontGenerator(file);
			get(DEFAULT_FONT_SIZE);
		}

		public BitmapFont get (int size) {
			BitmapFont font = bitmapFonts.get(size);

			if (font == null) {
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = size;
				parameter.minFilter = TextureFilter.Linear;
				parameter.magFilter = TextureFilter.Linear;
				font = generator.generateFont(parameter);
				bitmapFonts.put(size, font);
			}

			return font;
		}

		@Override
		public void dispose () {
			for (BitmapFont font : bitmapFonts.values())
				font.dispose();

			generator.dispose();
		}
	}
}
