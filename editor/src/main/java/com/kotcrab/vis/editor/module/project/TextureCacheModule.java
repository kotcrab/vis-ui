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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent.ResourceType;
import com.kotcrab.vis.editor.module.editor.StatusBarModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.gdx.VisTexturePacker;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.editor.util.vis.TextureCacheFilter;
import com.kotcrab.vis.runtime.assets.AtlasRegionAsset;
import com.kotcrab.vis.runtime.assets.TextureRegionAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.SpriteSheetHelper;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;
import org.apache.commons.io.FilenameUtils;

import java.util.EnumSet;

/**
 * Allows to get loaded textures from project 'gfx' assets directory and allows to get loaded atlases from project 'atlas' asset directory.
 * Live reloading is fully supported, however it requires listening for {@link ResourceReloadedEvent} and manually updating
 * textures.
 * @author Kotcrab
 */
public class TextureCacheModule extends ProjectModule implements WatchListener {
	private static final String TAG = "TextureCacheModule";
	private static final boolean DEBUG_LOG = false;

	private StatusBarModule statusBar;

	private AssetsMetadataModule assetsMetadata;
	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcher;

	private String assetFolderPath;
	private String cacheFilePath;

	private Settings settings;

	private TextureCacheFilter cacheFilter;

	private TextureRegion missingRegion;

	private ObjectMap<String, TextureRegion> regions = new ObjectMap<>();
	private ObjectMap<String, Texture> textures = new ObjectMap<>();

	private FileHandle cacheFile;
	private FileHandle atlasesFolder;
	private FileHandle assetsFolder;
	private TextureAtlas cache;

	private ObjectMap<String, TextureAtlas> atlases = new ObjectMap<>();
	private ObjectMap<TextureAtlas, SpriteSheetHelper> spriteSheetHelpers = new ObjectMap<>();

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

		cacheFilter = new TextureCacheFilter(assetsMetadata, 1024);

		missingRegion = Assets.getIconRegion("file-question-big");

		FileHandle out = fileAccess.getModuleFolder(".textureCache");
		cacheFilePath = out.path();
		cacheFile = out.child("cache.atlas");

		assetFolderPath = fileAccess.getAssetsFolder().path();
		atlasesFolder = fileAccess.getAssetsFolder().child("atlas");
		assetsFolder = fileAccess.getAssetsFolder();

		watcher.addListener(this);

		try {
			if (cacheFile.exists()) cache = new TextureAtlas(cacheFile);
		} catch (Exception e) {
			Log.error(TAG, "Error while loading texture cache, texture cache will be regenerated");
		}

		try {
			FileUtils.streamFilesRecursively(assetsFolder, file -> {
				if (file.extension().equals("atlas")) {
					updateAtlas(file);
				}
			});
		} catch (Exception e) {
			Log.error(TAG, "Error encountered while loading one of atlases");
			Log.exception(e);
		}

		updateCache();
	}

	private void updateCache () {
		new Thread(this::packageAndReloadCache, "TextureCache").start();
	}

	private void packageAndReloadCache () {
		if (packagingEnabled) {
			if (DEBUG_LOG) Log.debug(TAG, "Rebuilding texture cache");
			VisTexturePacker.process(settings, assetFolderPath, cacheFilePath, "cache", cacheFilter);
			if (DEBUG_LOG) Log.debug(TAG, "Texture cache rebuilt");
		}

		Gdx.app.postRunnable(this::reloadCache);
	}

	private void reloadCache () {
		if (cacheFile.exists()) {
			TextureAtlas oldCache = null;

			if (cache != null) oldCache = cache;

			cache = new TextureAtlas(cacheFile);

			for (Entry<String, TextureRegion> e : regions.entries()) {
				String path = e.key;
				String regionName = FilenameUtils.removeExtension(path);
				TextureRegion region = e.value;
				TextureRegion newRegion = cache.findRegion(regionName);
				if (newRegion == null) {
					Texture texture = textures.get(path);
					if (texture == null) {
						Log.warn(TAG, "Missing texture for region: " + path);
						region.setRegion(missingRegion);
					} else {
						if (DEBUG_LOG) Log.debug(TAG, "Update region using texture: " + path);
						region.setRegion(new TextureRegion(texture));
					}
				} else {
					if (textures.containsKey(path)) {
						if (DEBUG_LOG) Log.debug(TAG, "Dispose texture " + path);
						textures.get(path).dispose();
						textures.remove(path);
					}
					if (DEBUG_LOG) Log.debug(TAG, "Update region using cache " + path);
					region.setRegion(newRegion);
				}
			}

			if (DEBUG_LOG) Log.debug(TAG, "Post update regions array size " + regions.size);

			disposeCacheLater(oldCache);

			App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.TEXTURES)));
		} else
			Log.error(TAG, "Texture cache not ready, probably they aren't any textures in project or packer failed");
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
			spriteSheetHelpers.remove(atlas);
			atlas.dispose();
		}

		if (file.exists()) {
			try {
				atlases.put(relativePath, new TextureAtlas(file));
			} catch (GdxRuntimeException e) {
				Log.exception(e);
			}
			App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.TEXTURES)));
			App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.TEXTURE_ATLASES)));
		}
	}

	@Override
	public void dispose () {
		watcher.removeListener(this);

		if (cache != null) {
			cache.dispose();
		}

		for (TextureAtlas atlas : atlases.values()) {
			atlas.dispose();
		}

		for (Texture texture : textures.values()) {
			texture.dispose();
		}
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

	@Override
	public void fileDeleted (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file);
		Texture texture = textures.get(path);
		if (texture != null) {
			if (DEBUG_LOG) Log.debug(TAG, "File deleted, dispose texture " + file.path());
			texture.dispose();
			textures.remove(path);
		}
	}

	public void setPackagingEnabled (boolean packagingEnabled) {
		this.packagingEnabled = packagingEnabled;
	}

	public TextureRegion getRegion (VisAssetDescriptor descriptor) {
		if (descriptor instanceof TextureRegionAsset) return getTextureRegion((TextureRegionAsset) descriptor);
		if (descriptor instanceof AtlasRegionAsset) return getAtlasRegion((AtlasRegionAsset) descriptor);

		throw new UnsupportedAssetDescriptorException(descriptor);
	}

	private TextureRegion getTextureRegion (TextureRegionAsset asset) {
		String relativePath = asset.getPath();
		String regionName = FilenameUtils.removeExtension(relativePath);

		TextureRegion region = regions.get(regionName);

		if (region == null) {
			if (cache != null) region = cache.findRegion(regionName);
			if (region == null) {
				try {

					Texture texture = textures.get(relativePath);
					if (texture == null) {
						texture = new Texture(Gdx.files.absolute(fileAccess.derelativizeFromAssetsFolder(relativePath)));
						if (DEBUG_LOG) Log.debug(TAG, "Load texture " + relativePath);
						textures.put(relativePath, texture);
					}

					region = new TextureRegion(texture);
				} catch (GdxRuntimeException e) {
					Log.exception(e);
					return missingRegion;
				}
			} else if (DEBUG_LOG) {
				Log.debug(TAG, "Using cached region " + relativePath);
			}

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

	public TextureAtlas getAtlas (AtlasRegionAsset asset) {
		String relativePath = asset.getPath();
		return atlases.get(relativePath);
	}

	public SpriteSheetHelper getSpriteSheetHelper (TextureAtlas atlas) {
		SpriteSheetHelper helper = spriteSheetHelpers.get(atlas);
		if (helper == null) {
			helper = new SpriteSheetHelper(atlas);
			spriteSheetHelpers.put(atlas, helper);
		}
		return helper;
	}
}
