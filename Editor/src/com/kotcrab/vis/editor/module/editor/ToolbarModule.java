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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.ToolbarEvent;
import com.kotcrab.vis.editor.event.ToolbarEventType;
import com.kotcrab.vis.editor.event.bus.Event;
import com.kotcrab.vis.editor.event.bus.EventListener;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

/**
 * VisEditor toolbar UI widget.
 * @author Kotcrab
 */
public class ToolbarModule extends EditorModule implements EventListener {
	@InjectModule private TabsModule tabsModule;

	private VisTable table;

	private Array<VisImageButton> savableScope = new Array<>();
	private Array<VisImageButton> sceneScope = new Array<>();

	public ToolbarModule () {
		table = new VisTable(false);
		table.defaults().pad(4, 0, 4, 3);
		table.setBackground(VisUI.getSkin().getDrawable("button"));
		table.add(createButton(Icons.SAVE, "Save", ToolbarEventType.FILE_SAVE, ControllerPolicy.SAVABLE));
		table.addSeparator(true);

		ButtonGroup toolsGroup = new ButtonGroup();

		table.add(createButton(Icons.CURSOR, "Select entities", ToolbarEventType.TOOL_SELECTION, ControllerPolicy.SCENE, toolsGroup, true));
		table.add(createButton(Icons.POLYGON, "Edit polygons", ToolbarEventType.TOOL_POLYGON, ControllerPolicy.SCENE, toolsGroup, true));
		table.add().expand().fill();
	}

	@Override
	public void init () {
		App.eventBus.register(this);

		tabsModule.addListener(new TabbedPaneAdapter() {
			@Override
			public void switchedTab (Tab tab) {
				savableScope.forEach(button -> button.setDisabled(true));
				sceneScope.forEach(button -> button.setDisabled(true));

				if (tab == null) return;

				if (tab instanceof SceneTab)
					sceneScope.forEach(button -> button.setDisabled(false));

				if (tab.isSavable())
					savableScope.forEach(button -> button.setDisabled(false));
			}
		});
	}

	@Override
	public void dispose () {
		App.eventBus.unregister(this);
	}

	@Override
	public boolean onEvent (Event e) {
		return false;
	}

	public Table getTable () {
		return table;
	}

	private VisImageButton createButton (Icons icon, String text, ToolbarEventType eventType, ControllerPolicy controllerPolicy) {
		return createButton(icon, text, eventType, controllerPolicy, null, false);
	}

	private VisImageButton createButton (Icons icon, String text, ToolbarEventType eventType, ControllerPolicy controllerPolicy, ButtonGroup group, boolean toggle) {
		VisImageButton button = new VisImageButton(Assets.getIcon(icon), text);
		button.addListener(new ToolbarButtonChangeListener(eventType));
		button.setGenerateDisabledImage(true);

		if (group != null) group.add(button);
		if (toggle) button.getStyle().checked = VisUI.getSkin().getDrawable("button-down");

		switch (controllerPolicy) {
			case SAVABLE:
				savableScope.add(button);
				break;
			case SCENE:
				sceneScope.add(button);
				break;
		}

		return button;
	}

	private class ToolbarButtonChangeListener extends ChangeListener {
		private ToolbarEventType type;

		public ToolbarButtonChangeListener (ToolbarEventType eventType) {
			this.type = eventType;
		}

		@Override
		public void changed (ChangeEvent event, Actor actor) {
			App.eventBus.post(new ToolbarEvent(type));
		}
	}

	enum ControllerPolicy {
		SAVABLE, SCENE
	}
}
