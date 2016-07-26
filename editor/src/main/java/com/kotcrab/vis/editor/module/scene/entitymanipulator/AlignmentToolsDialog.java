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

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.TransformEntityAction;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class AlignmentToolsDialog extends VisTable {
	private EntityManipulatorModule entityManipulator;
	private UndoModule undoModule;

	public AlignmentToolsDialog (ModuleInjector injector) {
		injector.injectModules(this);

		setBackground(VisUI.getSkin().getDrawable("window-bg"));
		setTouchable(Touchable.enabled);
		setVisible(false);

		top().left();
		defaults().left();
		TableUtils.setSpacingDefaults(this);

		VisTable horizontalAlign = new VisTable(true);

		VisImageButton alignLeft = new VisImageButton(Icons.ALIGN_LEFT.drawable(), "Align entities at left edge");
		VisImageButton alignRight = new VisImageButton(Icons.ALIGN_RIGHT.drawable(), "Align entities at right edge");
		VisImageButton alignCenterX = new VisImageButton(Icons.ALIGN_CENTER_X.drawable(), "Center entities at x axis");
		VisImageButton alignTop = new VisImageButton(Icons.ALIGN_BOTTOM.drawable(), "Align entities at top edge");
		VisImageButton alignBottom = new VisImageButton(Icons.ALIGN_TOP.drawable(), "Align entities at bottom edge");
		VisImageButton alignCenterY = new VisImageButton(Icons.ALIGN_CENTER_Y.drawable(), "Center entities at y axis");

		VisImageButton closeButton = new VisImageButton("close-window");

		horizontalAlign.add("Horizontal").width(70);
		horizontalAlign.add(alignLeft);
		horizontalAlign.add(alignRight);
		horizontalAlign.add(alignCenterX);

		VisTable verticalAlign = new VisTable(true);

		verticalAlign.add("Vertical").width(70);
		verticalAlign.add(alignTop);
		verticalAlign.add(alignBottom);
		verticalAlign.add(alignCenterY);

		add(new VisLabel("Alignment", Align.center)).expandX().fillX().top();
		add(closeButton).right().row();
		add(horizontalAlign).padLeft(3).colspan(2).row();
		add(verticalAlign).padLeft(3).colspan(2);

		pack();

		alignLeft.addListener(new VisChangeListener((event, actor) -> alignSelected(Align.left)));
		alignRight.addListener(new VisChangeListener((event, actor) -> alignSelected(Align.right)));
		alignCenterX.addListener(new VisChangeListener((event, actor) -> alignSelectedCenter(false)));
		alignTop.addListener(new VisChangeListener((event, actor) -> alignSelected(Align.top)));
		alignBottom.addListener(new VisChangeListener((event, actor) -> alignSelected(Align.bottom)));
		alignCenterY.addListener(new VisChangeListener((event, actor) -> alignSelectedCenter(true)));

		closeButton.addListener(new VisChangeListener((event, actor) -> setVisible(false)));
	}

	private void alignSelected (int align) {
		ImmutableArray<EntityProxy> selectedEntities = entityManipulator.getSelectedEntities();
		if (selectedEntities.size() == 0) return;

		UndoableActionGroup undoableGroup = new UndoableActionGroup("Change Alignment");

		if (align == Align.left || align == Align.right) {
			float targetX = selectedEntities.get(0).getX();

			for (EntityProxy proxy : selectedEntities) {
				if (align == Align.left) {
					if (proxy.getX() < targetX)
						targetX = proxy.getX();
				} else {
					if (proxy.getX() > targetX)
						targetX = proxy.getX();
				}
			}

			for (EntityProxy proxy : selectedEntities) {
				TransformEntityAction action = new TransformEntityAction(proxy);
				proxy.setX(targetX);
				action.saveNewData(proxy);
				undoableGroup.add(action);
			}
		}

		if (align == Align.top || align == Align.bottom) {
			float targetY = selectedEntities.get(0).getY();

			for (EntityProxy proxy : selectedEntities) {
				if (align == Align.top) {
					if (proxy.getY() < targetY)
						targetY = proxy.getY();
				} else {
					if (proxy.getY() > targetY)
						targetY = proxy.getY();
				}
			}

			for (EntityProxy proxy : selectedEntities) {
				TransformEntityAction action = new TransformEntityAction(proxy);
				proxy.setY(targetY);
				action.saveNewData(proxy);
				undoableGroup.add(action);
			}
		}

		undoableGroup.finalizeGroup();
		undoModule.add(undoableGroup);
	}

	private void alignSelectedCenter (boolean yAlign) {
		ImmutableArray<EntityProxy> selectedEntities = entityManipulator.getSelectedEntities();
		if (selectedEntities.size() == 0) return;

		UndoableActionGroup undoableGroup = new UndoableActionGroup("Change Alignment");

		float targetPos = 0;

		for (EntityProxy proxy : selectedEntities) {
			if (yAlign)
				targetPos += proxy.getX();
			else
				targetPos += proxy.getY();
		}

		targetPos /= selectedEntities.size();

		for (EntityProxy proxy : selectedEntities) {
			TransformEntityAction action = new TransformEntityAction(proxy);

			if (yAlign == true)
				proxy.setX(targetPos);
			else
				proxy.setY(targetPos);

			action.saveNewData(proxy);
			undoableGroup.add(action);
		}

		undoableGroup.finalizeGroup();
		undoModule.add(undoableGroup);
	}

	@Override
	public void setVisible (boolean visible) {
		super.setVisible(visible);
		invalidateHierarchy();
	}

	@Override
	public float getPrefHeight () {
		if (isVisible())
			return super.getPrefHeight() + 4;
		else
			return 0;
	}
}
