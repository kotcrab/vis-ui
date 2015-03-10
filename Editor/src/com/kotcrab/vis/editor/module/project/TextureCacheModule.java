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
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.Log;

public class TextureCacheModule extends ProjectModule implements WatchListener {
	private AssetsWatcherModule watcher;

	private String gfxPath;
	private String outPath;

	private Settings settings;

	private ObjectMap<String, TextureRegion> regions;

	private TextureRegion loadingRegion;
	private TextureRegion missingRegion;

	private FileHandle cacheFile;
	private TextureAtlas cache;

	private Timer waitTimer;

	private boolean firstReload = true;

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		watcher = projectContainer.get(AssetsWatcherModule.class);

		regions = new ObjectMap<>();

		waitTimer = new Timer();

		settings = new Settings();
		settings.combineSubdirectories = true;
		settings.silent = true;
		settings.fast = true;

		loadingRegion = Assets.icons.findRegion("refresh-big");
		missingRegion = Assets.icons.findRegion("file-question-big");

		FileHandle out = fileAccess.getModuleFolder(".textureCache");
		outPath = out.path();
		cacheFile = out.child("cache.atlas");

		gfxPath = fileAccess.getAssetsFolder().child("gfx").path();

		watcher.addListener(this);

		try {
			if (cacheFile.exists()) cache = new TextureAtlas(cacheFile);
		} catch (Exception e) {
			Log.error("Error while loading texture cache, texture cache will be regenerated");
		}

		updateCache();
	}

	private void updateCache () {
		new Thread(this::performUpdate, "TextureCache").start();
	}

	private void performUpdate () {
		TexturePacker.processIfModified(settings, gfxPath, outPath, "cache");

		Gdx.app.postRunnable(this::reloadAtlas);
	}

	private void reloadAtlas () {
		if (cacheFile.exists()) {
			TextureAtlas oldCache = null;

			if (cache != null) oldCache = cache;

			cache = new TextureAtlas(cacheFile);

			for (Entry<String, TextureRegion> e : regions.entries()) {
				String path = e.key;
				TextureRegion region = e.value;

				TextureRegion newRegion = cache.findRegion(path);

				if (newRegion == null)
					region.setRegion(missingRegion);
				else
					region.setRegion(newRegion);
			}

			disposeOldCacheLater(oldCache);

			App.eventBus.post(new TexturesReloadedEvent());
			if (firstReload == false) {
				//we don't want to display 'textures reloaded' right after editor startup / project loaded
				App.eventBus.post(new StatusBarEvent("Textures reloaded"));
				firstReload = true;
			}
		} else
			Log.error("Texture cache not ready, probably they aren't any textures in project or packer failed");
	}

	private void disposeOldCacheLater (final TextureAtlas oldCache) {
		Timer.instance().scheduleTask(new Task() {
			@Override
			public void run () {
				if (oldCache != null) oldCache.dispose();
			}
		}, 0.5f);
	}

	@Override
	public void dispose () {
		if (cache != null)
			cache.dispose();
		watcher.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
		if (file.extension().equals("jpg") || file.extension().equals("png")) {
			waitTimer.clear();
			waitTimer.scheduleTask(new Task() {
				@Override
				public void run () {
					updateCache();
				}
			}, 0.5f);
		}
	}

	@Override
	public void fileDeleted (FileHandle file) {

	}

	@Override
	public void fileCreated (FileHandle file) {

	}

	public TextureRegion getRegion (FileHandle file) {
		return getRegion(resolvePath(file));
	}

	public TextureRegion getRegion (String relativePath) {
		TextureRegion region = regions.get(relativePath);

		if (region == null) {
			if (cache != null) region = cache.findRegion(relativePath);

			if (region == null) region = new TextureRegion(loadingRegion);

			regions.put(relativePath, region);
		}

		return region;
	}

	private String resolvePath (FileHandle file) {
		String path = file.path();

		if (path.startsWith(gfxPath))
			return path.substring(gfxPath.length() + 1, path.length() - file.extension().length() - 1);
		else
			return path;
	}

	public String getRelativePath (TextureRegion region) {
		for (Entry<String, TextureRegion> e : regions.entries()) {
			if (e.value == region)
				return e.key;
		}

		throw new IllegalStateException("Region not found in cache!");
	}
}
