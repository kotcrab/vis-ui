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
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * Module controlling UI debug mode which can be enabled by pressing F12 key.
 * @author Kotcrab
 */
public class UIDebugControllerModule extends EditorModule {
	private StatusBarModule statusBar;
	private GlobalInputModule inputModule;

	private Stage stage;

	private boolean debugEnabled;

	private InputListener inputListener = new InputListener() {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (keycode == Keys.F11) {
				for (Actor actor : stage.getRoot().getChildren()) {
					if (actor instanceof WidgetGroup) {
						invalidateRecursively((WidgetGroup) actor);
					}
				}

				statusBar.setText("Invalidated UI layout");
			}

			if (keycode == Keys.F12) {
				debugEnabled = !debugEnabled;
				stage.setDebugAll(debugEnabled);

				if (debugEnabled)
					statusBar.setText("UI debug mode was enabled! (press F12 to disable)");
				else
					statusBar.setText("UI debug mode was disabled!");

				return true;
			}

			return false;
		}
	};

	private void invalidateRecursively (WidgetGroup group) {
		group.invalidate();

		for (Actor actor : group.getChildren()) {
			if (actor instanceof WidgetGroup)
				invalidateRecursively((WidgetGroup) actor);
			else if (actor instanceof Layout)
				((Layout) actor).invalidate();
		}
	}

	@Override
	public void init () {
		inputModule.addListener(inputListener);
	}

	@Override
	public void dispose () {
		inputModule.removeListener(inputListener);
	}
}
