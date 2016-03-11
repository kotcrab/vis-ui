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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent;
import com.kotcrab.vis.editor.event.ResourceReloadedEvent.ResourceType;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.vis.EditorRuntimeException;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;
import com.kotcrab.vis.runtime.assets.PathAsset;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;
import com.kotcrab.vis.runtime.util.UnsupportedAssetDescriptorException;

import java.util.EnumSet;

/**
 * Allows to get loaded particles from project asset directory.
 * @author Kotcrab
 */
public class ParticleCacheModule extends ProjectModule implements WatchListener {
	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcherModule;

	@Override
	public void init () {
		watcherModule.addListener(this);
	}

	public ParticleEffect get (VisAssetDescriptor assetDescriptor, float scaleFactor) {
		if (assetDescriptor instanceof PathAsset == false)
			throw new UnsupportedAssetDescriptorException(assetDescriptor);

		PathAsset path = (PathAsset) assetDescriptor;
		return get(fileAccess.getAssetsFolder().child(path.getPath()), scaleFactor);
	}

	private ParticleEffect get (FileHandle file, float scaleFactor) {
		ParticleEffect effect = new ParticleEffect();

		try {
			effect.load(file, file.parent());
		} catch (GdxRuntimeException e) {
			throw new EditorRuntimeException(e);
		}

		effect.scaleEffect(scaleFactor);
		return effect;
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
		if (ProjectPathUtils.isParticle(file)) {
			App.eventBus.post(new ResourceReloadedEvent(EnumSet.of(ResourceType.PARTICLES)));
		}
	}

	@Override
	public void fileDeleted (FileHandle file) {
	}

	@Override
	public void fileCreated (FileHandle file) {
	}
}
