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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.ui.AsyncTaskProgressDialog;
import com.kotcrab.vis.editor.util.AsyncTask;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.texturepacker.TexturePacker;
import com.kotcrab.vis.editor.util.texturepacker.TexturePacker.Settings;
import com.kotcrab.vis.runtime.data.ParticleEffectData;
import com.kotcrab.vis.runtime.data.SceneData;
import com.kotcrab.vis.runtime.data.SceneSpriteData;
import com.kotcrab.vis.runtime.data.TextData;
import com.kotcrab.vis.runtime.scene.SceneLoader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class ExportModule extends ProjectModule {
	private FileAccessModule fileAccess;
	private SceneIOModule sceneIO;
	private FontCacheModule fontCache;

	private FileHandle visAssetsDir;

	private Settings texturePackerSettings;
	private boolean firstExportDone;

	private Json json;

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		sceneIO = projectContainer.get(SceneIOModule.class);
		fontCache = projectContainer.get(FontCacheModule.class);

		visAssetsDir = fileAccess.getAssetsFolder();

		texturePackerSettings = new Settings();
		texturePackerSettings.combineSubdirectories = true;

		json = SceneLoader.getJson();
	}

	public void export (boolean quick) {
		switch (project.type) {
			case LibGDX:
				exportLibGDX(quick);
				break;
			case Generic:
				throw new UnsupportedOperationException("Not implemented yet");
		}
	}

	private void exportLibGDX (boolean quick) {
		if (firstExportDone == false && quick)
			Log.info("Requested quick export but normal export hasn't been done since editor launch, performing normal export.");

		if (firstExportDone == false || quick == false)
			doExport();
		else
			doExport(); //TODO do quick export

		firstExportDone = true;
	}

	private void doExport () {
		ExportAsyncTask exportTask = new ExportAsyncTask();
		Editor.instance.getStage().addActor(new AsyncTaskProgressDialog("Exporting", exportTask).fadeIn());
	}

	private class ExportAsyncTask extends AsyncTask {
		int step;
		int totalSteps;

		FileHandle outAssetsDir;

		/** Holds currently exported scene */
		private EditorScene editorScene;

		public ExportAsyncTask () {
			super("ProjectExporter");
		}

		@Override
		public void execute () {
			setMessage("Preparing for export...");
			totalSteps = calculateSteps();

			cleanOldAssets();
			packageTextures();
			copyAssets();
			exportScenes(this, visAssetsDir.child("scene"), outAssetsDir.child("scene"));

			nextStep();
			App.eventBus.post(new StatusBarEvent("Export finished"));
		}

		private void nextStep () {
			setProgressPercent(++step * 100 / totalSteps);
		}

		private int calculateSteps () {
			int steps = 0;
			steps++; //clean old assets, new dirs
			steps++; //package textures

			int assetsDirCounter = visAssetsDir.list(new FileFilter() {
				@Override
				public boolean accept (File file) {
					//exclude gfx and scene dir, exclude empty folders
					return file.isDirectory() && file.list().length > 0 && !(file.getName().equals("gfx") || file.getName().equals("scene"));
				}
			}).length;
			steps += assetsDirCounter;

			String[] ext = {"scene"};
			int sceneCounter = FileUtils.listFiles(visAssetsDir.child("scene").file(), ext, true).size();
			steps += sceneCounter;

			return steps;
		}

		private void cleanOldAssets () {
			setMessage("Cleaning old assets");
			outAssetsDir = Gdx.files.absolute(project.root + project.assets);

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
					FileUtils.copyDirectory(file.file(), outAssetsDir.child(file.name()).file());
				} catch (IOException e) {
					Log.exception(e);
				}
				nextStep();
			}
		}

		private void exportScenes (ExportAsyncTask task, FileHandle sceneDir, FileHandle outDir) {
			outDir.mkdirs();

			editorScene = null;

			for (final FileHandle file : sceneDir.list()) {
				if (file.isDirectory()) exportScenes(task, file, outDir.child(file.name()));

				if (file.extension().equals("scene")) {
					task.setMessage("Exporting scene: " + file.name());

					executeOnOpenGL(new Runnable() {
						@Override
						public void run () {
							editorScene = sceneIO.load(file);
						}
					});

					SceneData sceneData = new SceneData();

					sceneData.viewport = editorScene.viewport;
					sceneData.width = editorScene.width;
					sceneData.height = editorScene.height;
					sceneData.entities = new Array<>();

					for (EditorEntity entity : editorScene.entities) {
						if (entity instanceof SpriteObject) {
							SpriteObject obj = (SpriteObject) entity;

							SceneSpriteData data = new SceneSpriteData();
							data.saveFrom(obj);

							data.id = obj.getId();
							data.textureAtlas = "gfx/textures.atlas";
							data.textureRegion = obj.getCacheRegionName();

							sceneData.entities.add(data);
							continue;
						}

						if (entity instanceof TextObject) {
							TextObject obj = (TextObject) entity;

							TextData data = new TextData();
							data.saveFrom(obj);

							sceneData.entities.add(data);
							continue;
						}

						if (entity instanceof ParticleObject) {
							ParticleObject obj = (ParticleObject) entity;

							ParticleEffectData data = new ParticleEffectData();
							data.saveFrom(obj);

							sceneData.entities.add(data);
							continue;
						}

						Log.error("Ignoring unknown entity: " + entity.getClass());
					}

					json.toJson(sceneData, outDir.child(file.name()));
					task.nextStep();

				} else
					Log.warn("Unknown file in 'scene' directory: " + file.path());
			}
		}

	}

}
