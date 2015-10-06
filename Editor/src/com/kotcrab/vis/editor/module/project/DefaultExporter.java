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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.editor.EditorSettingsIOModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.plugin.ExporterPlugin;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.ui.dialog.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.ui.dialog.DefaultExporterSettingsDialog;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.util.SteppedAsyncTask;
import com.kotcrab.vis.runtime.data.LayerData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.UUID;

/**
 * Default VisEditor exporter, exports scenes to JSON format.
 * @author Kotcrab
 */
public class DefaultExporter implements ExporterPlugin {
	public static final String SETTINGS_FILE_NAME = "defaultExporterSettings";
	public static final String EXPORTER_UUID = "b8bd183c-1dc6-4ac5-9bbe-a4ba86a61b95";

	private EditorSettingsIOModule settingsIO;
	private StatusBarModule statusBar;
	private TabsModule tabsModule;

	private FileAccessModule fileAccess;
	private SceneCacheModule sceneCache;

	private Stage stage;

	private Project project;
	private DefaultExporterSettings settings;

	private FileHandle visAssetsDir;

	private Settings texturePackerSettings;
	private boolean firstExportDone;

	private Json json;

	@Override
	public void init (Project project) {
		this.project = project;
		settings = settingsIO.load(SETTINGS_FILE_NAME, DefaultExporterSettings.class);

		visAssetsDir = fileAccess.getAssetsFolder();

		texturePackerSettings = new Settings();
		texturePackerSettings.maxHeight = 2048;
		texturePackerSettings.maxWidth = 2048;
		texturePackerSettings.combineSubdirectories = true;
		texturePackerSettings.silent = true;
		texturePackerSettings.useIndexes = false;

		json = SceneLoader.getJson();
	}

	@Override
	public UUID getUUID () {
		return UUID.fromString(EXPORTER_UUID);
	}

	@Override
	public String getName () {
		return "Default Json";
	}

	@Override
	public boolean isQuickExportSupported () {
		return false;
	}

	@Override
	public void export (boolean quick) {
		if (tabsModule.getDirtyTabCount() > 0)
			stage.addActor(new UnsavedResourcesDialog(tabsModule, () -> beforeExport(quick)).fadeIn());
		else
			beforeExport(quick);
	}

	@Override
	public boolean isSettingsUsed () {
		return true;
	}

	@Override
	public void showSettings () {
		stage.addActor(new DefaultExporterSettingsDialog(settingsIO, settings).fadeIn());
	}

	private void beforeExport (boolean quick) {
		json.setUsePrototypes(settings.skipDefaultValues);
		if(settings.useMinimalOutputType)
			json.setOutputType(OutputType.minimal);
		else
			json.setOutputType(OutputType.json);


		if (firstExportDone == false && quick)
			Log.info("Requested quick export but normal export hasn't been done since editor launch, performing normal export.");

		if (firstExportDone == false || quick == false)
			doExport();
		else
			doExport(); //TODO do quick export

		firstExportDone = true;
	}

	private void doExport () {
		if (project instanceof ProjectLibGDX || project instanceof ProjectGeneric) {
			exportProject();
			return;
		}

		throw new UnsupportedOperationException("Not supported project type: " + project.getClass());
	}

	private void exportProject () {
		ExportAsyncTask exportTask = new ExportAsyncTask();
		stage.addActor(new AsyncTaskProgressDialog("Exporting", exportTask).fadeIn());
	}

	private class ExportAsyncTask extends SteppedAsyncTask {
		FileHandle outAssetsDir;

		EditorScene scene;

		public ExportAsyncTask () {
			super("ProjectExporter");
		}

		@Override
		public void execute () {
			setMessage("Preparing for export...");
			setTotalSteps(calculateSteps());

			cleanOldAssets();
			packageTextures();
			copyAssets();
			exportScenes(this, visAssetsDir.child("scene"), outAssetsDir.child("scene"));

			nextStep();
			statusBar.setText("Export finished");
		}

		private int calculateSteps () {
			int steps = 0;
			steps++; //clean old assets, new dirs
			steps++; //package textures

			int assetsDirCounter = visAssetsDir.list(file -> {
				//exclude gfx and scene dir, exclude empty folders
				return file.isDirectory() && file.list().length > 0 && !(file.getName().equals("gfx") || file.getName().equals("scene"));
			}).length;
			steps += assetsDirCounter;

			String[] ext = {"scene"};
			int sceneCounter = FileUtils.listFiles(visAssetsDir.child("scene").file(), ext, true).size();
			steps += sceneCounter;

			return steps;
		}

		private void cleanOldAssets () {
			setMessage("Cleaning old assets");
			outAssetsDir = project.getAssetOutputDirectory();

			outAssetsDir.deleteDirectory();
			outAssetsDir.mkdirs();

			outAssetsDir.child("gfx").mkdirs();
			outAssetsDir.child("scene").mkdirs();
			nextStep();
		}

		private void packageTextures () {
			setMessage("Packaging textures");
			TexturePacker.process(texturePackerSettings, visAssetsDir.child("gfx").path(), outAssetsDir.child("gfx").path(), "textures");
			nextStep();
		}

		private void copyAssets () {
			for (FileHandle file : visAssetsDir.list()) {
				if (file.isDirectory() == false) continue;
				if (file.list().length == 0) continue;
				if (file.name().equals("gfx") || file.name().equals("scene")) continue;

				setMessage("Copying assets directory: " + file.name());

				try {
					FileUtils.copyDirectory(file.file(), outAssetsDir.child(file.name()).file(), f -> f.getName().equals(".vis") == false);
				} catch (IOException e) {
					Log.exception(e);
				}
				nextStep();
			}
		}

		private void exportScenes (ExportAsyncTask task, FileHandle sceneDir, FileHandle outDir) {
			outDir.mkdirs();

			scene = null;

			for (final FileHandle file : sceneDir.list()) {
				if (file.isDirectory()) exportScenes(task, file, outDir.child(file.name()));

				if (file.extension().equals("scene")) {
					task.setMessage("Exporting scene: " + file.name());

					executeOnOpenGL(() -> scene = sceneCache.get(file));

					SceneData sceneData = new SceneData();

					sceneData.viewport = scene.viewport;
					sceneData.width = scene.width;
					sceneData.height = scene.height;
					sceneData.pixelsPerUnit = scene.pixelsPerUnit;
					sceneData.physicsSettings = scene.physicsSettings;

					sceneData.groupIds = new IntMap<>(scene.getGroups());

					for (Layer layer : scene.getLayers()) {
						sceneData.layers.add(new LayerData(layer.cordsSystem, layer.name, layer.id));
					}

					scene.getSchemes().forEach(scheme -> sceneData.entities.add(scheme.toData()));

					json.toJson(sceneData, outDir.child(file.name()));
					task.nextStep();

				} else
					Log.warn("Unknown file in 'scene' directory: " + file.path());
			}
		}
	}
}
