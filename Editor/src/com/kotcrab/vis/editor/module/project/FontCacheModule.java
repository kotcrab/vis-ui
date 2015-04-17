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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.ui.util.dialog.DialogUtils;

//TODO font reloading for bmp
public class FontCacheModule extends ProjectModule implements WatchListener {
	/** Maximum recommenced font size, not enforced byt FontCacheModule */
	public static final int MAX_FONT_SIZE = 300;
	/** Minimum recommenced font size, not enforced byt FontCacheModule */
	public static final int MIN_FONT_SIZE = 5;

	public static final int DEFAULT_FONT_SIZE = 20;
	public static final String DEFAULT_TEXT = "The quick brown fox jumps over the lazy dog";

	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcherModule;

	private FileHandle bmpFontDirectory;
	private FileHandle ttfFontDirectory;

	private Array<EditorFont> fonts = new Array<>();

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		watcherModule = projectContainer.get(AssetsWatcherModule.class);

		ttfFontDirectory = fileAccess.getTTFFontFolder();
		bmpFontDirectory = fileAccess.getBMPFontFolder();

		watcherModule.addListener(this);

		buildFonts();
	}

	private void buildFonts () {
		FileHandle[] ttfFiles = ttfFontDirectory.list();
		for (FileHandle file : ttfFiles) {
			if (file.extension().equals("ttf")) {
				fonts.add(new TTFEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));
			}
		}

		FileHandle[] bmpFiles = bmpFontDirectory.list();
		for (FileHandle file : bmpFiles) {
			if (file.extension().equals("fnt")) {
				fonts.add(new BMPEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));
			}
		}
	}

	private void refreshFont (FileHandle file) {
		EditorFont existingFont = null;

		for (EditorFont font : fonts) {
			if (font.getFile().equals(file)) {
				existingFont = font;
				break;
			}
		}

		if (existingFont != null) {
			existingFont.dispose();
			fonts.removeValue(existingFont, true);
		}

		fonts.add(new TTFEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));

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
	public void fileCreated (final FileHandle file) {
		if (file.extension().equals("ttf")) refreshFont(file);
	}

	public EditorFont get (FileHandle file) {
		for (EditorFont font : fonts) {
			if (font.getFile().equals(file))
				return font;
		}

		throw new IllegalStateException("Font not found, file: " + file.path());
	}

	public BitmapFont get (FileHandle file, int size) {
		for (EditorFont font : fonts) {
			if (font.getFile().equals(file))
				return font.get(size);
		}

		throw new IllegalStateException("Font not found");
	}

}
