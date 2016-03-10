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

import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

/** @author Kotcrab */
public class ChangeZIndexAction implements UndoableAction {
	private RenderBatchingSystem renderBatchingSystem;
	private EntityManipulatorModule entityManipulator;

	private final int sourceZIndex;
	private EntityProxy proxy;
	private final int targetZIndex;

	public ChangeZIndexAction (RenderBatchingSystem renderBatchingSystem, EntityManipulatorModule entityManipulator, EntityProxy proxy, int targetZIndex) {
		this.renderBatchingSystem = renderBatchingSystem;
		this.entityManipulator = entityManipulator;
		this.proxy = proxy;
		this.targetZIndex = targetZIndex;

		sourceZIndex = proxy.getZIndex();
	}

	@Override
	public void execute () {
		proxy.reload();
		proxy.setZIndex(targetZIndex);
		renderBatchingSystem.markDirty();
		entityManipulator.selectedEntitiesValuesChanged();
	}

	@Override
	public void undo () {
		proxy.reload();
		proxy.setZIndex(sourceZIndex);
		renderBatchingSystem.markDirty();
		entityManipulator.selectedEntitiesValuesChanged();
	}

	@Override
	public String getActionName () {
		return "Change Z Index";
	}
}
