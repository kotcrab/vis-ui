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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.Editor;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.module.scene.InputModule;

public class UIDebugControllerModule extends EditorModule {
	private InputModule inputModule;
	private Stage stage;
	private boolean debugEnabled;

	private InputListener inputListener = new InputListener() {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (keycode == Keys.F12) {
				debugEnabled = !debugEnabled;
				stage.setDebugAll(debugEnabled);

				if (debugEnabled)
					App.eventBus.post(new StatusBarEvent("UI debug mode was enabled! (press F12 to disable)"));
				else
					App.eventBus.post(new StatusBarEvent("UI debug mode was disabled!"));

				return true;
			}

			return false;
		}
	};

	@Override
	public void init () {
		stage = Editor.instance.getStage();
		inputModule = container.get(InputModule.class);
		inputModule.addListener(inputListener);
	}

	@Override
	public void dispose () {
		inputModule.removeListener(inputListener);
	}
}
