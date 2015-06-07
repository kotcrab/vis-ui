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
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.Event;
import com.kotcrab.vis.editor.event.EventListener;
import com.kotcrab.vis.editor.event.TexturesReloadedEvent;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.scene.*;
import com.kotcrab.vis.editor.util.gdx.SpriteUtils;

public class SceneCacheModule extends ProjectModule implements EventListener {
	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private SceneIOModule sceneIO;

	private ObjectMap<FileHandle, EditorScene> scenes = new ObjectMap<>();

	@Override
	public void init () {
		App.eventBus.register(this);
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	public EditorScene get (FileHandle fullPath) {
		EditorScene scene = scenes.get(fullPath);

		if (scene == null) {
			scene = sceneIO.load(fullPath);
			scenes.put(fullPath, scene);
		}

		return scene;
	}

	@Override
	public boolean onEvent (Event event) {
		if (event instanceof TexturesReloadedEvent) {
			for (EditorScene scene : scenes.values()) {
				for (Layer layer : scene.layers) {
					for (EditorObject object : layer.entities) {
						if (object instanceof SpriteObject) {
							SpriteObject spriteObject = (SpriteObject) object;
							SpriteUtils.setRegion(spriteObject.getSprite(), textureCache.getRegion(spriteObject.getAssetDescriptor()));
						}

						if (object instanceof ObjectGroup) {
							ObjectGroup group = (ObjectGroup) object;
							group.reloadTextures(textureCache);
						}
					}
				}
			}
		}

		return false;
	}
}
