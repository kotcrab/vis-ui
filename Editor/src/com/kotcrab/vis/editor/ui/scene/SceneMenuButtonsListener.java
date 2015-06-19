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

package com.kotcrab.vis.editor.ui.scene;

import com.kotcrab.vis.editor.module.editor.MenuBarModule;
import com.kotcrab.vis.ui.widget.MenuBar;

/**
 * {@link MenuBarModule} listener used to pass scene scope {@link MenuBar} events to {@link SceneTab}
 * @author Kotcrab
 */
public interface SceneMenuButtonsListener {
	void showSceneSettings ();

	void resetCamera ();

	void resetCameraZoom ();

	void undo ();

	void redo ();

	void group ();

	void ungroup ();
}
