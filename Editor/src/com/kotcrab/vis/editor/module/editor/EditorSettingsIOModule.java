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

package com.kotcrab.vis.editor.module.editor;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class EditorSettingsIOModule extends EditorModule {
	private Kryo kryo;
	private File settingsDirectory;

	@Override
	public void init () {
		kryo = new Kryo();
		settingsDirectory = new File(App.APP_FOLDER_PATH, "settings");
		settingsDirectory.mkdir();
	}

	public void save (Object configObject, String name) {
		try {
			Output output = new Output(new FileOutputStream(new File(settingsDirectory, name)));
			kryo.writeObject(output, configObject);
			output.close();
		} catch (FileNotFoundException e) {
			Log.exception(e);
		}

	}

	public <T> T load (String name, Class<T> type) {
		File configFile = new File(settingsDirectory, name);

		if (configFile.exists()) {
			try {
				Input input = new Input(new FileInputStream(configFile));
				T config = kryo.readObject(input, type);
				input.close();
				return config;
			} catch (FileNotFoundException e) {
				Log.exception(e);
			}
		}

		try {
			return  type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Log.exception(e);
		}

		throw new IllegalStateException("Failed to load settings");
	}
}
