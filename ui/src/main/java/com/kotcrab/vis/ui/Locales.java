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

package com.kotcrab.vis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.i18n.BundleText;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.ButtonBar;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

import java.util.Locale;

/**
 * Manages VisUI's I18N bundles.
 * @author Kotcrab
 * @since 1.0.0
 */
public class Locales {
	private static Locale locale = new Locale("en");
	private static I18NBundle commonBundle;
	private static I18NBundle buttonBarBundle;
	private static I18NBundle fileChooserBundle;
	private static I18NBundle dialogsBundle;
	private static I18NBundle tabbedPaneBundle;
	private static I18NBundle colorPickerBundle;

	/** Returns common I18N bundle. If current bundle is null, a default bundle is set and returned */
	public static I18NBundle getCommonBundle () {
		if (commonBundle == null) commonBundle = getBundle("com/kotcrab/vis/ui/i18n/Common");
		return commonBundle;
	}

	/**
	 * Changes common bundle. Since this bundle may be used by multiple VisUI parts it should be changed before loading VisUI.
	 * If set to null then {@link #getCommonBundle()} will return default bundle.
	 */
	public static void setCommonBundle (I18NBundle commonBundle) {
		Locales.commonBundle = commonBundle;
	}

	/** Returns I18N bundle used by {@link FileChooser}, if current bundle is null, a default bundle is set and returned */
	public static I18NBundle getFileChooserBundle () {
		if (fileChooserBundle == null) fileChooserBundle = getBundle("com/kotcrab/vis/ui/i18n/FileChooser");
		return fileChooserBundle;
	}

	/**
	 * Changes bundle used by {@link FileChooser}, will not affect already created FileChoosers.
	 * If set to null then {@link #getFileChooserBundle()} will return default bundle.
	 */
	public static void setFileChooserBundle (I18NBundle fileChooserBundle) {
		Locales.fileChooserBundle = fileChooserBundle;
	}

	/** Returns I18N bundle used by {@link Dialogs}, if current bundle is null, a default bundle is set and returned */
	public static I18NBundle getDialogsBundle () {
		if (dialogsBundle == null) dialogsBundle = getBundle("com/kotcrab/vis/ui/i18n/Dialogs");
		return dialogsBundle;
	}

	/**
	 * Changes bundle used by {@link Dialogs}, will not affect already created dialogs.
	 * If set to null then {@link #getDialogsBundle()} will return default bundle.
	 */
	public static void setDialogsBundle (I18NBundle dialogsBundle) {
		Locales.dialogsBundle = dialogsBundle;
	}

	/** Returns I18N bundle used by {@link TabbedPane}, if current bundle is null, a default bundle is set and returned */
	public static I18NBundle getTabbedPaneBundle () {
		if (tabbedPaneBundle == null) tabbedPaneBundle = getBundle("com/kotcrab/vis/ui/i18n/TabbedPane");
		return tabbedPaneBundle;
	}

	/**
	 * Changes bundle used by {@link TabbedPane}, will not affect already created TabbedPane.
	 * If set to null then {@link #getTabbedPaneBundle()} will return default bundle.
	 */
	public static void setTabbedPaneBundle (I18NBundle tabbedPaneBundle) {
		Locales.tabbedPaneBundle = tabbedPaneBundle;
	}

	/** Returns I18N bundle used by {@link ColorPicker}, if current bundle is null, a default bundle is set and returned */
	public static I18NBundle getColorPickerBundle () {
		if (colorPickerBundle == null) colorPickerBundle = getBundle("com/kotcrab/vis/ui/i18n/ColorPicker");
		return colorPickerBundle;
	}

	/**
	 * Changes bundle used by {@link ColorPicker}, will not affect already created pickers.
	 * If set to null then {@link #getColorPickerBundle()} will return default bundle.
	 */
	public static void setColorPickerBundle (I18NBundle colorPickerBundle) {
		Locales.colorPickerBundle = colorPickerBundle;
	}

	/** Returns I18N bundle used by {@link ButtonBar}, if current bundle is null, a default bundle is set and returned */
	public static I18NBundle getButtonBarBundle () {
		if (buttonBarBundle == null) buttonBarBundle = getBundle("com/kotcrab/vis/ui/i18n/ButtonBar");
		return buttonBarBundle;
	}

	/**
	 * Changes bundle used by {@link ButtonBar}, will not affect already created bars.
	 * If set to null then {@link #getButtonBarBundle()} ()} will return default bundle.
	 */
	public static void setButtonBarBundle (I18NBundle buttonBarBundle) {
		Locales.buttonBarBundle = buttonBarBundle;
	}

	/**
	 * Changes current locale, this should be done when VisUI isn't loaded yet because changing this won't affect bundles
	 * that are already loaded.
	 */
	public static void setLocale (Locale locale) {
		Locales.locale = locale;
	}

	private static I18NBundle getBundle (String path) {
		FileHandle bundleFile = Gdx.files.classpath(path);
		return I18NBundle.createBundle(bundleFile, locale);
	}

	public enum CommonText implements BundleText {
		PLEASE_WAIT("pleaseWait"),
		UNKNOWN_ERROR_OCCURRED("unknownErrorOccurred");

		private final String name;

		CommonText (final String name) {
			this.name = name;
		}

		private static I18NBundle getBundle () {
			return Locales.getCommonBundle();
		}

		@Override
		public final String getName () {
			return name;
		}

		@Override
		public final String get () {
			return getBundle().get(name);
		}

		@Override
		public final String format () {
			return getBundle().format(name);
		}

		@Override
		public final String format (final Object... arguments) {
			return getBundle().format(name, arguments);
		}

		@Override
		public final String toString () {
			return get();
		}
	}
}
