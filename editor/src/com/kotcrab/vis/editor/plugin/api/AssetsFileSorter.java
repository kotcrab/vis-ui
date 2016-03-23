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

package com.kotcrab.vis.editor.plugin.api;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.kotcrab.vis.editor.module.project.AssetsMetadataModule;

/**
 * Sorts file from given directory into main and misc files.
 * @author Kotcrab
 */
public interface AssetsFileSorter {
	boolean isSupported (AssetsMetadataModule assetsMetadata, FileHandle file, String assetsFolderRelativePath);

	/**
	 * Called when this context should decide if this file is main file. Such files will be showed in top part of files view.
	 * Note that user won't be able to add non-main files to scene. DragAndDrop {@link Source} creation will be skipped for them.
	 */
	boolean isMainFile (FileHandle file);

	boolean isExportedFile (FileHandle file);
}
