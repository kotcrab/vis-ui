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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorEntity;

public class ZIndexManipulator extends SceneModule {
	private UndoModule undoModule;
	private EntityManipulatorModule entityManipulator;

	private UndoableActionGroup actionGroup;

	@Override
	public void init () {
		undoModule = sceneContainer.get(UndoModule.class);
		entityManipulator = sceneContainer.get(EntityManipulatorModule.class);
	}

	private void moveSelectedEntities (boolean up) {
		actionGroup = new UndoableActionGroup();

		Array<EditorEntity> selectedEntities = entityManipulator.getSelectedEntities();

		for (EditorEntity entity : selectedEntities) {
			moveEntity(entity, getOverlappingEntities(entity, up), up);
		}

		actionGroup.finalizeGroup();
		undoModule.add(actionGroup);
	}

	private void moveEntity (EditorEntity entity, Array<EditorEntity> overlappingEntities, boolean up) {
		if (overlappingEntities.size > 0) {
			int currentIndex = scene.entities.indexOf(entity, true);
			int targetIndex = scene.entities.indexOf(overlappingEntities.first(), true);

			for (EditorEntity overlappingEntity : overlappingEntities) {
				int sceneIndex = scene.entities.indexOf(overlappingEntity, true);
				if (up ? sceneIndex < targetIndex : sceneIndex > targetIndex)
					targetIndex = sceneIndex;
			}

			actionGroup.execute(new ZIndexChangeAction(entity, currentIndex, targetIndex));
		}
	}

	private Array<EditorEntity> getOverlappingEntities (EditorEntity entity, boolean up) {
		Array<EditorEntity> overlapping = new Array<>();
		int entityIndex = scene.entities.indexOf(entity, true);

		for (EditorEntity sceneEntity : scene.entities) {
			int sceneEntityIndex = scene.entities.indexOf(sceneEntity, true);

			if (entity != sceneEntity &&
					entity.getBoundingRectangle().overlaps(sceneEntity.getBoundingRectangle())) {

				if (up ? (entityIndex < sceneEntityIndex) : (entityIndex > sceneEntityIndex))
					overlapping.add(sceneEntity);

			}
		}

		return overlapping;
	}

	@Override
	public boolean keyDown (InputEvent event, int keycode) {
		if (keycode == Keys.PAGE_UP) {
			moveSelectedEntities(true);
			return true;
		}

		if (keycode == Keys.PAGE_DOWN) {
			moveSelectedEntities(false);
			return true;
		}

		return false;
	}

	private class ZIndexChangeAction implements UndoableAction {
		private EditorEntity entity;
		private int currentIndex;
		private int targetIndex;

		public ZIndexChangeAction (EditorEntity entity, int currentIndex, int targetIndex) {
			this.entity = entity;
			this.currentIndex = currentIndex;
			this.targetIndex = targetIndex;
		}

		@Override
		public void execute () {
			scene.entities.removeIndex(currentIndex);
			scene.entities.insert(targetIndex, entity);
		}

		@Override
		public void undo () {
			scene.entities.removeIndex(targetIndex);
			scene.entities.insert(currentIndex, entity);
		}
	}
}
