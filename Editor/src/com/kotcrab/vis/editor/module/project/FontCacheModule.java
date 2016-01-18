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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent.ResourceType;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

import java.util.EnumSet;

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

	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcherModule;

	private ObjectMap<FileHandle, BmpFontsMap> bmpFonts = new ObjectMap<>();
	private ObjectMap<FileHandle, TmpFontsMap> ttfFonts = new ObjectMap<>();

	@Override
	public void init () {
		watcherModule.addListener(this);
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);

		for (BmpFontsMap font : bmpFonts.values())
			font.dispose();

		for (TmpFontsMap font : ttfFonts.values())
			font.dispose();
	}

	@Override
	public void fileChanged (FileHandle file) {
		if (file.extension().equals("ttf")) refreshTtfFont(file);
		if (file.extension().equals("fnt")) refreshBmpFont(file);
	}

	private void refreshTtfFont (FileHandle file) {
		TmpFontsMap font = ttfFonts.remove(file);
		if (font != null) font.dispose();

		App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.TTF_FONTS)));
	}

	private void refreshBmpFont (FileHandle file) {
		BmpFontsMap bmpFont = bmpFonts.remove(file);
		if (bmpFont != null) bmpFont.dispose();

		App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.BMP_FONTS)));
	}

	public BitmapFont getGeneric (VisAssetDescriptor asset, float pixelsPerUnit) {
		if (asset instanceof BmpFontAsset)
			return get((BmpFontAsset) asset, pixelsPerUnit);

		if (asset instanceof TtfFontAsset)
			return get((TtfFontAsset) asset, pixelsPerUnit);

		throw new UnsupportedAssetDescriptorException(asset);
	}

	public BitmapFont get (TtfFontAsset asset, float pixelsPerUnit) {
		if (asset.getFontSize() == -1) throw new IllegalArgumentException("Invalid font size: -1");

		FileHandle file = fileAccess.getAssetsFolder().child(asset.getPath());

		TmpFontsMap fontsMap = ttfFonts.get(file);
		if (fontsMap == null) {
			fontsMap = new TmpFontsMap();
			ttfFonts.put(file, fontsMap);
		}

		TtfEditorFont ttfFont = fontsMap.get(file, pixelsPerUnit);
		return ttfFont.get(asset.getFontSize());
	}

	public BitmapFont get (BmpFontAsset asset, float pixelsPerUnit) {
		FileHandle file = fileAccess.getAssetsFolder().child(asset.getPath());

		BmpFontsMap fontsMap = bmpFonts.get(file);
		if (fontsMap == null) {
			fontsMap = new BmpFontsMap();
			bmpFonts.put(file, fontsMap);
		}

		return fontsMap.get(file, pixelsPerUnit);
	}

	/** Maps various pixelsInUnit values to its {@link BitmapFont} */
	private static class BmpFontsMap implements Disposable {
		public ObjectMap<Float, BitmapFont> fonts = new ObjectMap<>();

		@Override
		public void dispose () {
			for (BitmapFont font : fonts.values())
				font.dispose();
		}

		public BitmapFont get (FileHandle file, float pixelsPerUnit) {
			BitmapFont font = fonts.get(pixelsPerUnit);

			if (font == null) {
				Texture texture = new Texture(FileUtils.sibling(file, "png"), true);
				texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);

				font = new BitmapFont(new BitmapFontData(file, false), new TextureRegion(texture), false);
				font.getData().setScale(1f / pixelsPerUnit);

				fonts.put(pixelsPerUnit, font);
			}

			return font;
		}
	}

	/** Maps various pixelsInUnit values to its {@link TtfEditorFont} */
	private static class TmpFontsMap implements Disposable {
		public ObjectMap<Float, TtfEditorFont> fonts = new ObjectMap<>();

		@Override
		public void dispose () {
			for (TtfEditorFont font : fonts.values())
				font.dispose();
		}

		public TtfEditorFont get (FileHandle file, float pixelsPerUnit) {
			TtfEditorFont font = fonts.get(pixelsPerUnit);

			if (font == null) {
				font = new TtfEditorFont(file, pixelsPerUnit);
				fonts.put(pixelsPerUnit, font);
			}

			return font;
		}
	}
}
