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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Assets;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.ToolSwitchedEvent;
import com.kotcrab.vis.editor.event.ToolbarEvent;
import com.kotcrab.vis.editor.event.ToolbarEventType;
import com.kotcrab.vis.editor.module.InjectModule;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.Tools;
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
public class ToolbarModule extends EditorModule {
	@InjectModule private TabsModule tabsModule;

	private VisTable table;

	private Array<VisImageButton> savableScope = new Array<>();
	private Array<VisImageButton> sceneScope = new Array<>();
	private final ButtonGroup<ToolButton> toolsGroup;

	public ToolbarModule () {
		table = new VisTable(false);
		table.defaults().pad(4, 0, 4, 3);
		table.setBackground(VisUI.getSkin().getDrawable("button"));
		table.add(createButton(Icons.SAVE, "Save (Ctrl + S)", ToolbarEventType.FILE_SAVE, ControllerPolicy.SAVABLE));
		table.addSeparator(true);

		toolsGroup = new ButtonGroup<>();

		table.add(createToolButton(Icons.CURSOR, Tools.SELECTION_TOOL, "Select entities (F1)"));
		table.add(createToolButton(Icons.POLYGON, Tools.POLYGON_TOOL, "Edit polygons (F2)"));
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

	@Subscribe
	public void handleToolSwitch (ToolSwitchedEvent event) {
		for (ToolButton b : toolsGroup.getButtons()) {
			if (b.getToolId() == event.newToolId) {
				b.setChecked(true);
				break;
			}
		}
	}

	public Table getTable () {
		return table;
	}

	private VisImageButton createButton (Icons icon, String text, ToolbarEventType eventType, ControllerPolicy controllerPolicy) {
		return createButton(icon, text, eventType, controllerPolicy, null, false);
	}

	private VisImageButton createToolButton (Icons icon, int toolId, String text) {
		ToolButton button = new ToolButton(Assets.getIcon(icon), text, toolId);
		button.addListener(new ToolbarButtonListener(new ToolSwitchedEvent(toolId)));
		button.setProgrammaticChangeEvents(false);
		button.setGenerateDisabledImage(true);

		return finishButtonCreation(button, ControllerPolicy.SCENE, toolsGroup, true);
	}

	private VisImageButton createButton (Icons icon, String text, ToolbarEventType eventType, ControllerPolicy controllerPolicy, ButtonGroup group, boolean toggle) {
		VisImageButton button = new VisImageButton(Assets.getIcon(icon), text);
		button.addListener(new ToolbarButtonChangeListener(eventType));
		button.setGenerateDisabledImage(true);

		return finishButtonCreation(button, controllerPolicy, group, toggle);
	}

	private VisImageButton finishButtonCreation (VisImageButton button, ControllerPolicy controllerPolicy, ButtonGroup group, boolean toggle) {
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

	private static class ToolbarButtonListener extends ChangeListener {
		private Object event;

		public ToolbarButtonListener (Object event) {
			this.event = event;
		}

		@Override
		public void changed (ChangeEvent changeEvent, Actor actor) {
			App.eventBus.post(event);
		}
	}

	@Deprecated
	private static class ToolbarButtonChangeListener extends ChangeListener {
		private ToolbarEventType type;

		public ToolbarButtonChangeListener (ToolbarEventType eventType) {
			this.type = eventType;
		}

		@Override
		public void changed (ChangeEvent event, Actor actor) {
			App.eventBus.post(new ToolbarEvent(type));
		}
	}

	private static class ToolButton extends VisImageButton {
		private int toolId;

		public ToolButton (Drawable imageUp, String tooltipText, int toolId) {
			super(imageUp, tooltipText);
			this.toolId = toolId;
		}

		public int getToolId () {
			return toolId;
		}
	}

	enum ControllerPolicy {
		SAVABLE, SCENE
	}
}
