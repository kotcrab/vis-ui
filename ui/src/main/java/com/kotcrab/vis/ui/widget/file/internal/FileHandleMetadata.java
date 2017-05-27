/*
 * Copyright 2014-2017 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.widget.file.FileUtils;

public class FileHandleMetadata {
	private final String name;
	private final boolean directory;
	private final long lastModified;
	private final long length;
	private final String readableFileSize;

	public static FileHandleMetadata of (FileHandle file) {
		return new FileHandleMetadata(file);
	}

	private FileHandleMetadata (FileHandle file) {
		this.name = file.name();
		this.directory = file.isDirectory();
		this.lastModified = file.lastModified();
		this.length = file.length();
		this.readableFileSize = FileUtils.readableFileSize(length);
	}

	public String name () {
		return name;
	}

	public boolean isDirectory () {
		return directory;
	}

	public long lastModified () {
		return lastModified;
	}

	public long length () {
		return length;
	}

	public String readableFileSize () {
		return readableFileSize;
	}
}
