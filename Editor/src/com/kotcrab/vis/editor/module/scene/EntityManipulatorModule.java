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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.editor.ColorPickerModule;
import com.kotcrab.vis.editor.module.project.*;
import com.kotcrab.vis.editor.scene.EditorObject;
import com.kotcrab.vis.editor.ui.scene.entityproperties.EntityProperties;

/**
 * Entity manipulator module, allows to move entities on scene. Providers right click menu, rectangular selection
 * and properties window. Supports undo and redo.
 * @author Kotcrab
 */
@Deprecated
public class EntityManipulatorModule extends SceneModule{
	@InjectModule private CameraModule camera;
	@InjectModule private UndoModule undoModule;
	@InjectModule private SceneIOModule sceneIOModule;
	@InjectModule private ObjectSupportModule supportManager;
	@InjectModule private FileAccessModule fileAccess;
	@InjectModule private ColorPickerModule colorPickerModule;
	@InjectModule private TextureCacheModule textureCache;
	@InjectModule private FontCacheModule fontCacheModule;

	private EntityProperties entityProperties;
	private final Array<EditorObject> selectedEntities = new Array<>();

	@Override
	public void init () {
		entityProperties = new EntityProperties(supportManager, fileAccess, fontCacheModule, undoModule, colorPickerModule.getPicker(), sceneTab, selectedEntities);
	}

	@Override
	public void postInit () {
		entityProperties.loadSupportsSpecificTables(projectContainer.get(ObjectSupportModule.class));
	}

	@Override @Deprecated
	public void dispose () {
		entityProperties.dispose();
	}

}
