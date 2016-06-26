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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.editor.module.editor.EditorSettingsModule;
import com.kotcrab.vis.editor.module.editor.SettableModule;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * Base class for all modules that provides settings section in VisEditor settings dialog.
 * @author Kotcrab
 * @see EditorSettingsModule
 */
public abstract class ProjectSettingsModule<T> extends ProjectModule implements SettableModule {
	private String name;
	private String settingsFileName;
	private Class<T> configClass;

	private ProjectSettingsIOModule settingsIO;

	protected Table settingsTable;

	public T config;

	public ProjectSettingsModule (String name, String settingsFileName, Class<T> configClass) {
		this.name = name;
		this.settingsFileName = settingsFileName;
		this.configClass = configClass;
	}

	@Override
	public boolean settingsChanged () {
		return true;
	}

	@Override
	public String getSettingsName () {
		return name;
	}

	@Override
	public void init () {
		settingsIO = projectContainer.get(ProjectSettingsIOModule.class);
		config = settingsIO.load(settingsFileName, configClass);
		buildTable();
		loadConfigToTable();
	}

	@Override
	public Table getSettingsTable () {
		onShow();
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

	protected void onShow () {

	}

	protected abstract void buildTable ();

	protected abstract void loadConfigToTable ();

	@Override
	public int getListPriority () {
		return PRIORITY_NORMAL;
	}
}
