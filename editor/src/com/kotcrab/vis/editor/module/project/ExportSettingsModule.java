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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.extension.DefaultExporter;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.project.ExportSettingsModule.ExportConfig;
import com.kotcrab.vis.editor.plugin.api.ExporterPlugin;
import com.kotcrab.vis.editor.util.scene2d.TableBuilder;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.UUID;

/** @author Kotcrab */
public class ExportSettingsModule extends ProjectSettingsModule<ExportConfig> {
	private ExtensionStorageModule extensionStorage;
	private Stage stage;

	private ExportersManagerModule exportersManager;

	private Array<String> exporters = new Array<>();

	private VisSelectBox<String> exporterSelector; //TODO change to uuid after libgdx supports name providers

	public ExportSettingsModule () {
		super("Export", "exportSettings", ExportConfig.class);
	}

	@Override
	public void init () {
		for (ExporterPlugin exporterPlugin : extensionStorage.getExporterPlugins()) {
			exporters.add(exporterPlugin.getName());
		}
		super.init();
	}

	@Override
	public void buildTable () {
		prepareTable();

		exporterSelector = new VisSelectBox<>();
		exporterSelector.setItems(exporters);

		VisTextButton exporterSettingsButton = new VisTextButton("Settings");

		settingsTable.defaults().left();
		settingsTable.add(TableBuilder.build(new VisLabel("Exporter"), exporterSelector, exporterSettingsButton));

		exporterSettingsButton.addListener(new VisChangeListener((event, actor) -> {
			UUID uuid = getUUIDForName(exporterSelector.getSelected());
			ExporterPlugin exporter = exportersManager.getExportersMap().get(uuid);

			if (exporter.isSettingsUsed() == false)
				Dialogs.showOKDialog(stage, "Message", "This exporter does not have any additional settings");
			else
				exporter.showSettings();
		}));
	}

	@Override
	public void loadConfigToTable () {
		exporterSelector.setSelected(exportersManager.getExportersMap().get(config.activeExporter).getName());
	}

	@Override
	public void settingsApply () {
		config.activeExporter = getUUIDForName(exporterSelector.getSelected());
		settingsSave();
	}

	private UUID getUUIDForName (String name) {
		for (ExporterPlugin plugin : exportersManager.getExportersMap().values()) {
			if (plugin.getName().equals(name)) {
				return plugin.getUUID();
			}
		}

		throw new IllegalStateException("Could not found exporter for name: " + name);
	}

	public UUID getCurrentExporterUUID () {
		return config.activeExporter;
	}

	public static class ExportConfig {
		public UUID activeExporter = UUID.fromString(DefaultExporter.EXPORTER_UUID);
	}
}
