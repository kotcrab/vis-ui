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

package com.kotcrab.vis.editor.module.scene.entitymanipulator.tool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.kotcrab.vis.editor.module.scene.action.MoveEntityAction;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;

/** @author Kotcrab */
public class SelectionTool extends BaseSelectionTool {
	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		super.touchDragged(event, x, y, pointer);

		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			x = camera.getInputX();
			y = camera.getInputY();

			if (dragged == false) {
				moveActions.clear();
				for (EntityProxy proxy : entityManipulator.getSelectedEntities())
					moveActions.add(new MoveEntityAction(proxy));
			}

			if (rectSelectionTouchDraggedResult == false) {

				if (dragging && entityManipulator.getSelectedEntities().size() > 0) {
					dragged = true;
					float deltaX = (x - lastTouchX);
					float deltaY = (y - lastTouchY);

					for (EntityProxy entity : entityManipulator.getSelectedEntities())
						entity.setPosition(entity.getX() + deltaX, entity.getY() + deltaY);

					lastTouchX = x;
					lastTouchY = y;

					entityManipulator.markSceneDirty();
					entityManipulator.selectedEntitiesChanged();
				}

			}
		}

		if (Gdx.input.isButtonPressed(Buttons.RIGHT))
			cameraDragged = true;
	}

	@Override
	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
		if (dragged) {
			for (int i = 0; i < entityManipulator.getSelectedEntities().size(); i++)
				moveActions.get(i).saveNewData(entityManipulator.getSelectedEntities().get(i));

			UndoableActionGroup group = new UndoableActionGroup("Move entities");

			for (MoveEntityAction action : moveActions)
				group.add(action);

			group.finalizeGroup();

			undoModule.add(group);
		}

		super.touchUp(event, x, y, pointer, button);
	}
}
