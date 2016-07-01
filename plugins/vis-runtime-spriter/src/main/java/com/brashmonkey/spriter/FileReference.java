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

package com.brashmonkey.spriter;

/**
 * Represents a reference to a specific file.
 * A file reference consists of a folder and file index.
 * @author Trixt0r
 */
public class FileReference {

	public int folder, file;

	public FileReference (int folder, int file) {
		this.set(folder, file);
	}

	@Override
	public int hashCode () {
		return folder * 10000 + file;//We can have 10000 files per folder
	}

	@Override
	public boolean equals (Object ref) {
		if (ref instanceof FileReference) {
			return this.file == ((FileReference) ref).file && this.folder == ((FileReference) ref).folder;
		} else return false;
	}

	public void set (int folder, int file) {
		this.folder = folder;
		this.file = file;
	}

	public void set (FileReference ref) {
		this.set(ref.folder, ref.file);
	}

	public boolean hasFile () {
		return this.file != -1;
	}

	public boolean hasFolder () {
		return this.folder != -1;
	}

	public String toString () {
		return "[folder: " + folder + ", file: " + file + "]";
	}

}
