/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.ui.widget.file;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

/** @author Kotcrab */
public class FileUtils {
	private static final String OS = System.getProperty("os.name").toLowerCase();
	private static final boolean windows = OS.contains("win");
	private static final boolean mac = OS.contains("mac");
	private static final boolean unix = OS.contains("nix") || OS.contains("nux") || OS.contains("aix");

	private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "EB"};

	private static final Comparator<FileHandle> FILE_COMPARATOR = new Comparator<FileHandle>() {
		@Override
		public int compare (FileHandle f1, FileHandle f2) {
			return f1.name().toLowerCase().compareTo(f2.name().toLowerCase());
		}
	};

	public static String readableFileSize (long size) {
		if (size <= 0) return "0 B";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)).replace(",", ".") + " " + UNITS[digitGroups];
	}

	public static Array<FileHandle> sortFiles (FileHandle[] files) {
		Array<FileHandle> directoriesList = new Array<FileHandle>();
		Array<FileHandle> filesList = new Array<FileHandle>();

		Arrays.sort(files, FILE_COMPARATOR);

		for (int i = 0; i < files.length; i++) {
			FileHandle f = files[i];
			if (f.isDirectory())
				directoriesList.add(f);
			else
				filesList.add(f);
		}

		directoriesList.addAll(filesList); // combine lists
		return directoriesList;
	}

	public static boolean isValidFileName (String name) {
		try {
			if (isWindows()) if (name.contains(">") || name.contains("<")) return false;
			return new File(name).getCanonicalFile().getName().equals(name);
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isWindows () {
		return windows;
	}

	public static boolean isMac () {
		return mac;
	}

	public static boolean isUnix () {
		return unix;
	}

	public static FileHandle toFileHandle (File file) {
		return Gdx.files.absolute(file.getAbsolutePath());
	}
}
