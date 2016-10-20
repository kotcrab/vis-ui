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

package com.kotcrab.vis.editor.extension;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.module.editor.EditorSettingsIOModule;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.module.editor.TabsModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.plugin.api.AssetsFileSorter;
import com.kotcrab.vis.editor.plugin.api.ExporterPlugin;
import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.ui.dialog.DefaultExporterSettingsDialog;
import com.kotcrab.vis.editor.ui.dialog.UnsavedResourcesDialog;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.Holder;
import com.kotcrab.vis.editor.util.async.Async;
import com.kotcrab.vis.editor.util.gdx.VisTexturePacker;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.editor.util.vis.TextureCacheFilter;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.data.LayerData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.properties.StoresAssetDescriptor;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import com.kotcrab.vis.ui.util.async.SteppedAsyncTask;

import java.util.UUID;

/**
 * Default VisEditor exporter, exports scenes to JSON format.
 * @author Kotcrab
 */
public class DefaultExporter implements ExporterPlugin {
	public static final String SETTINGS_FILE_NAME = "defaultExporterSettings";
	public static final String EXPORTER_UUID = "b8bd183c-1dc6-4ac5-9bbe-a4ba86a61b95";
	private static final int MAX_TEXTURE_SIZE = 2048;

	private EditorSettingsIOModule settingsIO;
	private ExtensionStorageModule extensionStorage;
	private StatusBarModule statusBar;
	private TabsModule tabsModule;

	private FileAccessModule fileAccess;
	private AssetsMetadataModule assetsMetadata;
	private SceneCacheModule sceneCache;

	private Stage stage;

	private Project project;
	private DefaultExporterSettings settings;
	private TextureCacheFilter textureCacheFilter;

	private FileHandle visAssetsDir;
	private FileHandle tmpDir;

	private Settings texturePackerSettings;
	private boolean firstExportDone;

	private Json json;

	@Override
	public void init (Project project) {
		this.project = project;
		reloadSettings();

		visAssetsDir = fileAccess.getAssetsFolder();
		tmpDir = fileAccess.getModuleFolder(".defaultExporter").child("tmp");
		tmpDir.mkdirs();

		json = SceneLoader.getJson();

		textureCacheFilter = new TextureCacheFilter(assetsMetadata, MAX_TEXTURE_SIZE);
	}

	private void reloadSettings () {
		settings = settingsIO.load(SETTINGS_FILE_NAME, DefaultExporterSettings.class);
		texturePackerSettings = new Settings();
		texturePackerSettings.filterMag = settings.magTextureFilter;
		texturePackerSettings.filterMin = settings.migTextureFilter;
		texturePackerSettings.maxHeight = MAX_TEXTURE_SIZE;
		texturePackerSettings.maxWidth = MAX_TEXTURE_SIZE;
		texturePackerSettings.combineSubdirectories = true;
		texturePackerSettings.silent = true;
		texturePackerSettings.useIndexes = false;
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
		reloadSettings();

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
		if (settings.useMinimalOutputType)
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
		Async.startTask(stage, "Exporting", exportTask);
	}

	private class ExportAsyncTask extends SteppedAsyncTask {
		FileHandle outAssetsDir;

		EditorScene scene;

		public ExportAsyncTask () {
			super("ProjectExporter");
		}

		@Override
		public void doInBackground () {
			setMessage("Preparing for export...");
			setTotalSteps(calculateSteps());

			cleanOldAssets();
			packageTextures();
			copyAssets();
			exportScenes(visAssetsDir, outAssetsDir);

			nextStep();
			statusBar.setText("Export finished");
		}

		private int calculateSteps () {
			Holder<Integer> steps = Holder.of(0);
			steps.value++; //clean old assets, new dirs

			int sceneCounter = FileUtils.ApacheFileUtils.listFiles(visAssetsDir.file(), new String[]{"scene"}, true).size();
			steps.value += sceneCounter;

			if (settings.packageSeparateAtlasForEachScene) {
				steps.value++; //package textures
			} else {
				steps.value += sceneCounter; //each scene needs to have it's own texture atlas
			}

			FileUtils.streamDirectoriesRecursively(visAssetsDir, file -> steps.value++);

			return steps.value;
		}

		private void cleanOldAssets () {
			setMessage("Cleaning old assets");
			outAssetsDir = project.getAssetOutputDirectory();

			outAssetsDir.deleteDirectory();
			outAssetsDir.mkdirs();

			nextStep();
		}

