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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.module.editor.EditingSettingsModule;
import com.kotcrab.vis.editor.module.scene.action.TransformEntityAction;
import com.kotcrab.vis.editor.module.scene.system.render.GridRendererSystem.GridSettingsModule;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.runtime.util.ImmutableArray;

/** @author Kotcrab */
public class SelectionTool extends BaseSelectionTool {
	public static final String TOOL_ID = App.PACKAGE + ".tools.SelectionTool";

	private EditingSettingsModule editingSettings;
	private GridSettingsModule gridSettings;

	protected Array<EntityTransform> startingEntityProps = new Array<>();
	protected Array<TransformEntityAction> moveActions = new Array<>();

	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		boolean result = super.touchDown(event, x, y, pointer, button);
		entityManipulator.getSelectedEntities().forEach(proxy -> startingEntityProps.add(new EntityTransform(proxy)));
		return result;
	}

	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer) {
		super.touchDragged(event, x, y, pointer);

		if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
			x = camera.getInputX();
			y = camera.getInputY();

			if (dragged == false) {
				moveActions.clear();
				for (EntityProxy proxy : entityManipulator.getSelectedEntities())
					moveActions.add(new TransformEntityAction(proxy));
			}

			if (rectSelectionTouchDraggedResult == false) {

				if (dragging && entityManipulator.getSelectedEntities().size() > 0) {
					dragged = true;

					float totalDeltaX = (x - dragStartX);
					float totalDeltaY = (y - dragStartY);

					ImmutableArray<EntityProxy> entities = entityManipulator.getSelectedEntities();
					for (int i = 0; i < entities.size(); i++) {
						EntityProxy entity = entities.get(i);
						EntityTransform startingProps = startingEntityProps.get(i);

						entity.setPosition(startingProps.x + totalDeltaX, startingProps.y + totalDeltaY);

						if (editingSettings.isSnapEnabledOrKeyPressed()) {
							float gridSize = gridSettings.config.gridSize;

							float snapX;
							float snapY;

							if (entities.size() == 1) {
								//for single entity selection we can use precise coordinates
								snapX = x;
								snapY = y;
							} else {
								//for multiple use imprecise value
								//selection may not always be on grid square that mouse points to (it will aligned to some other square)
								//but this method allows to align selection of many entities at once
								snapX = entity.getX();
								snapY = entity.getY();
							}

							entity.setPosition(MathUtils.floor(snapX / gridSize) * gridSize, MathUtils.floor(snapY / gridSize) * gridSize);
						}

					}
					lastTouchX = x;
					lastTouchY = y;

					entityManipulator.markSceneDirty();
					entityManipulator.selectedEntitiesValuesChanged();
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

			UndoableActionGroup group = new UndoableActionGroup("Move Entity", "Move Entities");

			for (TransformEntityAction action : moveActions)
				group.add(action);

			group.finalizeGroup();

			undoModule.add(group);
		}

		super.touchUp(event, x, y, pointer, button);
	}

	@Override
	protected void resetAfterTouchUp () {
		super.resetAfterTouchUp();
		startingEntityProps.clear();
	}

	protected static class EntityTransform {
		float x;
		float y;
		float rotation;
		float scaleX;
		float scaleY;

		public EntityTransform (EntityProxy proxy) {
			x = proxy.getX();
			y = proxy.getY();
			rotation = proxy.getRotation();
			scaleX = proxy.getScaleX();
			scaleY = proxy.getScaleY();
		}
	}

	@Override
	public String getToolId () {
		return TOOL_ID;
	}
}
