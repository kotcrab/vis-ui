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
import com.badlogic.gdx.utils.ObjectSet;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.editor.util.FileUtils;
import com.kotcrab.vis.editor.util.vis.ProjectPathUtils;

/** @author Kotcrab */
public class TextureNameCheckerModule extends ProjectModule implements WatchListener {
	private ToastModule toastModule;

	private AssetsWatcherModule assetsWatcher;
	private FileAccessModule fileAccess;

	private ObjectSet<String> paths = new ObjectSet<>();

	private boolean warningShown;

	@Override
	public void init () {
		assetsWatcher.addListener(this);

		FileUtils.streamFilesRecursively(fileAccess.getAssetsFolder(), this::fileCreated);
	}

	@Override
	public void dispose () {
		assetsWatcher.removeListener(this);
	}

	@Override
	public void fileCreated (FileHandle file) {
		if (warningShown) return;

		if (ProjectPathUtils.isTexture(file) == false) return;

		String pathWithoutExt = file.pathWithoutExtension();
		if (paths.contains(pathWithoutExt)) {
			//TODO details dialog supporting auto text wrapping
			toastModule.show(new DetailsToast("Warning, found invalid textures in gfx directory", "Details",
					"Files inside gfx subdirectories cannot have same name but different extension." +
							"\nVisEditor does not store image extension in TextureAtlas thus you cannot have " +
							"2 files\nin single directory that have the same name but different extension eg. `image.png` " +
							"\nand `image.jpg`. It is recommend to remove one of those conflicting files, otherwise " +
							"\nonly one of them will be available.\n\nFile: " + pathWithoutExt));
			warningShown = true;
		} else {
			paths.add(pathWithoutExt);
		}
	}

	@Override
	public void fileDeleted (FileHandle file) {
		if (warningShown) return;
		String pathWithoutExt = file.pathWithoutExtension();
		paths.remove(pathWithoutExt);
	}
}
