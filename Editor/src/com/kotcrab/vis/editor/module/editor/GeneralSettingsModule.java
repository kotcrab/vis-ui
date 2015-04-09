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

package com.kotcrab.vis.editor.module.editor;

import com.kotcrab.vis.editor.module.editor.GeneralSettingsModule.GeneralConfig;
import com.kotcrab.vis.ui.widget.VisCheckBox;

public class GeneralSettingsModule extends EditorSettingsModule<GeneralConfig> {
	private VisCheckBox confirmExitCheck;

	public GeneralSettingsModule () {
		super("General", "generalSettings", GeneralConfig.class);
	}

	@Override
	public boolean settingsChanged () {
		return true;
	}

	@Override
	public void buildTable () {
		prepareTable();
		settingsTable.add(confirmExitCheck = new VisCheckBox("Confirm exit", config.confirmExit)).left();
	}

	@Override
	public void loadConfigToTable () {
		confirmExitCheck.setChecked(config.confirmExit);
	}

	@Override
	public void settingsApply () {
		config.confirmExit = confirmExitCheck.isChecked();
		settingsSave();
	}

	public boolean isConfirmExit () {
		return config.confirmExit;
	}

	public static class GeneralConfig {
		private boolean confirmExit = true;
	}
}
