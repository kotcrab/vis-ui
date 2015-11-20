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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.assets.AssetType;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.serializer.json.ObjectMapJsonSerializer;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

/** @author Kotcrab */
public class AssetsMetadataModule extends ProjectModule {
	private ToastModule toastModule;

	private FileAccessModule fileAccess;

	private FileHandle metadataFile;
	private FileHandle metadataBackupFile;
	private ObjectMap<FileHandle, String> metadata;

	private Gson gson;

	@SuppressWarnings("rawtypes")
	@Override
	public void init () {
		metadataFile = fileAccess.getModuleFolder().child("assetsMetadata.json");
		metadataBackupFile = fileAccess.getModuleFolder(".backup").child("assetsMetadata.json.bak");

		GsonBuilder builder = new GsonBuilder()
				.setPrettyPrinting()
				.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
				.registerTypeAdapter(ObjectMap.class, new ObjectMapJsonSerializer());

		gson = builder.create();

		if (metadataFile.exists()) {
			loadMetadata();
		} else {
			metadata = new ObjectMap<>();
		}
	}

	private void commit (FileHandle file, String fileType) {
		if (fileType.equals(AssetType.UNKNOWN))
			throw new IllegalStateException("Cannot commit unknown file type to metadata!");

		metadata.put(file, fileType);
		saveMetadata();
	}

	private String get (FileHandle file) {
		return metadata.get(file, AssetType.UNKNOWN);
	}

	private void loadMetadata () {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(metadataFile.file()));
			metadata = gson.fromJson(reader, ObjectMap.class);
			reader.close();
		} catch (IOException e) {
			Log.exception(e);
			toastModule.show(new DetailsToast("Error occurred when loading assets metadata.\nIt's not recommend to continue.", e)); //TODO: help link what to do
		}
	}

	private void saveMetadata () {
		metadataFile.copyTo(metadataBackupFile);
		try {
			FileWriter writer = new FileWriter(metadataFile.file());
			gson.toJson(metadata, writer);
			writer.close();
		} catch (IOException e) {
			Log.exception(e);
			toastModule.show(new DetailsToast("Error occurred when saving assets metadata.\nIt's not recommend to continue.", e)); //TODO: help link what to do
		}
	}
}
