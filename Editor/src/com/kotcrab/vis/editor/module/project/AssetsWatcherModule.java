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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.DirectoryWatcher;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

import java.io.IOException;

/**
 * Allow to add listener that will notify about changes in files in project asset directory
 * @author Kotcrab
 */
public class AssetsWatcherModule extends ProjectModule {
	private Stage stage;

	private DirectoryWatcher watcher;

	@Override
	public void init () {
		FileAccessModule fileAccess = projectContainer.get(FileAccessModule.class);
		FileHandle assetsFolder = fileAccess.getAssetsFolder();

		watcher = new DirectoryWatcher(assetsFolder.file().toPath());
		try {
			watcher.start();
		} catch (IOException e) {
			Dialogs.showErrorDialog(stage, "Error creating file watch service, automatic files reloading will not work!", e);
			Log.exception(e);
		}
	}

	@Override
	public void dispose () {
		watcher.stop();
	}

	public void addListener (DirectoryWatcher.WatchListener listener) {
		watcher.addListener(listener);
	}

	public boolean removeListener (DirectoryWatcher.WatchListener listener) {
		return watcher.removeListener(listener);
	}
}