		private void packageTextures () {
			if (settings.packageSeparateAtlasForEachScene) {
				FileHandle sceneTextureDir = outAssetsDir.child("scene-textures");
				sceneTextureDir.mkdirs();
				exportSceneTextures(visAssetsDir, sceneTextureDir);
			} else {
				setMessage("Packaging textures");
				VisTexturePacker.process(texturePackerSettings, visAssetsDir.path(), outAssetsDir.path(), "textures", textureCacheFilter);
				nextStep();
			}
		}

		private void copyAssets () {
			FileUtils.streamDirectoriesRecursively(visAssetsDir, folder -> {
				setMessage("Processing assets directory: " + folder.name());
				AssetsFileSorter fileSorter = null;
				String relativeFolderPath = fileAccess.relativizeToAssetsFolder(folder);
				for (AssetsFileSorter sorter : extensionStorage.getAssetsFileSorters()) {
					if (sorter.isSupported(assetsMetadata, folder, relativeFolderPath)) {
						fileSorter = sorter;
						break;
					}
				}

				for (FileHandle file : folder.list()) {
					if (file.isDirectory()) continue;
					if (ProjectPathUtils.isScene(file)) continue;

					String relativeFilePath = fileAccess.relativizeToAssetsFolder(file);

					if (fileSorter != null) {
						if (fileSorter.isExportedFile(file)) file.copyTo(outAssetsDir.child(relativeFilePath));
					} else {
						file.copyTo(outAssetsDir.child(relativeFilePath));
					}
				}

				nextStep();
			});
		}

		private void exportScenes (FileHandle sceneDir, FileHandle outDir) {
			scene = null;

			for (final FileHandle file : sceneDir.list()) {
				if (file.isDirectory()) exportScenes(file, outDir.child(file.name()));

				if (ProjectPathUtils.isScene(file)) {
					setMessage("Exporting scene: " + file.name());
					outDir.mkdirs();

					executeOnGdx(() -> scene = sceneCache.get(file));

					SceneData sceneData = new SceneData();

					sceneData.viewport = scene.viewport;
					sceneData.width = scene.width;
					sceneData.height = scene.height;
					sceneData.pixelsPerUnit = scene.pixelsPerUnit;
					sceneData.textureAtlasPath = settings.packageSeparateAtlasForEachScene ?
							"scene-textures/" + FileUtils.relativize(visAssetsDir, FileUtils.sibling(file, "atlas")) : "textures.atlas";
					sceneData.physicsSettings = scene.physicsSettings;
					sceneData.variables = scene.variables;

					sceneData.groupIds = new IntMap<>(scene.getGroups());

					for (EditorLayer layer : scene.getLayers()) {
						sceneData.layers.add(new LayerData(layer.cordsSystem, layer.name, layer.id));
					}

					scene.getSchemes().forEach(scheme -> sceneData.entities.add(scheme.toData()));

					json.toJson(sceneData, outDir.child(file.name()));
					nextStep();
				}
			}
		}

		private void exportSceneTextures (FileHandle sceneDir, FileHandle outDir) {
			scene = null;

			for (final FileHandle file : sceneDir.list()) {
				if (file.isDirectory()) exportSceneTextures(file, outDir.child(file.name() + ""));

				if (ProjectPathUtils.isScene(file)) {
					setMessage("Exporting scene textures: " + file.name());
					outDir.mkdirs();

					FileHandle sceneTextureDir = tmpDir.child("scene-textures");
					sceneTextureDir.deleteDirectory();
					sceneTextureDir.mkdirs();

					executeOnGdx(() -> scene = sceneCache.get(file));

					scene.getSchemes().forEach(scheme -> scheme.getComponents().forEach(component ->
							{
								if (component instanceof StoresAssetDescriptor == false) return;
								VisAssetDescriptor asset = ((StoresAssetDescriptor) component).getAsset();
								if (asset instanceof TextureRegionAsset == false) return;
								String path = ((TextureRegionAsset) asset).getPath();
								FileHandle targetTextureDirectory = sceneTextureDir.child(Gdx.files.absolute(path).path());
								visAssetsDir.child(path).copyTo(targetTextureDirectory);
							}
					));

					String outTexturePath = "scene-textures/" + FileUtils.relativize(visAssetsDir, file);
					VisTexturePacker.process(texturePackerSettings, sceneTextureDir.path(), outAssetsDir.child(outTexturePath).parent().path(),
							file.nameWithoutExtension(), textureCacheFilter);
					sceneTextureDir.deleteDirectory();
					nextStep();
				}
			}
		}

	}
}
