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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;

//TODO support dynamic refreshing
public class ParticleCacheModule extends ProjectModule implements WatchListener {
	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcherModule;

	private FileHandle particleDirectory;

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		watcherModule = projectContainer.get(AssetsWatcherModule.class);

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
