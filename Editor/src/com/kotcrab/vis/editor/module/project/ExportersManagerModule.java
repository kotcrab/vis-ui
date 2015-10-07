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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.event.ProjectMenuBarEvent;
import com.kotcrab.vis.editor.event.ProjectMenuBarEventType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.InputModule;
import com.kotcrab.vis.editor.plugin.ExporterPlugin;
import com.kotcrab.vis.editor.util.gdx.ModalInputListener;

import java.util.UUID;

/**
 * @author Kotcrab
 */
@EventBusSubscriber
public class ExportersManagerModule extends ProjectModule {
	private InputModule inputModule;
	private ExtensionStorageModule extensionStorage;

	private ExportSettingsModule exportSettings;

	private ObjectMap<UUID, ExporterPlugin> exporters = new ObjectMap<>();

	private ExportInputListener inputListener;

	@Subscribe
	public void handleProjectMenuBarEvent (ProjectMenuBarEvent event) {
		if (event.type == ProjectMenuBarEventType.EXPORT) {
			export(false);
		}
	}

	@Override
	public void init () {
		for (ExporterPlugin exporterPlugin : extensionStorage.getExporterPlugins()) {
			//simple check to verify that exporters returns same uuid every time
			UUID u1 = exporterPlugin.getUUID();
			UUID u2 = exporterPlugin.getUUID();
			if (u1.equals(u2) == false)
				throw new IllegalStateException("ExporterPlugin must return always return same UUID");

			exporters.put(exporterPlugin.getUUID(), exporterPlugin);
			projectContainer.injectModules(exporterPlugin);
			exporterPlugin.init(project);
		}

		inputListener = new ExportInputListener();
		inputModule.addListener(inputListener);
	}

	@Override
	public void dispose () {
		inputModule.removeListener(inputListener);
	}

	public ObjectMap<UUID, ExporterPlugin> getExportersMap () {
		return exporters;
	}

	public void export (boolean quickExport) {
		exporters.get(exportSettings.getCurrentExporterUUID()).export(quickExport);
	}

	private class ExportInputListener extends ModalInputListener {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (UIUtils.ctrl() && keycode == Keys.E) {
				export(false);
				return true;
			}

			return false;
		}
	}
}
