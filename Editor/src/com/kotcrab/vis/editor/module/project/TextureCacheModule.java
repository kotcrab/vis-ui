/**
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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.event.TexturesReloadedEvent;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.texturepacker.TexturePacker;
import com.kotcrab.vis.editor.util.texturepacker.TexturePacker.Settings;

public class TextureCacheModule extends ProjectModule implements WatchListener {
	private AssetsWatcherModule watcher;

	private String assetsPath;
	private String outPath;

	private Settings settings;

	private ObjectMap<String, TextureRegion> regions;

	private TextureRegion loadingRegion;
	private TextureRegion missingRegion;

	private FileHandle cacheFile;
	private TextureAtlas cache;

	private Timer waitTimer;

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainter.get(FileAccessModule.class);
		watcher = projectContainter.get(AssetsWatcherModule.class);

		regions = new ObjectMap<>();

		waitTimer = new Timer();

		settings = new Settings();
		settings.combineSubdirectories = true;

		loadingRegion = Assets.icons.findRegion("refresh-big");
		missingRegion = Assets.icons.findRegion("file-question-big");

		FileHandle out = fileAccess.getModuleFolder(".textureCache");
		outPath = out.path();
		cacheFile = out.child("cache.atlas");

		assetsPath = fileAccess.getAssetsFolder().path();

		watcher.addListener(this);
		updateCache();

		if (cacheFile.exists()) cache = new TextureAtlas(cacheFile);
	}

	private void updateCache () {
		new Thread(new Runnable() {
			@Override
			public void run () {
				performUpdate();
			}
		}).start();
	}

	private void performUpdate () {
		TexturePacker.processIfModified(settings, assetsPath, outPath, "cache");

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				reloadAtlas();
			}
		});
	}

	private void reloadAtlas () {
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
		App.eventBus.post(new StatusBarEvent("Textures reloaded"));
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

		if (path.startsWith(assetsPath))
			return path.substring(assetsPath.length() + 1, path.length() - file.extension().length() - 1);
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
