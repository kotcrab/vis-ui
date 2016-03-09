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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.util.FileUtils;

import java.util.Iterator;

/**
 * Provides FileHandle access to various Vis Project directories and files, contains method for path (de)relativize
 * @author Kotcrab
 */
public class FileAccessModule extends ProjectModule {
	private FileHandle visFolder;
	private FileHandle assetsFolder;
	private FileHandle modulesFolder;

	private FileHandle sceneFolder;
	private FileHandle gfxFolder;

	@Override
	public void init () {
		visFolder = project.getVisDirectory();
		assetsFolder = visFolder.child("assets");
		modulesFolder = visFolder.child("modules");

		sceneFolder = assetsFolder.child("scene");
		gfxFolder = assetsFolder.child("gfx");
	}

	public Array<FileHandle> getSceneFiles () {
		Array<FileHandle> files = FileUtils.listRecursive(getAssetsFolder());

		Iterator<FileHandle> it = files.iterator();

		while (it.hasNext())
			if (it.next().extension().equals("scene") == false) it.remove();

		files.sort((o1, o2) -> o1.path().toLowerCase().compareTo(o2.path().toLowerCase()));

		return files;
	}

	public FileHandle getVisFolder () {
		return visFolder;
	}

	/** @return assets folder inside vis directory */
	public FileHandle getAssetsFolder () {
		return assetsFolder;
	}

	public FileHandle getModuleFolder () {
		return modulesFolder;
	}

	public FileHandle getModuleFolder (String moduleName) {
		FileHandle moduleFolder = modulesFolder.child(moduleName);
		if (modulesFolder.exists() == false) moduleFolder.mkdirs();
		return moduleFolder;
	}

	@Deprecated
	public FileHandle getGfxFolder () {
		return gfxFolder;
	}

	public String relativizeToVisFolder (FileHandle file) {
		return FileUtils.relativize(visFolder, file);
	}

	public String relativizeToAssetsFolder (FileHandle file) {
		return FileUtils.relativize(assetsFolder, file);
	}

	public String derelativizeFromAssetsFolder (String relativePath) {
		return assetsFolder.child(relativePath).path();
	}
}
