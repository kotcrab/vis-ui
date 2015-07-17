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
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.runtime.assets.BmpFontAsset;
import com.kotcrab.vis.runtime.assets.TtfFontAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

//TODO font reloading for bmp
//TODO: [artemis-wip] clean up
/**
 * Allows to get loaded fonts from project asset directory. TTF fonts can be reloaded automatically.
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

	//TODO dispose
	private ObjectMap<FileHandle, BitmapFont> bmpFonts = new ObjectMap<>();
	private ObjectMap<FileHandle, TtfEditorFont> ttfFonts = new ObjectMap<>();

//	@Deprecated private Array<EditorFont> fonts = new Array<>();

	@Override
	public void init () {
		ttfFontDirectory = fileAccess.getTTFFontFolder();
		bmpFontDirectory = fileAccess.getBMPFontFolder();

		watcherModule.addListener(this);

		//buildFonts();
	}

//	private void buildFonts () {
//		FileHandle[] ttfFiles = ttfFontDirectory.list();
//		for (FileHandle file : ttfFiles) {
//			if (file.extension().equals("ttf")) {
//				fonts.add(new TTFEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));
//			}
//		}
//
//		FileHandle[] bmpFiles = bmpFontDirectory.list();
//		for (FileHandle file : bmpFiles) {
//			if (file.extension().equals("fnt")) {
//				fonts.add(new BMPEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));
//			}
//		}
//	}
//
//	private void refreshFont (FileHandle file) {
//		//FIXME font reloading
//		EditorFont existingFont = null;
//
//		for (EditorFont font : fonts) {
//			if (font.getFile().equals(file)) {
//				existingFont = font;
//				break;
//			}
//		}
//
//		if (existingFont != null) {
//			existingFont.dispose();
//			fonts.removeValue(existingFont, true);
//		}
//
//		fonts.add(new TTFEditorFont(file, fileAccess.relativizeToAssetsFolder(file)));
//
//		//TODO: post font reloaded event
//	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
		//refreshFont(file);
	}

	@Override
	public void fileCreated (final FileHandle file) {
		//if (file.extension().equals("ttf")) refreshFont(file);
	}

//	@Deprecated
//	public EditorFont _get (VisAssetDescriptor assetDescriptor) {
//		if (assetDescriptor instanceof PathAsset == false)
//			throw new UnsupportedAssetDescriptorException(assetDescriptor);
//
//		PathAsset path = (PathAsset) assetDescriptor;
//		return _get(fileAccess.getAssetsFolder().child(path.getPath()));
//	}

	public BitmapFont getGeneric (VisAssetDescriptor asset) {
		if (asset instanceof BmpFontAsset)
			return get((BmpFontAsset) asset);

		if (asset instanceof TtfFontAsset)
			return get((TtfFontAsset) asset);

		throw new UnsupportedAssetDescriptorException(asset);
	}

	public BitmapFont get (TtfFontAsset asset) {
		if(asset.getFontSize() == -1) throw new IllegalArgumentException("Invalid font size: -1");

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

//	@Deprecated
//	public EditorFont _get (FileHandle file) {
//		for (EditorFont font : fonts) {
//			if (font.getFile().equals(file))
//				return font;
//		}
//
//		throw new IllegalStateException("Font not found, file: " + file.path());
//	}
//
//	@Deprecated
//	public BitmapFont _get (FileHandle file, int size) {
//		for (EditorFont font : fonts) {
//			if (font.getFile().equals(file))
//				return font.get(size);
//		}
//
//		throw new IllegalStateException("Font not found");
//	}

}
