package com.kotcrab.vis.launcher;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.common.utils.JarUtils;

public class FileAccess {
	private FileHandle jarDir;

	private FileHandle dataFolder;
	private FileHandle editorFolder;

	private FileHandle cacheFolder;

	public FileAccess () {
		jarDir = Gdx.files.absolute(JarUtils.getJarPath(FileAccess.class));

		dataFolder = jarDir.child("data");
		editorFolder = jarDir.child("editor");

		cacheFolder = dataFolder.child(".cache");

		dataFolder.mkdirs();
		editorFolder.mkdirs();
		cacheFolder.mkdirs();
	}

	public FileHandle getEditorFolder () {
		return editorFolder;
	}

	public FileHandle getDataFolder () {
		return dataFolder;
	}

	public FileHandle getCacheFolder () {
		return cacheFolder;
	}
}
