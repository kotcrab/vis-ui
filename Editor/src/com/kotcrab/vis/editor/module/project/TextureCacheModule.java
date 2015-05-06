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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.event.TexturesReloadedEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.Log;
import com.kotcrab.vis.editor.util.ProjectPathUtils;

public class TextureCacheModule extends ProjectModule implements WatchListener {
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private AssetsWatcherModule watcher;

	private String gfxPath;
	private String cachePath;

	private Settings settings;

	private TextureRegion loadingRegion;
	private TextureRegion missingRegion;

	private ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

	private FileHandle cacheFile;
	private FileHandle atlasesFolder;
	private TextureAtlas cache;

	private ObjectMap<String, TextureAtlas> atlases = new ObjectMap<>();

	private Timer cacheWaitTimer = new Timer();
	private Timer atlasWaitTimer = new Timer();

	private boolean firstReload = true;

	@Override
	public void init () {
		settings = new Settings();
		settings.maxWidth = 4096;
		settings.maxHeight = 4096;
		settings.combineSubdirectories = true;
		settings.silent = true;
		settings.fast = true;

		loadingRegion = Assets.icons.findRegion("refresh-big");
		missingRegion = Assets.icons.findRegion("file-question-big");

		FileHandle out = fileAccess.getModuleFolder(".textureCache");
		cachePath = out.path();
		cacheFile = out.child("cache.atlas");

		gfxPath = fileAccess.getAssetsFolder().child("gfx").path();
		atlasesFolder = fileAccess.getAssetsFolder().child("atlas");

		watcher.addListener(this);

		try {
			if (cacheFile.exists()) cache = new TextureAtlas(cacheFile);

			if (atlasesFolder.exists()) {
				FileHandle[] files = atlasesFolder.list();

				for (FileHandle file : files)
					if (file.extension().equals("atlas"))
						updateAtlas(file);
			}
		} catch (Exception e) {
			Log.error("Error while loading texture cache, texture cache will be regenerated");
		}

		updateCache();
	}

	private void updateCache () {
		new Thread(this::packageAndReloadCache, "TextureCache").start();
	}

	private void packageAndReloadCache () {
		TexturePacker.processIfModified(settings, gfxPath, cachePath, "cache");

		Gdx.app.postRunnable(this::reloadCache);
	}

	private void reloadCache () {
		if (cacheFile.exists()) {
			TextureAtlas oldCache = null;

			if (cache != null) oldCache = cache;

			cache = new TextureAtlas(cacheFile);

			for (Entry<String, TextureRegion> e : regions.entries()) {
				String path = e.key.substring(4, e.key.length() - 4);
				TextureRegion region = e.value;

				TextureRegion newRegion = cache.findRegion(path);

				if (newRegion == null)
					region.setRegion(missingRegion);
				else
					region.setRegion(newRegion);
			}

			disposeCacheLater(oldCache);

			App.eventBus.post(new TexturesReloadedEvent());
			if (firstReload == false) {
				//we don't want to display 'textures reloaded' right after editor startup / project loaded
				App.eventBus.post(new StatusBarEvent("Textures reloaded"));
				firstReload = true;
			}
		} else
			Log.error("Texture cache not ready, probably they aren't any textures in project or packer failed");
	}

	private void disposeCacheLater (final TextureAtlas oldCache) {
		Timer.instance().scheduleTask(new Task() {
			@Override
			public void run () {
				if (oldCache != null) oldCache.dispose();
			}
		}, 0.5f);
	}

	private void updateAtlas (FileHandle file) {
		String relativePath = fileAccess.relativizeToAssetsFolder(file);

		TextureAtlas atlas = atlases.get(relativePath);
		if (atlas != null) {
			atlases.remove(relativePath);
			atlas.dispose();
		}

		if (file.exists()) {
			atlases.put(relativePath, new TextureAtlas(file));
			App.eventBus.post(new TexturesReloadedEvent());
		}
	}

	@Override
	public void dispose () {
		if (cache != null)
			cache.dispose();

		for (TextureAtlas atlas : atlases.values())
			atlas.dispose();

		watcher.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
		String relativePath = fileAccess.relativizeToAssetsFolder(file);

		if (ProjectPathUtils.isTexture(relativePath, file.extension())) {
			cacheWaitTimer.clear();
			cacheWaitTimer.scheduleTask(new Task() {
				@Override
				public void run () {
					updateCache();
				}
			}, 0.5f);
		}

		if (ProjectPathUtils.isTextureAtlas(file, relativePath)) {
			atlasWaitTimer.clear();
			cacheWaitTimer.scheduleTask(new Task() {
				@Override
				public void run () {
					updateAtlas(file);
				}
			}, 0.5f);
		}
	}

	public TextureRegion getRegion (String relativePath) {
		if (relativePath.startsWith("gfx")) {

			String regionName = relativePath.substring(4, relativePath.length() - 4);

			TextureRegion region = regions.get(regionName);

			if (region == null) {
				if (cache != null) region = cache.findRegion(regionName);

				if (region == null) region = new TextureRegion(loadingRegion);

				regions.put(relativePath, region);
			}

			return region;
		}

		if (relativePath.startsWith("atlas")) {
			if (relativePath.endsWith(".atlas")) {
				TextureAtlas atlas = atlases.get(relativePath);

				if (atlas == null) return missingRegion;
				return new TextureRegion(atlas.getTextures().first());
			} else {
				String[] paths = relativePath.split("\\*", 2);

				TextureAtlas atlas = atlases.get(paths[0]);
				TextureRegion region = atlas.findRegion(paths[1]);
				if (region == null) return missingRegion;
				return region;
			}
		}

		throw new IllegalStateException("Invalid texture path!");
	}

	public TextureAtlas getAtlas (String relativePath) {
		return atlases.get(relativePath);
	}
}
