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

package com.kotcrab.vis.ui.widget.file.internal;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

/** @author Kotcrab */
public class PreferencesIO {
	private static final String VIS_DEFAULT_PREFS_NAME = "com.kotcrab.vis.ui.widget.file.filechooser_favorites";
	private static String defaultPrefsName = VIS_DEFAULT_PREFS_NAME;

	private String favoritesKeyName = "favorites";
	private String recentDirKeyName = "recentDirectories";
	private String lastDirKeyName = "lastDirectory";

	private Preferences prefs;
	private Json json = new Json();

	public PreferencesIO () {
		this(defaultPrefsName);
	}

	public PreferencesIO (String prefsName) {
		prefs = Gdx.app.getPreferences(prefsName);
		checkIfUsingDefaultName();
	}

	public void checkIfUsingDefaultName () {
		if (defaultPrefsName.equals(VIS_DEFAULT_PREFS_NAME)) {
			Gdx.app.log("VisUI", "Warning, using default preferences file name for file chooser! (see FileChooser.setDefaultPrefsName(String))");
		}
	}

	public static void setDefaultPrefsName (String prefsName) {
		if (prefsName == null) throw new IllegalStateException("prefsName can't be null");
		PreferencesIO.defaultPrefsName = prefsName;
	}

	public Array<FileHandle> loadFavorites () {
		String data = prefs.getString(favoritesKeyName, null);
		if (data == null)
			return new Array<FileHandle>();
		else
			return json.fromJson(FileArrayData.class, data).toFileHandleArray();
	}

	public void saveFavorites (Array<FileHandle> favorites) {
		prefs.putString(favoritesKeyName, json.toJson(new FileArrayData(favorites)));
		prefs.flush();
	}

	public Array<FileHandle> loadRecentDirectories () {
		String data = prefs.getString(recentDirKeyName, null);
		if (data == null)
			return new Array<FileHandle>();
		else
			return json.fromJson(FileArrayData.class, data).toFileHandleArray();
	}

	public void saveRecentDirectories (Array<FileHandle> recentDirs) {
		prefs.putString(recentDirKeyName, json.toJson(new FileArrayData(recentDirs)));
		prefs.flush();
	}

	public FileHandle loadLastDirectory () {
		String data = prefs.getString(lastDirKeyName, null);
		if (data == null) return null;
		return json.fromJson(FileHandleData.class, data).toFileHandle();
	}

	public void saveLastDirectory (FileHandle file) {
		prefs.putString(lastDirKeyName, json.toJson(new FileHandleData(file)));
		prefs.flush();
	}

	private static class FileArrayData {
		public Array<FileHandleData> data;

		public FileArrayData () {

		}

		public FileArrayData (Array<FileHandle> favourites) {
			data = new Array<FileHandleData>();
			for (FileHandle file : favourites)
				data.add(new FileHandleData(file));
		}

		public Array<FileHandle> toFileHandleArray () {
			Array<FileHandle> files = new Array<FileHandle>();

			for (FileHandleData fileData : data) {
				files.add(fileData.toFileHandle());
			}

			return files;
		}
	}

	private static class FileHandleData {
		public FileType type;
		public String path;

		public FileHandleData () {
		}

		public FileHandleData (FileHandle file) {
			type = file.type();
			path = file.path();
		}

		public FileHandle toFileHandle () {
			switch (type) {
				case Absolute:
					return Gdx.files.absolute(path);
				case Classpath:
					return Gdx.files.classpath(path);
				case External:
					return Gdx.files.external(path);
				case Internal:
					return Gdx.files.internal(path);
				case Local:
					return Gdx.files.local(path);
				default:
					throw new IllegalStateException("Unknown file type!");
			}
		}
	}

}
