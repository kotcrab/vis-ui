/*
 * Copyright 2014-2015 Pawel Pastuszak
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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * Makes loading VisUI skin easier
 * @author Pawel Pastuszak
 */
public class VisUI {
	public static final String VERSION = "0.6.2";

	private static int defaultTitleAlign = Align.left;
	private static Skin skin;

	/** Loads default VisUI skin from library */
	public static void load () {
		load(Gdx.files.classpath("com/kotcrab/vis/ui/uiskin.json"));
	}

	/** Loads provided skin, skin must be compatible with default VisUI skin */
	public static void load (FileHandle visSkinFile) {
		skin = new Skin(visSkinFile);
	}

	public static Skin getSkin () {
		return skin;
	}

	/** Unloads skin */
	public static void dispose () {
		if (skin != null) {
			skin.dispose();
			skin = null;
		}
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
}
