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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.App;

import java.io.File;

/**
 * Provides access to VisEditor common storage directories. All folders provided by this are shared between different
 * VisEditor versions. Note that those directories SHOULD NOT be used by plugins, see {@link PluginFilesAccessModule} if
 * you need that.
 * @author Kotcrab
 */
public class AppFileAccessModule extends EditorModule {
	/**
	 * VisEditor cache folder path, stores application-wide cache data (different than project cache data, that is stored inside project). This SHOULD NOT be used by plugins
	 * see {@link PluginFilesAccessModule}
	 */
	private static final String CACHE_FOLDER_PATH = App.APP_FOLDER_PATH + "cache" + File.separator;

	/** VisEditor metadata folder path. This SHOULD NOT be used by plugins see {@link PluginFilesAccessModule} */
	private static final String METADATA_FOLDER_PATH = App.APP_FOLDER_PATH + "metadata" + File.separator;

	/** VisEditor general purpose directory for configuration files. This is different from settings folder which is used exclusively by {@link EditorSettingsIOModule} */
	private static final String CONFIG_FOLDER_PATH = App.APP_FOLDER_PATH + "config" + File.separator;

	private FileHandle cacheFolder;
	private FileHandle metadataFolder;
	private FileHandle configFolder;

	@Override
	public void init () {
		cacheFolder = Gdx.files.absolute(CACHE_FOLDER_PATH);
		metadataFolder = Gdx.files.absolute(METADATA_FOLDER_PATH);
		configFolder = Gdx.files.absolute(CONFIG_FOLDER_PATH);

		cacheFolder.mkdirs();
		metadataFolder.mkdirs();
		configFolder.mkdirs();
	}

	public FileHandle getCacheFolder () {
		return cacheFolder;
	}

	public FileHandle getMetadataFolder () {
		return metadataFolder;
	}

	public FileHandle getConfigFolder () {
		return configFolder;
	}
}
