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

package com.kotcrab.vis.plugin.spriter.module;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.Loader;
import com.brashmonkey.spriter.SCMLReader;
import com.kotcrab.vis.editor.module.project.AssetsWatcherModule;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.ProjectModule;
import com.kotcrab.vis.editor.plugin.api.ContainerExtension;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.plugin.spriter.runtime.assets.SpriterAsset;
import com.kotcrab.vis.plugin.spriter.runtime.component.VisSpriter;
import com.kotcrab.vis.plugin.spriter.runtime.loader.SpriterLoader;
import com.kotcrab.vis.runtime.plugin.VisPlugin;

/** @author Kotcrab */
@VisPlugin
public class SpriterCacheModule extends ProjectModule implements WatchListener, ContainerExtension {
	private FileAccessModule fileAccess;
	private AssetsWatcherModule assetsWatcher;

	private ObjectMap<FileHandle, Loader> loaders = new ObjectMap<>();
	private Array<Loader> awaitingDisposeLoaders = new Array<>();

	@Override
	public void init () {
		assetsWatcher.addListener(this);
	}

	public VisSpriter createComponent (SpriterAsset asset, float scale) {
		FileHandle file = fileAccess.getAssetsFolder().child(asset.getPath());
		Data data = new SCMLReader(file.read()).getData();
		Loader<Sprite> loader = new SpriterLoader(data);
		loader.load(file.file());

		loaders.put(file, loader);

		return new VisSpriter(loader, data, scale);
	}

	@Override
	public void fileDeleted (FileHandle file) {
		if (file.extension().equals("scml")) {
			Loader loader = loaders.get(file);
			if (loader != null) awaitingDisposeLoaders.add(loader);
		}
	}

	public void disposeOldLoaders () {
		for (Loader loader : awaitingDisposeLoaders)
			loader.dispose();
	}

	@Override
	public void dispose () {
		assetsWatcher.removeListener(this);

		for (Loader loader : loaders.values())
			loader.dispose();

		for (Loader loader : awaitingDisposeLoaders)
			loader.dispose();
	}

	public VisSpriter cloneComponent (SpriterAsset asset, VisSpriter original) {
		VisSpriter clone = createComponent(asset, original.getPlayer().getScale());
		clone.setFlip(original.isFlipX(), original.isFlipY());
		clone.onDeserialize(original.isPlayOnStart(), original.getDefaultAnimation());
		clone.setAnimationPlaying(original.isAnimationPlaying());
		return clone;
	}

	@Override
	public ExtensionScope getScope () {
		return ExtensionScope.PROJECT;
	}
}
