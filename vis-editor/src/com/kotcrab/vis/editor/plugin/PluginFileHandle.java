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

package com.kotcrab.vis.editor.plugin;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.io.InputStream;

/**
 * FileHandle used to access files from inside plugin jars. Please note that some functionality of FileHandle won't be available.
 * @author Kotcrab
 */
public class PluginFileHandle extends FileHandle {
	private Class baseClass;
	private String filePath;

	public PluginFileHandle (Class baseClass, String filePath) {
		super((File) null, FileType.Classpath);
		this.baseClass = baseClass;
		this.filePath = filePath.replace("\\", "/");
	}

	@Override
	public FileHandle child (String name) {
		return new PluginFileHandle(baseClass, filePath + '/' + name);
	}

	@Override
	public FileHandle sibling (String name) {
		return new PluginFileHandle(baseClass, name);
	}

	@Override
	public FileHandle parent () {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream read () {
		InputStream stream = baseClass.getResourceAsStream(filePath);
		if (stream == null) throw new IllegalStateException("PluginFileHandle could not find file: " + filePath);
		return stream;
	}

	@Override
	public boolean exists () {
		throw new UnsupportedOperationException();
	}

	@Override
	public long length () {
		return 0;
	}

	@Override
	public long lastModified () {
		return 0;
	}

	@Override
	public String path () {
		return filePath;
	}

	@Override
	public String name () {
		int slashIndex = filePath.lastIndexOf('/');
		if (slashIndex == -1) return filePath;
		return filePath.substring(slashIndex + 1);
	}

	@Override
	public String extension () {
		String name = name();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) return "";
		return name.substring(dotIndex + 1);
	}

	@Override
	public String nameWithoutExtension () {
		String name = name();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1) return name;
		return name.substring(0, dotIndex);
	}

	@Override
	public String pathWithoutExtension () {
		int dotIndex = filePath.lastIndexOf('.');
		if (dotIndex == -1) return filePath;
		return filePath.substring(dotIndex + 1);
	}

	@Override
	public File file () {
		throw new UnsupportedOperationException();
	}

	@Override
	public void copyTo (FileHandle dest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void moveTo (FileHandle dest) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals (Object o) {
		if (o instanceof PluginFileHandle == false) return false;

		PluginFileHandle file = (PluginFileHandle) o;
		if (baseClass.equals(file.baseClass) == false) return false;
		return filePath.equals(file.filePath);
	}

	@Override
	public int hashCode () {
		int hash = super.hashCode();
		hash = 31 * hash + baseClass.hashCode();
		return hash;
	}

	@Override
	public String toString () {
		return filePath;
	}
}
