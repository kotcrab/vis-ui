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
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.util.DirectoryWatcher.WatchListener;
import com.kotcrab.vis.ui.util.DialogUtils;

public class FontCacheModule extends ProjectModule implements WatchListener {
	private FileAccessModule fileAccess;
	private AssetsWatcherModule watcherModule;
	private FileHandle fontDirectory;

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		watcherModule = projectContainer.get(AssetsWatcherModule.class);

		FileHandle assetsDirectory = fileAccess.getAssetsFolder();
		fontDirectory = assetsDirectory.child("gfx").child("font");

		watcherModule.addListener(this);

		refreshFonts();
	}

	private void refreshFonts () {

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
		if (file.extension().equals("ttf")) {
			DialogUtils.showErrorDialog(Editor.instance.getStage(), "Font " + file.name() + " unexpectedly removed, please restore font file and press OK");
		}
	}

	@Override
	public void fileCreated (FileHandle file) {
		refreshFonts();
	}
}
