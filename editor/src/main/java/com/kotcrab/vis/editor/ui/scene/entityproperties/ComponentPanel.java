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

package com.kotcrab.vis.editor.ui.scene.entityproperties;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.editor.module.ModuleInjector;
import com.kotcrab.vis.editor.module.scene.UndoModule;
import com.kotcrab.vis.editor.module.scene.action.ComponentRemoveAction;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.EntityManipulatorModule;
import com.kotcrab.vis.editor.module.scene.system.VisComponentManipulator;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.editor.util.undo.UndoableActionGroup;
import com.kotcrab.vis.runtime.util.ImmutableArray;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/** @author Kotcrab */
public class ComponentPanel extends VisTable {
	private static final Drawable treePlus = VisUI.getSkin().getDrawable("tree-plus");
	private static final Drawable treeMinus = VisUI.getSkin().getDrawable("tree-minus");
	private static final Drawable treeOver = VisUI.getSkin().getDrawable("tree-over");

	private EntityManipulatorModule entityManipulatorModule;
	private UndoModule undoModule;

	private Image image;

	public ComponentPanel (ModuleInjector injector, VisComponentManipulator componentManipulator, String name, ComponentTable componentTable) {
		injector.injectModules(this);
		VisTable topTable = new VisTable(true);
		topTable.setBackground(treeOver);

		image = new Image(treeMinus, Scaling.none);

		VisLabel nameLabel = new VisLabel(name);
		nameLabel.setEllipsis(true);

		topTable.add(image).size(22).spaceRight(0);
		topTable.add(nameLabel).spaceRight(0).width(205);
		topTable.add().space(0).expandX().fillX();
		if (componentTable.isRemovable()) {
			VisImageButton button = new VisImageButton("close");
			VisImageButtonStyle style = button.getStyle();
			Drawable up = style.up;
			style.up = style.over;
			style.over = up;
			topTable.add(button);

			button.addListener(new VisChangeListener((event, actor) -> {
				ImmutableArray<EntityProxy> proxies = entityManipulatorModule.getSelectedEntities();

				UndoableActionGroup undoableActionGroup = new UndoableActionGroup("Remove components");

				for (EntityProxy proxy : proxies) {
					undoableActionGroup.add(new ComponentRemoveAction(injector, proxy, componentTable.getComponentClass()));
				}

				undoableActionGroup.finalizeGroup();
				undoModule.execute(undoableActionGroup);
			}));
		}

		CollapsibleWidget collapsible = new CollapsibleWidget(componentTable, false);
		add(topTable).expandX().fillX().row();
		add(collapsible).expandX().fillX().padTop(3).padLeft(6);

		image.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				collapsible.setCollapsed(!collapsible.isCollapsed(), false);
				image.setDrawable(collapsible.isCollapsed() ? treePlus : treeMinus);
			}
		});
	}
}
