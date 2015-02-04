/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module;

import com.kotcrab.vis.editor.module.GeneralSettingsModule.GeneralConfig;
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
