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

package com.kotcrab.vis.ui.widget.file;

import com.apple.eio.FileManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.util.OsUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;

/**
 * File related utils. Note that FileUtils are not available on GWT.
 * @author Kotcrab
 */
public class FileUtils {

	private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

	/** Sorts file by names ignoring upper case */
	public static final Comparator<FileHandle> FILE_NAME_COMPARATOR = new Comparator<FileHandle>() {
		@Override
		public int compare (FileHandle f1, FileHandle f2) {
			return f1.name().toLowerCase().compareTo(f2.name().toLowerCase());
		}
	};

	/** Sorts file by modified date then by name. */
	public static final Comparator<FileHandle> FILE_MODIFIED_DATE_COMPARATOR = new Comparator<FileHandle>() {
		@Override
		public int compare (FileHandle f1, FileHandle f2) {
			long l1 = f1.lastModified();
			long l2 = f2.lastModified();
			return l1 > l2 ? 1 : (l1 == l2 ? FILE_NAME_COMPARATOR.compare(f1, f2) : -1);
		}
	};

	/** Sorts file by their size then by name. */
	public static final Comparator<FileHandle> FILE_SIZE_COMPARATOR = new Comparator<FileHandle>() {
		@Override
		public int compare (FileHandle f1, FileHandle f2) {
			long l1 = f1.length();
			long l2 = f2.length();
			return l1 > l2 ? -1 : (l1 == l2 ? FILE_NAME_COMPARATOR.compare(f1, f2) : 1);
		}
	};

	/**
	 * Converts byte file size to human readable, eg:<br>
	 * 500 becomes 500 B<br>
	 * 1024 becomes 1 KB<br>
	 * 123456 becomes 120.6 KB<br>
	 * 10000000000 becomes 9.3 GB<br>
	 * Max supported unit is yottabyte (YB).
	 * @param size file size in bytes.
	 * @return human readable file size.
	 */
	public static String readableFileSize (long size) {
		if (size <= 0) return "0 B";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)).replace(",", ".") + " " + UNITS[digitGroups];
	}

	/**
	 * Sorts file list, using this rules: directories first, sorted by names ignoring uppercase, then files sorted by names
	 * ignoring uppercase.
	 * @param files list to sort
	 * @return sorted file list
	 */
	public static Array<FileHandle> sortFiles (FileHandle[] files) {
		return sortFiles(files, FILE_NAME_COMPARATOR);
	}

	/**
	 * Sorts file list, using this rules: directories first, sorted using provided comparator, then files sorted using provided comparator.
	 * @param files list to sort
	 * @param comparator comparator used to sort files and directories list
	 * @return sorted file list
	 */
	public static Array<FileHandle> sortFiles (FileHandle[] files, Comparator<FileHandle> comparator) {
		return sortFiles(files, comparator, false);
	}

	/**
	 * Sorts file list, using this rules: directories first, sorted using provided comparator, then files sorted using provided comparator.
	 * @param files list to sort
	 * @param comparator comparator used to sort files list
	 * @param descending if true then sorted list will be in reversed order
	 * @return sorted file list
	 */
	public static Array<FileHandle> sortFiles (FileHandle[] files, Comparator<FileHandle> comparator, boolean descending) {
		Array<FileHandle> directoriesList = new Array<FileHandle>();
		Array<FileHandle> filesList = new Array<FileHandle>();

		for (FileHandle f : files) {
			if (f.isDirectory()) {
				directoriesList.add(f);
			} else {
				filesList.add(f);
			}
		}

		directoriesList.sort(comparator);
		filesList.sort(comparator);

		if (descending) {
			directoriesList.reverse();
			filesList.reverse();
		}

		directoriesList.addAll(filesList); // combine lists
		return directoriesList;
	}

	/**
	 * Checks whether given name is valid for current user OS.
	 * @param name that will be checked
	 * @return true if name is valid, false otherwise
	 */
	public static boolean isValidFileName (String name) {
		try {
			if (OsUtils.isWindows()) {
				if (name.contains(">") || name.contains("<")) return false;
				name = name.toLowerCase(); //Windows is case insensitive
			}
			return new File(name).getCanonicalFile().getName().equals(name);
		} catch (Exception e) {
			return false;
		}
	}

	/** Converts {@link File} to absolute {@link FileHandle}. */
	public static FileHandle toFileHandle (File file) {
		return Gdx.files.absolute(file.getAbsolutePath());
	}

	/** Shows given directory in system explorer window. */
	@SuppressWarnings("unchecked")
	public static void showDirInExplorer (FileHandle dir) throws IOException {
		File dirToShow;
		if (dir.isDirectory()) {
			dirToShow = dir.file();
		} else {
			dirToShow = dir.parent().file();
		}

		if (OsUtils.isMac()) {
			FileManager.revealInFinder(dirToShow);
		} else {
			try {
				// Using reflection to avoid importing AWT desktop which would trigger Android Lint errors
				// This is desktop only, rarely called, performance drop is negligible
				// Basically 'Desktop.getDesktop().open(dirToShow);'
				Class desktopClass = Class.forName("java.awt.Desktop");
				Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
				desktopClass.getMethod("open", File.class).invoke(desktop, dirToShow);
			} catch (Exception e) {
				Gdx.app.log("VisUI", "Can't open file " + dirToShow.getPath(), e);
			}
		}
	}
}
