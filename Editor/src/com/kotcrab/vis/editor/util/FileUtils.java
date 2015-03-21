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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class FileUtils {
	private static com.sun.jna.platform.FileUtils jnaFileUtils;

	static {
		jnaFileUtils = com.sun.jna.platform.FileUtils.getInstance();
	}

	public static Array<FileHandle> listRecursive (FileHandle baseDir) {
		Array<FileHandle> files = new Array<>();
		list(files, baseDir);
		return files;
	}

	private static void list (Array<FileHandle> files, FileHandle current) {
		for (FileHandle file : current.list()) {
			if (file.isDirectory())
				list(files, current);
			else
				files.add(file);
		}
	}

	public static boolean hasTrash () {
		return jnaFileUtils.hasTrash();
	}

	public static void browse (FileHandle dir) {
		try {
			if (dir.isDirectory())
				Desktop.getDesktop().open(dir.file());
			else
				Desktop.getDesktop().open(dir.parent().file());
		} catch (IOException e) {
			Log.exception(e);
		}
	}

	/**
	 * Trashes file if possible, if not the file is just deleted
	 * @return if success, false otherwise
	 * @see #hasTrash
	 */
	public static boolean delete (FileHandle file) {
		try {
			if (hasTrash())
				jnaFileUtils.moveToTrash(new File[]{file.file()});
			else
				file.deleteDirectory();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
