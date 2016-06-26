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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.editor.module.scene.CameraModule;
import com.kotcrab.vis.editor.module.scene.SceneModuleContainer;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.editor.scene.EditorScene;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * @author Kotcrab
 */
public abstract class Tool extends InputListener {
	private boolean loaded = false;

	protected EntityManipulatorModule entityManipulator;
	protected CameraModule camera;
	protected UndoModule undoModule;

	protected SceneModuleContainer sceneMC;
	protected EditorScene scene;

	protected EntityProxyCache entityProxyCache;

	public void setModules (SceneModuleContainer moduleContainer, EditorScene scene) {
		if (loaded) return;
		this.sceneMC = moduleContainer;
		this.scene = scene;
		moduleContainer.injectModules(this);
		loaded = true;
		init();
	}

	public void init () {
	}

	public void render (ShapeRenderer shapeRenderer) {
	}

	public void render (Batch batch) {
	}

	public void activated () {
	}

	public void deactivated () {
	}

	public void selectedEntitiesChanged () {

	}

	public void selectedEntitiesValuesChanged () {

	}

	public VisTable getToolPropertiesUI () {
		return null;
	}

	public boolean isRenderBoundsEnabled () {
		return true;
	}

	public abstract String getToolId ();
}
