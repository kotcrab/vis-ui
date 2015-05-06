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
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;

//TODO support dynamic refreshing
public class ParticleCacheModule extends ProjectModule implements WatchListener {
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private AssetsWatcherModule watcherModule;

	private FileHandle particleDirectory;

	@Override
	public void init () {
		particleDirectory = fileAccess.getParticleFolder();

		watcherModule.addListener(this);
	}

	public ParticleEffect get (FileHandle file) {
		ParticleEffect effect = new ParticleEffect();
		effect.load(file, file.parent());
		return effect;
	}

	@Override
	public void dispose () {
		watcherModule.removeListener(this);
	}

	@Override
	public void fileChanged (FileHandle file) {
	}

	@Override
	public void fileDeleted (FileHandle file) {
	}

	@Override
	public void fileCreated (final FileHandle file) {
	}
}
