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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Manages focus of VisUI components. This is different from stage2d.ui focus management. In scene2d widgets can only
 * acquire keyboard and scroll focus. VisUI focus managers allows any widget to acquire general user focus, this is used
 * mainly to manage rendering focus borders around widgets. Generally there is no need to call those method manually.
 * @author Kotcrab
 * @see Focusable
 */
public class FocusManager {
	private static Focusable focusedWidget;

	/**
	 * Takes focus from current focused widget (if any), and sets focus to provided widget
	 * @param stage if passed stage is not null then stage keyboard focus will be set to null
	 * @param widget that will acquire focus
	 */
	public static void switchFocus (Stage stage, Focusable widget) {
		if (focusedWidget == widget) return;
		if (focusedWidget != null) focusedWidget.focusLost();
		focusedWidget = widget;
		if (stage != null) stage.setKeyboardFocus(null);
		focusedWidget.focusGained();
	}

	/**
	 * Takes focus from current focused widget (if any), and sets current focused widget to null. If widgets owns
	 * keyboard focus {@link #resetFocus(Stage, Actor)} should be always preferred.
	 * @param stage if passed stage is not null then stage keyboard focus will be set to null
	 */
	public static void resetFocus (Stage stage) {
		if (focusedWidget != null) focusedWidget.focusLost();
		if (stage != null) stage.setKeyboardFocus(null);
		focusedWidget = null;
	}

	/**
	 * Takes focus from current focused widget (if any), and sets current focused widget to null
	 * @param stage if passed stage is not null then stage keyboard focus will be set to null only if current
	 * focus owner is passed actor
	 */
	public static void resetFocus (Stage stage, Actor caller) {
		if (focusedWidget != null) focusedWidget.focusLost();
		if (stage != null && stage.getKeyboardFocus() == caller) stage.setKeyboardFocus(null);
		focusedWidget = null;
	}

	public static Focusable getFocusedWidget () {
		return focusedWidget;
	}
}
