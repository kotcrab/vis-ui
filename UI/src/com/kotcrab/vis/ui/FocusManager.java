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

package com.kotcrab.vis.ui;

/**
 * Manages focus of VisUI components
 * @author Kotcrab
 */
public class FocusManager {
	private static Focusable focusedWidget;

	/**
	 * Takes focus from current focused widget (if any), and sets current focused widget to provided widget
	 * @param widget that will acquire focus
	 */
	public static void getFocus (Focusable widget) {
		if (focusedWidget != null) focusedWidget.focusLost();
		focusedWidget = widget;
		focusedWidget.focusGained();
	}

	/** Takes focus from current focused widget (if any), and sets current focused widget to null */
	public static void getFocus () {
		if (focusedWidget != null) focusedWidget.focusLost();
		focusedWidget = null;
	}

	public static Focusable getFocusedWidget () {
		return focusedWidget;
	}
}
