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
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.editor.util.vis.TextureCacheFilter;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;
import org.apache.commons.io.FilenameUtils;

/**
 * Allows to get loaded textures from project 'gfx' assets directory and allows to get loaded atlases from project 'atlas' asset directory.
 * Live reloading is fully supported, however it requires listening for {@link ResourceReloadedEvent} and manually updating
 * textures.
 * @author Kotcrab
 */
public class TextureCacheModule extends ProjectModule implements WatchListener {
	private StatusBarModule statusBar;

	private AssetsMetadataModule assetsMetadata;
	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcher;

	private String gfxPath;
	private String cachePath;

	private Settings settings;

	private TextureCacheFilter cacheFilter;

	private TextureRegion loadingRegion;
	private TextureRegion missingRegion;

	private ObjectMap<String, TextureRegion> regions = new ObjectMap<>();

	private FileHandle cacheFile;
	private FileHandle atlasesFolder;
	private FileHandle assetsFolder;
	private TextureAtlas cache;

	private ObjectMap<String, TextureAtlas> atlases = new ObjectMap<>();

	private Timer cacheWaitTimer = new Timer();
	private Timer atlasWaitTimer = new Timer();

	private boolean packagingEnabled = true;

	@Override
	public void init () {
		settings = new Settings();
		settings.maxWidth = 4096;
		settings.maxHeight = 4096;
		settings.combineSubdirectories = true;
		settings.silent = true;
		settings.useIndexes = false;
		settings.fast = true;

		cacheFilter = new TextureCacheFilter(assetsMetadata);

		loadingRegion = Assets.icons.findRegion("refresh-big");
		missingRegion = Assets.icons.findRegion("file-question-big");

		FileHandle out = fileAccess.getModuleFolder(".textureCache");
		cachePath = out.path();
		cacheFile = out.child("cache.atlas");

		gfxPath = fileAccess.getAssetsFolder().child("gfx").path();
		atlasesFolder = fileAccess.getAssetsFolder().child("atlas");
		assetsFolder = fileAccess.getAssetsFolder();

		watcher.addListener(this);

		try {
			if (cacheFile.exists()) cache = new TextureAtlas(cacheFile);
		} catch (Exception e) {
			Log.error("Error while loading texture cache, texture cache will be regenerated");
		}

		try {
			FileUtils.streamRecursively(assetsFolder, file -> {
				if (file.extension().equals("atlas")) {
					updateAtlas(file);
				}
			});
		} catch (Exception e) {
			Log.error("Error encountered while loading one of atlases");
			Log.exception(e);
		}

		updateCache();
	}

	private void updateCache () {
		new Thread(this::packageAndReloadCache, "TextureCache").start();
	}

	private void packageAndReloadCache () {
		if (packagingEnabled) {
			TexturePacker.process(settings, gfxPath, cachePath, "cache", cacheFilter);
		}

		Gdx.app.postRunnable(this::reloadCache);
	}

	private void reloadCache () {
		if (cacheFile.exists()) {
			TextureAtlas oldCache = null;

			if (cache != null) oldCache = cache;

			cache = new TextureAtlas(cacheFile);

			for (Entry<String, TextureRegion> e : regions.entries()) {
				String path = FileUtils.removeFirstSeparator(FilenameUtils.removeExtension(e.key));
				TextureRegion region = e.value;
				TextureRegion newRegion = cache.findRegion(path);
				if (newRegion == null)
					region.setRegion(missingRegion);
				else
					region.setRegion(newRegion);
			}

			disposeCacheLater(oldCache);

			App.eventBus.post(new ResourceReloadedEvent(ResourceReloadedEvent.RESOURCE_TEXTURES));
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
			App.eventBus.post(new ResourceReloadedEvent(ResourceReloadedEvent.RESOURCE_TEXTURES));
			App.eventBus.post(new ResourceReloadedEvent(ResourceReloadedEvent.RESOURCE_TEXTURE_ATLASES));
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
		if (ProjectPathUtils.isTexture(file)) {
			cacheWaitTimer.clear();
			cacheWaitTimer.scheduleTask(new Task() {
				@Override
				public void run () {
					updateCache();
				}
			}, 0.5f);
		}

		if (ProjectPathUtils.isTextureAtlas(file) || ProjectPathUtils.isTextureAtlasImage(file)) {
			atlasWaitTimer.clear();
			atlasWaitTimer.scheduleTask(new Task() {
				@Override
				public void run () {
					updateAtlas(file);
				}
			}, 0.5f);
		}
	}

	public void setPackagingEnabled (boolean packagingEnabled) {
		this.packagingEnabled = packagingEnabled;
	}

	public TextureRegion getRegion (VisAssetDescriptor descriptor) {
		if (descriptor instanceof TextureRegionAsset) return getCachedGfxRegion((TextureRegionAsset) descriptor);
		if (descriptor instanceof AtlasRegionAsset) return getAtlasRegion((AtlasRegionAsset) descriptor);

		throw new UnsupportedAssetDescriptorException(descriptor);
	}

	private TextureRegion getCachedGfxRegion (TextureRegionAsset asset) {
		String relativePath = asset.getPath();
		String regionName = FileUtils.removeFirstSeparator(FilenameUtils.removeExtension(relativePath));

		TextureRegion region = regions.get(regionName);

		if (region == null) {
			if (cache != null) region = cache.findRegion(regionName);
			if (region == null) region = new TextureRegion(loadingRegion);
			regions.put(relativePath, region);
		}

		return region;
	}

	private TextureRegion getAtlasRegion (AtlasRegionAsset asset) {
		String relativePath = asset.getPath();

		TextureAtlas atlas = atlases.get(relativePath);
		if (atlas == null) return missingRegion;

		if (asset.getRegionName() == null) {
			return new TextureRegion(atlas.getTextures().first());
		} else {
			TextureRegion region = atlas.findRegion(asset.getRegionName());
			if (region == null) return missingRegion;
			return region;
		}
	}

	public TextureAtlas getAtlas (String relativePath) {
		return atlases.get(relativePath);
	}
}
