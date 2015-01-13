/*
 * Copyright 2014-2015 Pawel Pastuszak
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

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class FavoritesIO {
	private static String favoritesPrefsName = "pl.kotcrab.vis.ui.widget.file.filechooser_favorites";
	private static String keyName = "favorites";

	private Preferences prefs;
	private Json json = new Json();

	public static String getFavoritesPrefsName () {
		return favoritesPrefsName;
	}

	public static void setFavoritesPrefsName (String favoritesPrefsName) {
		FavoritesIO.favoritesPrefsName = favoritesPrefsName;
	}

	public FavoritesIO () {
		prefs = Gdx.app.getPreferences(favoritesPrefsName);
	}

	public Array<FileHandle> loadFavorites () {
		String data = prefs.getString(keyName, null);
		if (data == null)
			return new Array<FileHandle>();
		else
			return json.fromJson(FavouriteData.class, data).toFileHadnleArray();
	}

	public void saveFavorites (Array<FileHandle> favorites) {
		prefs.putString(keyName, json.toJson(new FavouriteData(favorites)));
		prefs.flush();
	}

	@SuppressWarnings("unused")
	private static class FavouriteData {
		public Array<FileHandleData> data;

		public FavouriteData () {

		}

		public FavouriteData (Array<FileHandle> favourites) {
			data = new Array<FileHandleData>();
			for (FileHandle file : favourites)
				data.add(new FileHandleData(file));
		}

		public Array<FileHandle> toFileHadnleArray () {
			Array<FileHandle> files = new Array<FileHandle>();

			for (FileHandleData fileData : data)
				files.add(fileData.toFileHandle());

			return files;
		}
	}

	@SuppressWarnings("unused")
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
