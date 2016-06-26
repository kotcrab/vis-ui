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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.EventBusExceptionEvent;
import com.kotcrab.vis.editor.util.ApplicationUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.Dialogs.OptionDialogType;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;

/**
 * Module controlling debug features such as UI debug mode which can be enabled by pressing F12 key. Debug mode
 * must be enabled first by pressing F9.
 * @author Kotcrab
 */
public class DebugFeaturesControllerModule extends EditorModule {
	private StatusBarModule statusBar;
	private GlobalInputModule inputModule;

	private Stage stage;

	private boolean debugEnabled = false;
	private boolean uiDebugEnabled;

	private InputListener inputListener = new InputListener() {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (keycode == Keys.F9) {
				debugEnabled = !debugEnabled;
				if (debugEnabled)
					statusBar.setText("Debug mode enabled");
				else
					statusBar.setText("Debug mode disabled");

			}

			if (debugEnabled == false) return false;

			if (keycode == Keys.F6) {
				Dialogs.showOptionDialog(stage, "Warning", "Start new instance of application?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
					@Override
					public void yes () {
						ApplicationUtils.startNewInstance();
					}
				});
			}

			if (keycode == Keys.F7) {
				Dialogs.showOptionDialog(stage, "Warning", "Simulate internal exception?", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
					@Override
					public void yes () {
						App.eventBus.post(new EventBusExceptionEvent(new IllegalStateException(), null));
					}
				});
			}

			if (keycode == Keys.F8) {
				Dialogs.showOptionDialog(stage, "Warning", "Throw IllegalStateException? This will crash application.", OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
					@Override
					public void yes () {
						throw new IllegalStateException();
					}
				});
			}

			if (keycode == Keys.F11) {
				for (Actor actor : stage.getRoot().getChildren()) {
					if (actor instanceof WidgetGroup) {
						invalidateRecursively((WidgetGroup) actor);
					}
				}

				statusBar.setText("Invalidated UI layout");
			}

			if (keycode == Keys.F12) {
				uiDebugEnabled = !uiDebugEnabled;
				stage.setDebugAll(uiDebugEnabled);

				if (uiDebugEnabled)
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
