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

package com.kotcrab.vis.editor.assets.transaction.action;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.project.FileAccessModule;
import com.kotcrab.vis.editor.module.project.SceneCacheModule;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.scene.Layer;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.assets.VisAssetDescriptor;

/**
 * Undoable action for updating assets references in Entities. This is generic case usage that replaces provided source {@link VisAssetDescriptor} with provided target.
 * @author Kotcrab
 * @see UpdateAtlasAssetReferencesAction
 */
public class UpdateReferencesAction implements UndoableAction {
	@InjectModule private SceneCacheModule sceneCache;
	@InjectModule private FileAccessModule fileAccess;

	private VisAssetDescriptor source;
	private VisAssetDescriptor target;

	public UpdateReferencesAction (ModuleInjector injector, VisAssetDescriptor source, VisAssetDescriptor target) {
		injector.injectModules(this);
		this.source = source;
		this.target = target;
	}

	@Override
	public void execute () {
		for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
			EditorScene scene = sceneCache.get(sceneFile);

			for (Layer layer : scene.layers) {
				for (EditorObject entity : layer.entities) {
					if (entity.getAssetDescriptor().compare(source)) entity.setAssetDescriptor(target);
				}
			}
		}
	}

	@Override
	public void undo () {
		for (FileHandle sceneFile : fileAccess.getSceneFiles()) {
			EditorScene scene = sceneCache.get(sceneFile);

			for (Layer layer : scene.layers) {
				for (EditorObject entity : layer.entities) {
					if (entity.getAssetDescriptor().compare(target)) entity.setAssetDescriptor(source);
				}
			}
		}
	}
}
