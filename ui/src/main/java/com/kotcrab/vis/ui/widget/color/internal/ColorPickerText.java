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

package com.kotcrab.vis.ui.widget.color.internal;

import com.badlogic.gdx.utils.I18NBundle;
import com.kotcrab.vis.ui.Locales;
import com.kotcrab.vis.ui.i18n.BundleText;

/**
 * Contains texts for chooser access via I18NBundle.
 * @author Kotcrab
 * @since 0.7.0
 */
public enum ColorPickerText implements BundleText {
	TITLE("title"),
	RESTORE("restore"),
	CANCEL("cancel"),
	OK("ok"),
	HEX("hex");

	private final String name;

	ColorPickerText (final String name) {
		this.name = name;
	}

	private static I18NBundle getBundle () {
		return Locales.getColorPickerBundle();
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
