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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;
import com.google.common.eventbus.Subscribe;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.ToggleToolbarEvent;
import com.kotcrab.vis.editor.event.ToolbarEventType;
import com.kotcrab.vis.editor.module.EventBusSubscriber;
import com.kotcrab.vis.editor.util.scene2d.ModalInputListener;

/** @author Kotcrab */
@EventBusSubscriber
public class EditingSettingsModule extends EditorModule {
	private InputModule inputModule;
	private EditorSettingsIOModule settingsIO;

	private static final String SETTINGS_NAME = "editingProperties";

	private EditingConfig config;

	private EditingInputListener inputListener = new EditingInputListener();

	@Override
	public void init () {
		config = settingsIO.load(SETTINGS_NAME, EditingConfig.class);
		inputModule.addListener(inputListener);
	}

	@Override
	public void postInit () {
		App.eventBus.post(new ToggleToolbarEvent(ToolbarEventType.GRID_SNAP_SETTING_CHANGED, config.snapToGrid));
	}

	@Override
	public void dispose () {
		settingsIO.save(config, SETTINGS_NAME);
		inputModule.removeListener(inputListener);
	}

	@Subscribe
	public void handleToggleToolbarEvent (ToggleToolbarEvent event) {
		if (event.type == ToolbarEventType.GRID_SNAP_SETTING_CHANGED) {
			config.snapToGrid = event.toggleState;
		}
	}

	public boolean isSnapToGrid () {
		return config.snapToGrid;
	}

	public boolean isSnapEnabledOrKeyPressed () {
		boolean snap = config.snapToGrid;
		if (UIUtils.ctrl()) snap = !snap; //allow to override snap setting
		return snap;
	}

	public static class EditingConfig {
		@Tag(0) public boolean snapToGrid = false;
	}

	private class EditingInputListener extends ModalInputListener {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (UIUtils.shift() && keycode == Keys.NUM_5) {
				App.eventBus.post(new ToggleToolbarEvent(ToolbarEventType.GRID_SNAP_SETTING_CHANGED, !isSnapToGrid()));
				return true;
			}

			return false;
		}
	}
}
