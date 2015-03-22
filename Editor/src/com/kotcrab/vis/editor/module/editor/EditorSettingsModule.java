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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisTable;

public abstract class EditorSettingsModule<T> extends EditorModule implements SettableModule {
	private String name;
	private String settingsFileName;
	private Class<T> configClass;

	private EditorSettingsIOModule settingsIO;

	protected Table settingsTable;

	public T config;

	public EditorSettingsModule (String name, String settingsFileName, Class<T> configClass) {
		this.name = name;
		this.settingsFileName = settingsFileName;
		this.configClass = configClass;
	}

	@Override
	public String getSettingsName () {
		return name;
	}

	@Override
	public void init () {
		settingsIO = container.get(EditorSettingsIOModule.class);
		config = settingsIO.load(settingsFileName, configClass);
		buildTable();
		loadConfigToTable();
	}

	@Override
	public Table getSettingsTable () {
		return settingsTable;
	}

	protected void settingsSave () {
		settingsIO.save(config, settingsFileName);
	}

	protected void prepareTable () {
		settingsTable = new VisTable(true);
		settingsTable.clear();
		settingsTable.left().top();
		settingsTable.defaults().expandX().left();
	}

	public abstract void buildTable ();

	public abstract void loadConfigToTable ();
}
