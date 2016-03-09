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

import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableAction;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;

public class ChangeEntitiesLayerAction extends UndoableActionGroup {
	private final RenderBatchingSystem renderBatchingSystem;
	private final EntityManipulatorModule entityManipulator;
	private final int targetLayerId;

	public ChangeEntitiesLayerAction (RenderBatchingSystem renderBatchingSystem, EntityManipulatorModule entityManipulator, Array<EntityProxy> proxies, int targetLayerId) {
		super("Move Entity To Layer", "Move Entities To Layer");
		this.renderBatchingSystem = renderBatchingSystem;
		this.entityManipulator = entityManipulator;
		this.targetLayerId = targetLayerId;

		for (EntityProxy proxy : proxies) {
			add(new ChangeEntityLayerAction(proxy));
		}

		finalizeGroup();
	}

	@Override
	public void execute () {
		super.execute();
		renderBatchingSystem.markDirty();
		entityManipulator.hardSelectionReset();
	}

	@Override
	public void undo () {
		super.undo();
		renderBatchingSystem.markDirty();
		entityManipulator.hardSelectionReset();
	}

	private class ChangeEntityLayerAction implements UndoableAction {
		private final int sourceLayer;
		private final EntityProxy proxy;

		public ChangeEntityLayerAction (EntityProxy proxy) {
			this.proxy = proxy;
			this.sourceLayer = proxy.getLayerID();
		}

		@Override
		public void execute () {
			proxy.reload();
			proxy.setLayerId(targetLayerId);

		}

		@Override
		public void undo () {
			proxy.reload();
			proxy.setLayerId(sourceLayer);

		}

		@Override
		public String getActionName () {
			return "Move Entity to Layer";
		}
	}
}


