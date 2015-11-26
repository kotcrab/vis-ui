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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.google.gson.Gson;
import com.kotcrab.vis.editor.Log;
import com.kotcrab.vis.editor.extension.AssetType;
import com.kotcrab.vis.editor.module.editor.ExtensionStorageModule;
import com.kotcrab.vis.editor.module.editor.GsonModule;
import com.kotcrab.vis.editor.module.editor.ToastModule;
import com.kotcrab.vis.editor.module.project.assetsmanager.AssetDirectoryDescriptor;
import com.kotcrab.vis.editor.ui.toast.DetailsToast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** @author Kotcrab */
public class AssetsMetadataModule extends ProjectModule {
	private ExtensionStorageModule extensionStorage;
	private GsonModule gsonModule;
	private ToastModule toastModule;

	private FileAccessModule fileAccess;

	private FileHandle metadataFile;
	private FileHandle metadataBackupFile;
	private ObjectMap<String, String> metadata;

	private Gson gson;

	@SuppressWarnings("rawtypes")
	@Override
	public void init () {
		metadataFile = fileAccess.getModuleFolder().child("assetsMetadata.json");
		metadataBackupFile = fileAccess.getModuleFolder(".backup").child("assetsMetadata.json.bak");

		gson = gsonModule.getCommonGson();

		if (metadataFile.exists()) loadMetadata();
		if (metadata == null) metadata = new ObjectMap<>();
		verifyMetadata();
	}

	public void commit (FileHandle file, String fileType) {
		if (fileType.equals(AssetType.UNKNOWN)) {
			throw new IllegalStateException("Cannot commit unknown file type to metadata!");
		}

		String path = fileAccess.relativizeToAssetsFolder(file);
		metadata.put(path, fileType);
		saveMetadata();
	}

	public String get (FileHandle file) {
		return metadata.get(fileAccess.relativizeToAssetsFolder(file), AssetType.UNKNOWN);
	}

	public String getRecursively (FileHandle dir) {
		String relativePath = fileAccess.relativizeToAssetsFolder(dir);
		for (Entry<String, String> entry : metadata) {
			if (relativePath.startsWith(entry.key)) {
				return entry.value;
			}
		}
		return AssetType.UNKNOWN;
	}

	public AssetDirectoryDescriptor getAsDirectoryDescriptor (FileHandle file) {
		return getDirDescriptorForId(get(file));
	}

	public AssetDirectoryDescriptor getAsDirectoryDescriptorRecursively (FileHandle file) {
		return getDirDescriptorForId(getRecursively(file));
	}

	public boolean isMarked (FileHandle file) {
		return get(file) != null;
	}

	public boolean isMarkedRecursively (FileHandle file) {
		return getRecursively(file) != null;
	}

	public void remove (FileHandle file) {
		metadata.remove(fileAccess.relativizeToAssetsFolder(file));
		saveMetadata();
	}

	private AssetDirectoryDescriptor getDirDescriptorForId (String descId) {
		if (descId.equals(AssetType.UNKNOWN)) return null;

		for (AssetDirectoryDescriptor desc : extensionStorage.getAssetDirectoryDescriptors()) {
			if (descId.equals(desc.getId())) {
				return desc;
			}
		}

		return null;
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
		if (metadataFile.exists()) metadataFile.copyTo(metadataBackupFile);
		try {
			FileWriter writer = new FileWriter(metadataFile.file());
			gson.toJson(metadata, writer);
			writer.close();
		} catch (IOException e) {
			Log.exception(e);
			toastModule.show(new DetailsToast("Error occurred when saving assets metadata.\nIt's not recommend to continue.", e)); //TODO: help link what to do
		}
	}

	private void verifyMetadata () {
		Array<String> missingDescIds = new Array<>();

		for (String descId : metadata.values()) {
			if (descriptorIdExistsInAdvisableDirDescriptors(descId) == false)
				missingDescIds.add(descId);
		}

		if (missingDescIds.size > 0) {
			String msg = "A required plugin could be missing or failed to load.\n\nMissing descriptors:\n";

			for (String descId : missingDescIds) {
				msg += descId + "\n";
			}

			msg += "\nIt's not recommend to continue."; //TODO: help link what to do
			toastModule.show(new DetailsToast("Missing AssetDirectory definitions. It's not recommend to continue.", msg));
		}
	}

	private boolean descriptorIdExistsInAdvisableDirDescriptors (String descId) {
		for (AssetDirectoryDescriptor desc : extensionStorage.getAssetDirectoryDescriptors()) {
			if (descId.equals(desc.getId())) {
				return true;
			}
		}

		return false;
	}
}
