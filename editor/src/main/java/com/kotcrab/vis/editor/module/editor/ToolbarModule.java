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

package com.kotcrab.vis.editor.module.editor;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Icons;
import com.kotcrab.vis.editor.event.ToggleToolbarEvent;
import com.kotcrab.vis.editor.event.ToolSwitchedEvent;
import com.kotcrab.vis.editor.event.ToolbarEvent;
import com.kotcrab.vis.editor.event.ToolbarEventType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.PolygonTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.RotateTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.ScaleTool;
import com.kotcrab.vis.editor.module.scene.entitymanipulator.tool.SelectionTool;
import com.kotcrab.vis.editor.ui.scene.SceneTab;
import com.kotcrab.vis.editor.util.gdx.ArrayUtils;
import com.kotcrab.vis.editor.util.scene2d.EventButtonChangeListener;
import com.kotcrab.vis.editor.util.scene2d.VisChangeListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.OsUtils;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

/**
 * VisEditor toolbar UI widget.
 * @author Kotcrab
 */
@EventBusSubscriber
public class ToolbarModule extends EditorModule {
	private TabsModule tabsModule;

	private VisTable table;

	private Array<VisImageButton> savableScope = new Array<>();
	private Array<VisImageButton> sceneScope = new Array<>();
	private final ButtonGroup<ToolButton> toolsGroup;
	private String activeToolId = SelectionTool.TOOL_ID;

	public ToolbarModule () {
		toolsGroup = new ButtonGroup<>();

		table = new VisTable(false);
		table.defaults().pad(4, 0, 4, 3);
		table.setBackground(VisUI.getSkin().getDrawable("button"));

		table.add(new ToolbarButtonBuilder().icon(Icons.SAVE)
				.text("Save (" + OsUtils.getShortcutFor(Keys.CONTROL_LEFT, Keys.S) + ")").eventToolbar(ToolbarEventType.FILE_SAVE)
				.policy(ControllerPolicy.SAVABLE).build());

		table.addSeparator(true);
		table.add(new ToolbarButtonBuilder().icon(Icons.TOOL_MOVE).text("Select and move entities (F1)").eventTool(SelectionTool.TOOL_ID).build());
		table.add(new ToolbarButtonBuilder().icon(Icons.TOOL_ROTATE).text("Rotate entities (F2)").eventTool(RotateTool.TOOL_ID).build());
		table.add(new ToolbarButtonBuilder().icon(Icons.TOOL_SCALE).text("Scale entities (F3)").eventTool(ScaleTool.TOOL_ID).build());
		table.add(new ToolbarButtonBuilder().icon(Icons.POLYGON).text("Edit polygons (F4)").eventTool(PolygonTool.TOOL_ID).build());

		table.addSeparator(true);
		table.add(new ToolbarButtonBuilder().icon(Icons.SETTINGS_VIEW).text("Enable grid snapping (%)").eventToolbar(ToolbarEventType.GRID_SNAP_SETTING_CHANGED).toggle().build());

		table.add().expand().fill();

		savableScope.forEach(button -> button.setDisabled(true));
		sceneScope.forEach(button -> button.setDisabled(true));
	}

	@Override
	public void init () {
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

			@Override
			public void removedAllTabs () {
				savableScope.forEach(button -> button.setDisabled(true));
				sceneScope.forEach(button -> button.setDisabled(true));
			}
		});
	}

	@Subscribe
	public void handleToolSwitch (ToolSwitchedEvent event) {
		for (ToolButton b : toolsGroup.getButtons()) {
			if (b.getToolId().equals(event.newToolId)) {
				b.setChecked(true);
				activeToolId = event.newToolId;
				break;
			}
		}
	}

	@Subscribe
	public void handleToggleToolbarEvent (ToggleToolbarEvent event) {
		ArrayUtils.cancelableStream(table.getChildren(), ToolbarButton.class, button -> {
			if (button.eventType == event.type) {
				button.setChecked(event.toggleState);
				return true;
			}

			return false;
		});
	}

	public String getActiveToolId () {
		return activeToolId;
	}

	public Table getTable () {
		return table;
	}

	private static class ToolbarButton extends VisImageButton {
		private ToolbarEventType eventType;

		public ToolbarButton (Drawable imageUp, String tooltipText, ToolbarEventType eventType) {
			super(imageUp, tooltipText);
			this.eventType = eventType;
		}
	}

	private static class ToolButton extends VisImageButton {
		private String toolId;

		public ToolButton (Drawable imageUp, String tooltipText, String toolId) {
			super(imageUp, tooltipText);
			this.toolId = toolId;
		}

		public String getToolId () {
			return toolId;
		}

	}

	enum ControllerPolicy {
		SAVABLE, SCENE, NONE
	}

	public class ToolbarButtonBuilder {
		private Icons icon;
		private String text;

		private ControllerPolicy policy = ControllerPolicy.NONE;
		private ButtonGroup group;

		private boolean toggle;

		private ToolbarEventType type;

		private String toolId = null;

		public ToolbarButtonBuilder icon (Icons icon) {
			this.icon = icon;
			return this;
		}

		public ToolbarButtonBuilder text (String text) {
			this.text = text;
			return this;
		}

		public ToolbarButtonBuilder policy (ControllerPolicy policy) {
			this.policy = policy;
			return this;
		}

		public ToolbarButtonBuilder group (ButtonGroup group) {
			this.group = group;
			return this;
		}

		public ToolbarButtonBuilder toggle () {
			this.toggle = true;
			return this;
		}

		public ToolbarButtonBuilder eventTool (String toolId) {
			this.toolId = toolId;
			this.group = toolsGroup;
			this.toggle = true;
			return this;
		}

		public ToolbarButtonBuilder eventToolbar (ToolbarEventType type) {
			this.type = type;
			return this;
		}

		public VisImageButton build () {
			VisImageButton button;

			if (toolId != null) {
				button = new ToolButton(icon.drawable(), text, toolId);
				button.addListener(new EventButtonChangeListener(new ToolSwitchedEvent(toolId)));
			} else {
				button = new ToolbarButton(icon.drawable(), text, type);

				if (toggle)
					button.addListener(new VisChangeListener((changeEvent, actor) -> App.eventBus.post(new ToggleToolbarEvent(type, button.isChecked()))));
				else
					button.addListener(new EventButtonChangeListener(new ToolbarEvent(type)));

			}

			button.setGenerateDisabledImage(true);
			button.setProgrammaticChangeEvents(false);

			if (group != null) group.add(button);

			if (toggle) {
				button.getStyle().checked = VisUI.getSkin().getDrawable("button-down");
				button.getStyle().focusBorder = null;
			}

			switch (policy) {
				case SAVABLE:
					savableScope.add(button);
					break;
				case SCENE:
					sceneScope.add(button);
					break;
				case NONE:
					break;
			}

			return button;
		}
	}
}
