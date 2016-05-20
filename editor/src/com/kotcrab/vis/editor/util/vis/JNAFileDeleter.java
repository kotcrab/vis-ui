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

package com.kotcrab.vis.editor.util.vis;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.sun.jna.platform.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Special implementation of {@link FileChooser.FileDeleter} that can be used if your project is using JNA library. When set
 * to file chooser using {@link FileChooser#setFileDeleter(FileChooser.FileDeleter)}, when user deletes file it will be moved
 * to trash instead of being deleted permanently. This also changes chooser texts to ensure that user knows that file
 * is moved to trash. Trying to create this class without JNA will result in {@link ClassNotFoundException}s.
 * <p>
 * Note that user system may not support trash even if JNA is used, in that case file chooser behaviour won't be changed.
 * @author Kotcrab
 */
public final class JNAFileDeleter implements FileChooser.FileDeleter {
	private final FileUtils fileUtils = FileUtils.getInstance();

	@Override
	public boolean hasTrash () {
		return fileUtils.hasTrash();
	}

	@Override
	public boolean delete (FileHandle file) throws IOException {
		if (hasTrash()) {
			fileUtils.moveToTrash(new File[]{file.file()});
			return true;
		} else {
			return file.delete();
		}
	}
}
