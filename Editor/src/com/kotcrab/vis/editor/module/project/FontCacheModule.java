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
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.assetreloaded.BmpFontReloadedEvent;
import com.kotcrab.vis.editor.event.assetreloaded.TtfFontReloadedEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

/**
 * Allows to get loaded fonts from project asset directory. Fonts can be reloaded automatically.
 * @author Kotcrab
 */
public class FontCacheModule extends ProjectModule implements WatchListener {
	/** Maximum recommenced font size, not enforced byt FontCacheModule */
	public static final int MAX_FONT_SIZE = 300;
	/** Minimum recommenced font size, not enforced byt FontCacheModule */
	public static final int MIN_FONT_SIZE = 5;

	public static final int DEFAULT_FONT_SIZE = 20;
	public static final String DEFAULT_TEXT = "The quick brown fox jumps over the lazy dog";

	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private AssetsWatcherModule watcherModule;

	private FileHandle bmpFontDirectory;
	private FileHandle ttfFontDirectory;

	private ObjectMap<FileHandle, BitmapFont> bmpFonts = new ObjectMap<>();
	private ObjectMap<FileHandle, TtfEditorFont> ttfFonts = new ObjectMap<>();

	@Override
	public void init () {
		ttfFontDirectory = fileAccess.getTTFFontFolder();
		bmpFontDirectory = fileAccess.getBMPFontFolder();

		watcherModule.addListener(this);
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);

		for (BitmapFont font : bmpFonts.values())
			font.dispose();

		for (TtfEditorFont font : ttfFonts.values())
			font.dispose();
	}

	@Override
	public void fileChanged (FileHandle file) {
		if (file.extension().equals("ttf")) refreshTtfFont(file);
		if (file.extension().equals("fnt")) refreshBmpFont(file);
	}

	private void refreshTtfFont (FileHandle file) {
		TtfEditorFont font = ttfFonts.remove(file);
		if (font != null) font.dispose();

		App.eventBus.post(new TtfFontReloadedEvent());
	}

	private void refreshBmpFont (FileHandle file) {
		BitmapFont bmpFont = bmpFonts.remove(file);
		if (bmpFont != null) bmpFont.dispose();

		App.eventBus.post(new BmpFontReloadedEvent());
	}

//	@Override
//	public void fileCreated (final FileHandle file) {
//		if (file.extension().equals("ttf")) refreshFont(file);
//	}

	public BitmapFont getGeneric (VisAssetDescriptor asset) {
		if (asset instanceof BmpFontAsset)
			return get((BmpFontAsset) asset);

		if (asset instanceof TtfFontAsset)
			return get((TtfFontAsset) asset);

		throw new UnsupportedAssetDescriptorException(asset);
	}

	public BitmapFont get (TtfFontAsset asset) {
		if (asset.getFontSize() == -1) throw new IllegalArgumentException("Invalid font size: -1");

		FileHandle file = fileAccess.getAssetsFolder().child(asset.getPath());
		TtfEditorFont ttfFont = ttfFonts.get(file);

		if (ttfFont == null) {
			ttfFont = new TtfEditorFont(file);
			ttfFonts.put(file, ttfFont);
		}

		return ttfFont.get(asset.getFontSize());
	}

	public BitmapFont get (BmpFontAsset asset) {
		FileHandle file = fileAccess.getAssetsFolder().child(asset.getPath());
		BitmapFont font = bmpFonts.get(file);

		if (font == null) {
			Texture texture = new Texture(FileUtils.sibling(file, "png"), true);
			texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

			font = new BitmapFont(file, new TextureRegion(texture), false);
			bmpFonts.put(file, font);
		}

		return font;
	}
}
