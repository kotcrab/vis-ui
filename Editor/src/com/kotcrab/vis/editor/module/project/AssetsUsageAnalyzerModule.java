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

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorEntity;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.ParticleObject;
import com.kotcrab.vis.editor.scene.SpriteObject;
import com.kotcrab.vis.editor.scene.TextObject;
import com.kotcrab.vis.editor.util.FileUtils;

import java.util.Iterator;

public class AssetsUsageAnalyzerModule extends ProjectModule {
	private FileAccessModule fileAccess;
	private SceneTabsModule sceneTabsModule;
	private SceneIOModule sceneIOModule;

	private FileHandle sceneDir;

	@Override
	public void init () {
		fileAccess = projectContainer.get(FileAccessModule.class);
		sceneTabsModule = projectContainer.get(SceneTabsModule.class);
		sceneIOModule = projectContainer.get(SceneIOModule.class);

		sceneDir = fileAccess.getSceneFolder();
	}

	public boolean canAnalyze (FileHandle file) {
		if (file.extension().equals("scene")) return false;

		return true;
	}

	public AssetsUsages analyze (FileHandle file) {
		String path = fileAccess.relativizeToAssetsFolder(file.path());
		Class clazz = guessEntityType(path);

		if (clazz == null) return null;

		Array<FileHandle> sceneFiles = getSceneFiles();

		AssetsUsages usages = new AssetsUsages();

		for (FileHandle sceneFile : sceneFiles) {
			EditorScene scene = sceneTabsModule.getSceneByPath(fileAccess.relativizeToAssetsFolder(sceneFile.path()));
			if (scene == null)
				scene = sceneIOModule.load(sceneFile);

			Array<EditorEntity> sceneUsagesList = new Array<>();
			usages.list.put(scene, sceneUsagesList);

			for (EditorEntity entity : scene.entities) {

				if (clazz.isInstance(entity)) {
					boolean used = false;

					if (entity instanceof SpriteObject) {
						SpriteObject obj = (SpriteObject) entity;
						if (obj.getCacheRegionName().equals(fileAccess.getTextureCacheRegionName(path))) used = true;

					} else if (entity instanceof TextObject) {
						TextObject obj = (TextObject) entity;
						if (obj.getRelativeFontPath().equals(path)) used = true;

					} else if (entity instanceof ParticleObject) {
						ParticleObject obj = (ParticleObject) entity;
						if (obj.getRelativeEffectPath().equals(path)) used = true;
					}

					if (used) {
						usages.count++;
						sceneUsagesList.add(entity);
					}
				}

				if (usages.count == 100) {
					usages.limitExceeded = true;
					break;
				}
			}
		}

		return usages;
	}

	private Class guessEntityType (String path) {
		if (path.startsWith("gfx")) return SpriteObject.class;
		if (path.startsWith("font") || path.startsWith("bmpfont")) return TextObject.class;
		if (path.startsWith("particle")) return ParticleObject.class;

		return null;
	}

	public Array<FileHandle> getSceneFiles () {
		Array<FileHandle> files = FileUtils.listRecursive(sceneDir);

		Iterator<FileHandle> it = files.iterator();

		while (it.hasNext())
			if (it.next().extension().equals("scene") == false) it.remove();

		return files;
	}

}
