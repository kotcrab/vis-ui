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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Modules implementing this interface provide UI table that is displayed on Settings window.
 * See {@link EditorSettingsModule} for default implementation.
 * @author Kotcrab
 */
public interface SettableModule {
	int PRIORITY_HIGHEST = 1000;
	int PRIORITY_HIGH = 750;
	int PRIORITY_NORMAL = 500;
	int PRIORITY_LOW = 250;
	int PRIORITY_LOWEST = 0;

	/** @return table that will be displayed in settings window, this method must always return same table instance */
	Table getSettingsTable ();

	String getSettingsName ();

	boolean settingsChanged ();

	void settingsApply ();

	int getListPriority ();
}
