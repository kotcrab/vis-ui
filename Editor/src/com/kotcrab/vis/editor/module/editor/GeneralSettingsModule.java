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

import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.editor.GeneralSettingsModule.GeneralConfig;
import com.kotcrab.vis.editor.ui.EnumSelectBox;
import com.kotcrab.vis.editor.webapi.UpdateChannelType;
import com.kotcrab.vis.ui.widget.Tooltip;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * VisEditor general settings module
 * @author Kotcrab
 */
public class GeneralSettingsModule extends EditorSettingsModule<GeneralConfig> {
	private VisCheckBox confirmExitCheck;
	private VisCheckBox checkForUpdatesCheck;
	private EnumSelectBox<UpdateChannelType> updateChannelSelectBox;

	public GeneralSettingsModule () {
		super("General", "generalSettings", GeneralConfig.class);
	}

	@Override
	public void buildTable () {
		prepareTable();

		updateChannelSelectBox = new EnumSelectBox<>(UpdateChannelType.class);
		updateChannelSelectBox.setSelectedEnum(config.updateChannel);

		VisTable updateTable = new VisTable(true);
		updateTable.add("Update channel:");
		updateTable.add(updateChannelSelectBox);
		VisImage updateHelpImage = new VisImage(Assets.getIcon(Icons.QUESTION));
		new Tooltip(updateHelpImage, "Select update channel that will be used for update checking:\n" +
				"Stable: The most stable builds, should be bug free in theory.\n" +
				"Beta: Experimentally builds made before stable release, may contain bugs.\n" +
				"Cutting Edge: Built after every single change, expect a lot of bugs and a lot of builds.", Align.left);
		updateTable.add(updateHelpImage).size(22);

		settingsTable.defaults().left();
		settingsTable.add(confirmExitCheck = new VisCheckBox("Confirm exit", config.confirmExit)).row();
		settingsTable.add(checkForUpdatesCheck = new VisCheckBox("Check for updates", config.checkForUpdates)).row();
		settingsTable.add(updateTable);
	}

	@Override
	public void loadConfigToTable () {
		confirmExitCheck.setChecked(config.confirmExit);
		checkForUpdatesCheck.setChecked(config.checkForUpdates);
		updateChannelSelectBox.setSelectedEnum(config.updateChannel);
	}

	@Override
	public void settingsApply () {
		config.confirmExit = confirmExitCheck.isChecked();
		config.checkForUpdates = checkForUpdatesCheck.isChecked();
		config.updateChannel = updateChannelSelectBox.getSelectedEnum();
		settingsSave();
	}

	public boolean isConfirmExit () {
		return config.confirmExit;
	}

	public boolean isCheckForUpdates () {
		return config.checkForUpdates;
	}

	public UpdateChannelType getUpdateChannel () {
		return config.updateChannel;
	}

	public static class GeneralConfig {
		@Tag(0) boolean confirmExit = true;
		@Tag(1) boolean checkForUpdates = true;
		@Tag(2) UpdateChannelType updateChannel = UpdateChannelType.STABLE;
	}
}
