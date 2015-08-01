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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.project.ExportSettingsModule.ExportConfig;
import com.kotcrab.vis.editor.plugin.ExporterPlugin;
import com.kotcrab.vis.editor.util.gdx.TableBuilder;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;

import java.util.UUID;

/** @author Kotcrab */
public class ExportSettingsModule extends ProjectSettingsModule<ExportConfig> {
	@InjectModule private ExtensionStorageModule extensionStorage;

	@InjectModule private ExportersManagerModule exportersManager;

	private Array<String> exporters = new Array<>();

	private VisSelectBox<String> exporterSelector;

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

		settingsTable.defaults().left();
		settingsTable.add(TableBuilder.build(new VisLabel("Exporter"), exporterSelector));
	}

	@Override
	public void loadConfigToTable () {
		exporterSelector.setSelected(exportersManager.getExportersMap().get(config.activeExporter).getName());
	}

	@Override
	public void settingsApply () {
		config.activeExporter = exportersManager.getExportersMap().findKey(exporterSelector.getSelected(), false);
		settingsSave();
	}

	public UUID getCurrentExporerUUID () {
		return config.activeExporter;
	}

	public static class ExportConfig {
		public UUID activeExporter = UUID.fromString(DefaultExportModule.EXPORTER_UUID);
	}
}
