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

package com.kotcrab.vis.editor.module.scene.system;

import com.artemis.Manager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.ChangeZIndexAction;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.runtime.system.render.RenderBatchingSystem;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class ZIndexManipulator extends Manager {
	private UndoModule undoModule;
	private EntityManipulatorModule entityManipulator;

	private RenderBatchingSystem renderBatchingSystem;
	private EntityProxyCache proxyCache;

	private UndoableActionGroup actionGroup;

	public void moveSelectedEntities (ImmutableArray<EntityProxy> selectedEntities, boolean up) {
		actionGroup = new UndoableActionGroup("Change Z Index");

		for (EntityProxy entity : selectedEntities.newIterator()) {
			moveEntity(entity, getOverlappingEntities(entity, up), up);
		}

		actionGroup.finalizeGroup();

		if (actionGroup.size() > 0) undoModule.add(actionGroup);
	}

	private void moveEntity (EntityProxy entity, Array<EntityProxy> overlappingEntities, boolean up) {
		int targetZIndex = entity.getZIndex();

		overlappingEntities.sort((o1, o2) -> (int) Math.signum(o1.getZIndex() - o2.getZIndex()) * (up ? 1 : -1));

		for (EntityProxy proxy : overlappingEntities) {
			if (up) {
				if (targetZIndex <= proxy.getZIndex()) {
					targetZIndex = proxy.getZIndex() + 1;
					break;
				}
			} else {
				if (targetZIndex >= proxy.getZIndex()) {
					targetZIndex = proxy.getZIndex() - 1;
					break;
				}
			}
		}

		if (targetZIndex != entity.getZIndex()) {
			actionGroup.execute(new ChangeZIndexAction(renderBatchingSystem, entityManipulator, entity, targetZIndex));
		}
	}

	private Array<EntityProxy> getOverlappingEntities (EntityProxy baseEntity, boolean up) {
		Values<EntityProxy> entities = proxyCache.getCache().values();
		Array<EntityProxy> overlapping = new Array<>();

		for (EntityProxy entity : entities) {
			if (entity.getLayerID() != baseEntity.getLayerID()) continue;

			if (baseEntity != entity && baseEntity.getBoundingRectangle().overlaps(entity.getBoundingRectangle())) {

				if (up ? (baseEntity.getZIndex() <= entity.getZIndex()) : (baseEntity.getZIndex() >= entity.getZIndex()))
					overlapping.add(entity);

			}
		}

		return overlapping;
	}
}
