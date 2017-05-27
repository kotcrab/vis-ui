/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Allows to easily load VisUI skin and change default title alignment and I18N bundles.
 * Contains static field with VisUI version.
 * @author Kotcrab
 */
public class VisUI {
	/** Current VisUI version, does not include SNAPSHOT even if this version is snapshot. */
	public static final String VERSION = "1.3.1";

	private static final String TARGET_GDX_VERSION = "1.9.6";
	private static boolean skipGdxVersionCheck = false;

	private static int defaultTitleAlign = Align.left;

	private static SkinScale scale;
	private static Skin skin;

	/** Defines possible built-in skin scales. */
	public enum SkinScale {
		/** Standard VisUI skin */
		X1("com/kotcrab/vis/ui/skin/x1/uiskin.json", "default"),
		/** VisUI skin 2x upscaled */
		X2("com/kotcrab/vis/ui/skin/x2/uiskin.json", "x2");

		private final String classpath;
		private final String sizesName;

		SkinScale (String classpath, String sizesName) {
			this.classpath = classpath;
			this.sizesName = sizesName;
		}

		public FileHandle getSkinFile () {
			return Gdx.files.classpath(classpath);
		}

		public String getSizesName () {
			return sizesName;
		}
	}

	/** Loads default VisUI skin with {@link SkinScale#X1}. */
	public static void load () {
		load(SkinScale.X1);
	}

	/** Loads default VisUI skin for given {@link SkinScale}. */
	public static void load (SkinScale scale) {
		VisUI.scale = scale;
		load(scale.getSkinFile());
	}

	/** Loads skin from provided internal file path. Skin must be compatible with default VisUI skin. */
	public static void load (String internalVisSkinPath) {
		load(Gdx.files.internal(internalVisSkinPath));
	}

	/** Loads skin from provided file. Skin must be compatible with default VisUI skin. */
	public static void load (FileHandle visSkinFile) {
		checkBeforeLoad();
		VisUI.skin = new Skin(visSkinFile);
	}

	/**
	 * Sets provided skin as default for every VisUI widget. Skin must be compatible with default VisUI skin. This
	 * can be used if you prefer to load skin manually for example by using {@link AssetManager}.
	 */
	public static void load (Skin skin) {
		checkBeforeLoad();
		VisUI.skin = skin;
	}

	private static void checkBeforeLoad () {
		if (skin != null) throw new GdxRuntimeException("VisUI cannot be loaded twice");
		if (skipGdxVersionCheck == false && Version.VERSION.equals(TARGET_GDX_VERSION) == false) {
			Gdx.app.log("VisUI", "Warning, using invalid libGDX version for VisUI " + VERSION + ".\n" +
					"You are using libGDX " + Version.VERSION + " but you need " + TARGET_GDX_VERSION + ". This may cause " +
					"unexpected problems and runtime exceptions.");
		}
	}

	/** Unloads VisUI. */
	public static void dispose () {
		dispose(true);
	}

	/**
	 * Unloads VisUI.
	 * @param disposeSkin if true then internal skin instance will be disposed
	 */
	public static void dispose (boolean disposeSkin) {
		if (skin != null) {
			if (disposeSkin) skin.dispose();
			skin = null;
		}
	}

	public static Skin getSkin () {
		if (skin == null) throw new IllegalStateException("VisUI is not loaded!");
		return skin;
	}

	public static boolean isLoaded () {
		return skin != null;
	}

	public static Sizes getSizes () {
		if (scale == null)
			return getSkin().get(Sizes.class);
		else
			return getSkin().get(scale.getSizesName(), Sizes.class);
	}

	/** @return int value from {@link Align} */
	public static int getDefaultTitleAlign () {
		return defaultTitleAlign;
	}

	/**
	 * Sets default title align user for VisWindow and VisDialog
	 * @param defaultTitleAlign int value from {@link Align}
	 */
	public static void setDefaultTitleAlign (int defaultTitleAlign) {
		VisUI.defaultTitleAlign = defaultTitleAlign;
	}

	/**
	 * @param setSkipGdxVersionCheck if true VisUI won't check if provided libGDX version is compatible for current version of VisUI.
	 * If false, before loading VisUI, a libGDX version check will be performed, in case of version mismatch warning
	 * will be printed to console
	 * @see <a href="https://github.com/kotcrab/vis-editor/wiki/VisUI#libgdx-compatibility">Version compatiblity table (online)</a>
	 */
	public static void setSkipGdxVersionCheck (boolean setSkipGdxVersionCheck) {
		VisUI.skipGdxVersionCheck = setSkipGdxVersionCheck;
	}
}
