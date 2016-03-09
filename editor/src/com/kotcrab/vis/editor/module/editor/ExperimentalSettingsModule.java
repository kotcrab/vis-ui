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

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.module.editor.ExperimentalSettingsModule.ExperimentalConfig;
import com.kotcrab.vis.ui.widget.VisCheckBox;

/**
 * VisEditor experimental settings module
 * @author Kotcrab
 */
public class ExperimentalSettingsModule extends EditorSettingsModule<ExperimentalConfig> {
	private VisCheckBox uiScaleCheck;

	public ExperimentalSettingsModule () {
		super("Experimental", "experimentalSettings", ExperimentalConfig.class);
	}

	@Override
	public void buildTable () {
		prepareTable();

		settingsTable.defaults().left();
		settingsTable.add("This sections contains experimental editor\nsettings.\n\nEditor restart will be required to apply\nchanges.").row();
		settingsTable.add(uiScaleCheck = new VisCheckBox("Enable 200% UI scaling\n(for high resolution displays)", config.uiScale)).row();
	}

	@Override
	public void loadConfigToTable () {
		uiScaleCheck.setChecked(config.uiScale);
	}

	@Override
	public void settingsApply () {
		config.uiScale = uiScaleCheck.isChecked();
		settingsSave();
	}

	public boolean isUIScale () {
		return config.uiScale;
	}

	@Override
	public int getListPriority () {
		return PRIORITY_LOWEST;
	}

	public static class ExperimentalConfig {
		@Tag(0) boolean uiScale = false;
	}
}
