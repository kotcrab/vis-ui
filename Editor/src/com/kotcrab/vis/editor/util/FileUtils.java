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

package com.kotcrab.vis.editor.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.util.vis.CancelableConsumer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * File related utils.
 * @author Kotcrab
 */
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

	public static void open (FileHandle file) {
		try {
			Desktop.getDesktop().open(file.file());
		} catch (IOException e) {
			Log.exception(e);
		}
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
			Log.exception(e);
			return false;
		}
	}

	/**
	 * Checks whether sibling with extension exists for provided file. Eg .in directory there are 2 files: game.atlas and game.png, calling
	 * siblingExists(Gdx.files.internal("game.atlas"), "png") would return true.
	 */
	public static boolean siblingExists (FileHandle file, String siblingExtension) {
		return sibling(file, siblingExtension).exists();
	}

	/** Returns sibling file with provided extension eg. sibling(Gdx.files.internal("game.atlas"), "png") would return Gdx.files.internal("game.png") */
	public static FileHandle sibling (FileHandle file, String siblingExtension) {
		return file.sibling(file.nameWithoutExtension() + "." + siblingExtension);
	}

	/** Replace path extension eg. replaceExtension("/gfx/game.atlas", "png") would return "/gfx/game.png" */
	public static String replaceExtension (String path, String newExtension) {
		StringBuilder builder = new StringBuilder(path);
		builder.replace(path.lastIndexOf('.'), path.length(), '.' + newExtension);
		return builder.toString();
	}

	public static String relativize (FileHandle base, String absolute) {
		Path pathAbsolute = Paths.get(absolute);
		Path pathBase = Paths.get(base.path());
		Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative.toString().replace("\\", "/");
	}

	public static FileHandle toFileHandle (String path) {
		return Gdx.files.absolute(path);
	}

	public static FileHandle toFileHandle (File file) {
		return Gdx.files.absolute(file.getAbsolutePath());
	}

	public static void streamResursive (FileHandle folder, CancelableConsumer<FileHandle> consumer) {
		if (folder.isDirectory() == false) throw new IllegalStateException("Root must be directory!");

		for (FileHandle file : folder.list()) {
			if (file.isDirectory())
				streamResursive(folder, consumer);
			else
				consumer.accept(file);
		}
	}

	/** Removes the first directory separator from a path
	 * <p>
	 * gdx/asset.png -> asset.png
	 * @param path path where the the first directory separator is removed from
	 * @return the path without the parts that are before the separator
	 * @see com.kotcrab.vis.runtime.util.PathUtils#removeFirstSeparator(String path) */
	public static String removeFirstSeparator (String path) {
		return path.substring(path.indexOf('/') + 1);
	}
}
