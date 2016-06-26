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

package com.kotcrab.vis.editor.module.scene.action;

import com.kotcrab.vis.editor.scene.EditorLayer;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.scene.LayerCordsSystem;

/** @author Kotcrab */
public class ChangeLayerProperties implements UndoableAction {
	private final EditorScene scene;
	private final EditorLayer layer;

	private final String newName;
	private final LayerCordsSystem newCordsSystem;

	private final String oldName;
	private final LayerCordsSystem oldCordsSystem;

	public ChangeLayerProperties (EditorScene scene, EditorLayer layer, String newName, LayerCordsSystem newCordsSystem) {
		this.scene = scene;
		this.layer = layer;
		this.newName = newName;
		this.newCordsSystem = newCordsSystem;

		oldName = layer.name;
		oldCordsSystem = layer.cordsSystem;
	}

	@Override
	public void execute () {
		layer.name = newName;
		layer.cordsSystem = newCordsSystem;
		scene.postNotification(EditorScene.LAYER_DATA_CHANGED);
	}

	@Override
	public void undo () {
		layer.name = oldName;
		layer.cordsSystem = oldCordsSystem;
		scene.postNotification(EditorScene.LAYER_DATA_CHANGED);
	}

	@Override
	public String getActionName () {
		return "Change Layer Properties";
	}
}
