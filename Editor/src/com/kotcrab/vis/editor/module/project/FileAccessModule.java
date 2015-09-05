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
	private FileHandle shaderFolder;
	private FileHandle ttfFontFolder;
	private FileHandle bmpFontFolder;
	private FileHandle particleFolder;
	private FileHandle spriterFolder;

	@Override
	public void init () {
		visFolder = project.getVisDirectory();
		assetsFolder = visFolder.child("assets");
		modulesFolder = visFolder.child("modules");

		sceneFolder = assetsFolder.child("scene");
		gfxFolder = assetsFolder.child("gfx");
		shaderFolder = assetsFolder.child("shader");
		ttfFontFolder = assetsFolder.child("font");
		bmpFontFolder = assetsFolder.child("bmpfont");
		particleFolder = assetsFolder.child("particle");
		spriterFolder = assetsFolder.child("spriter");
	}

	public Array<FileHandle> getSceneFiles () {
		Array<FileHandle> files = FileUtils.listRecursive(getSceneFolder());

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

	public FileHandle getSceneFolder () {
		return sceneFolder;
	}

	public FileHandle getGfxFolder () {
		return gfxFolder;
	}

	public FileHandle getShaderFolder () {
		return shaderFolder;
	}

	public FileHandle getParticleFolder () {
		return particleFolder;
	}

	public FileHandle getSpriterFolder () {
		return spriterFolder;
	}

	public FileHandle getBMPFontFolder () {
		return bmpFontFolder;
	}

	public String getBMPFontFolderRelative () {
		return relativizeToAssetsFolder(bmpFontFolder);
	}

	public FileHandle getTTFFontFolder () {
		return ttfFontFolder;
	}

	public String getTTFFontFolderRelative () {
		return relativizeToAssetsFolder(ttfFontFolder);
	}

	public String relativizeToVisFolder (FileHandle file) {
		return relativizeToVisFolder(file.path());
	}

	public String relativizeToVisFolder (String absolutePath) {
		return FileUtils.relativize(visFolder, absolutePath);
	}

	public String relativizeToAssetsFolder (FileHandle file) {
		return relativizeToAssetsFolder(file.path());
	}

	public String relativizeToAssetsFolder (String absolutePath) {
		return FileUtils.relativize(assetsFolder, absolutePath);
	}

	public String derelativizeFromAssetsFolder (String relativePath) {
		return assetsFolder.child(relativePath).path();
	}
}
