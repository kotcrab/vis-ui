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

public class MoveEntitiesAction implements UndoableAction {
	private Array<EntityProxy> entities = new Array<>();
	private final EntityManipulatorModule entityManipulatorModule;

	private Array<EntityPositionData> oldDatas = new Array<>();
	private Array<EntityPositionData> newDatas = new Array<>();

	public MoveEntitiesAction (EntityManipulatorModule entityManipulator) {
		this.entityManipulatorModule = entityManipulator;
		this.entities = new Array<>(entityManipulator.getSelectedEntities().toArray());

		for (EntityProxy entity : entities) {
			EntityPositionData positionData = new EntityPositionData();
			positionData.saveFrom(entity);
			oldDatas.add(positionData);
		}
	}

	public void saveNewData () {
		for (EntityProxy entity : entities) {
			EntityPositionData positionData = new EntityPositionData();
			positionData.saveFrom(entity);
			newDatas.add(positionData);
		}
	}

	@Override
	public void execute () {
		entityManipulatorModule.markSceneDirty();
		entities.forEach(EntityProxy::reload);

		for (int i = 0; i < entities.size; i++) {
			newDatas.get(i).loadTo(entities.get(i));
		}
	}

	@Override
	public void undo () {
		entityManipulatorModule.markSceneDirty();
		entities.forEach(EntityProxy::reload);
		for (int i = 0; i < entities.size; i++) {
			oldDatas.get(i).loadTo(entities.get(i));
		}
	}

	@Override
	public String getActionName () {
		return "Entities Move";
	}

	private static class EntityPositionData {
		public float x;
		public float y;

		public void saveFrom (EntityProxy entity) {
			x = entity.getX();
			y = entity.getY();
		}

		public void loadTo (EntityProxy entity) {
			entity.setPosition(x, y);
		}
	}

}

