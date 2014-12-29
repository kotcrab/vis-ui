/**
 * Copyright 2014 Pawel Pastuszak
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

package pl.kotcrab.vis.editor.module.project;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

@SuppressWarnings("unchecked")
public class FileAccessModule extends ProjectModule {
	private FileHandle root;
	private FileHandle visFolder;
	private FileHandle assetsFolder;
	private FileHandle modulesFolder;

	private Json json;
	private FileHandle moduleFolder;
	private FileHandle fileTypeMapFile;
	private ObjectMap<String, EditorFileType> fileTypeMap;

	@Override
	public void init () {
		root = Gdx.files.absolute(project.root);
		visFolder = root.child("vis");
		assetsFolder = visFolder.child("assets");
		modulesFolder = visFolder.child("modules");

		json = new Json();
		moduleFolder = getModuleFolder("fileAccess");

		loadFileTypeMap();
	}

	private void loadFileTypeMap () {
		fileTypeMapFile = moduleFolder.child("fileTypeMap.json");

		if (fileTypeMapFile.exists() == false)
			fileTypeMap = new ObjectMap<String, EditorFileType>();
		else
			fileTypeMap = json.fromJson(new ObjectMap<String, EditorFileType>().getClass(), fileTypeMapFile);

		Iterator<String> it = fileTypeMap.keys().iterator();
		while (it.hasNext()) {
			String path = it.next();
			if (visFolder.child(path).exists() == false) it.remove();
		}

		saveFileTypeMap();
	}

	private void saveFileTypeMap () {
		json.toJson(fileTypeMap, fileTypeMapFile);
	}

	public void addFileType (FileHandle file, EditorFileType type) {
		String relativePath = file.path().substring(visFolder.path().length());
		fileTypeMap.put(relativePath, type);
		saveFileTypeMap();
	}

	public EditorFileType getFileType (FileHandle file) {
		String relativePath = file.path().substring(visFolder.path().length());

		for (Entry<String, EditorFileType> e : fileTypeMap.entries()) {
			if (e.key.equals(relativePath)) return e.value;
		}

		return EditorFileType.UNKNOWN;
	}

	public FileHandle getVisFolder () {
		return visFolder;
	}

	public FileHandle getAssetsFolder () {
		return assetsFolder;
	}

	public FileHandle getModulesFolder () {
		return modulesFolder;
	}

	@Override
	public void dispose () {
		saveFileTypeMap();
	}

	public FileHandle getModuleFolder (String moduleName) {
		FileHandle moduleFolder = modulesFolder.child(moduleName);
		if (modulesFolder.exists() == false) moduleFolder.mkdirs();
		return moduleFolder;
	}
}
