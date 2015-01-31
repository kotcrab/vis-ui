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

import com.kotcrab.vis.ui.widget.VisCheckBox;

public class GeneralSettingsModule extends EditorSettingsModule {
	private boolean confirmExit = true;

	private VisCheckBox confirmExitCheck;

	@Override
	protected void rebuildSettingsTable () {
		settingsTable.clear();
		settingsTable.left().top();
		settingsTable.defaults().expandX().left();
		settingsTable.add(confirmExitCheck = new VisCheckBox("Confirm exit", confirmExit)).left();

		confirmExitCheck.setChecked(true);
	}

	@Override
	public String getSettingsName () {
		return "General";
	}

	@Override
	public boolean changed () {
		return true;
	}

	@Override
	public void apply () {
		confirmExit = confirmExitCheck.isChecked();
	}

	public boolean isConfirmExit () {
		return confirmExit;
	}
}
