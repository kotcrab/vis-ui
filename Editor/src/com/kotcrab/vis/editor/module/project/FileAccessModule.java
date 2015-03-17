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

package com.kotcrab.vis.editor.module.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileAccessModule extends ProjectModule {
	private FileHandle visFolder;
	private FileHandle assetsFolder;
	private FileHandle modulesFolder;

	private FileHandle sceneFolder;
	private FileHandle ttfFontFolder;
	private FileHandle bmpFontFolder;
	private FileHandle particleFolder;

	@Override
	public void init () {
		FileHandle root = Gdx.files.absolute(project.root);
		visFolder = root.child("vis");
		assetsFolder = visFolder.child("assets");
		modulesFolder = visFolder.child("modules");

		sceneFolder = assetsFolder.child("scene");
		ttfFontFolder = assetsFolder.child("font");
		bmpFontFolder = assetsFolder.child("bmpfont");
		particleFolder = assetsFolder.child("particle");
	}

	public FileHandle getVisFolder () {
		return visFolder;
	}

	/** Returns Vis assets folder */
	public FileHandle getAssetsFolder () {
		return assetsFolder;
	}

	public FileHandle getModuleFolder (String moduleName) {
		FileHandle moduleFolder = modulesFolder.child(moduleName);
		if (modulesFolder.exists() == false) moduleFolder.mkdirs();
		return moduleFolder;
	}

	public FileHandle getSceneFolder () {
		return sceneFolder;
	}

	public FileHandle getParticleFolder () {
		return particleFolder;
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
		return relativize(visFolder, absolutePath);
	}

	public String relativizeToAssetsFolder (FileHandle file) {
		return relativizeToAssetsFolder(file.path());
	}

	public String relativizeToAssetsFolder (String absolutePath) {
		return relativize(assetsFolder, absolutePath);
	}

	public String getTextureCacheRegionName (String assetsFolderRelativePath) {
		return assetsFolderRelativePath.substring(assetsFolderRelativePath.indexOf('/') + 1, assetsFolderRelativePath.lastIndexOf("."));
	}

	private String relativize (FileHandle base, String absolute) {
		Path pathAbsolute = Paths.get(absolute);
		Path pathBase = Paths.get(base.path());
		Path pathRelative = pathBase.relativize(pathAbsolute);
		return pathRelative.toString().replace("\\", "/");
	}
}
