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

package com.kotcrab.vis.ui.widget.color;

import com.badlogic.gdx.graphics.Color;

/**
 * Listener for {@link ColorPicker}.
 * @author Kotcrab
 */
public interface ColorPickerListener {
	/**
	 * Called when color selection was canceled by user (either by clicking cancel or closing the window). Note that this
	 * event can only occur when using {@link ColorPicker} dialog.
	 */
	void canceled (Color oldColor);

	/**
	 * Called when currently selected color in picker has changed. This does not mean that user finished selecting color, if
	 * you are only interested in that event use {@link #finished(Color)} or {@link #canceled(Color)}.
	 */
	void changed (Color newColor);

	/**
	 * Called when selected color in picker were reset to previously select one.
	 * @param previousColor color that was set before reset.
	 * @param newColor new picker color.
	 */
	void reset (Color previousColor, Color newColor);

	/**
	 * Called when user has finished selecting new color. Note that this
	 * event can only occur when using {@link ColorPicker} dialog.
	 */
	void finished (Color newColor);
}
