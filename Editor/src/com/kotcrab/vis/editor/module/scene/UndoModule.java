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

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.RedoEvent;
import com.kotcrab.vis.editor.event.StatusBarEvent;
import com.kotcrab.vis.editor.event.UndoEvent;
import com.kotcrab.vis.editor.module.editor.InputModule;
import com.kotcrab.vis.editor.util.gdx.ModalInputListener;
import com.kotcrab.vis.editor.util.undo.UndoableAction;

/**
 * Manages undoable actions and provides ctrl+z + ctrl+y key shortcuts.
 * @author Kotcrab
 */
public class UndoModule extends SceneModule {
	private Array<UndoableAction> undoList;
	private Array<UndoableAction> redoList;

	private boolean tabActive;

	@Override
	public void added () {
		undoList = new Array<>();
		redoList = new Array<>();

		InputModule input = container.get(InputModule.class);
		input.addListener(new UndoInputListener());
	}

	public void undo () {
		if (undoList.size > 0) {
			UndoableAction action = undoList.pop();
			action.undo();
			redoList.add(action);
			App.eventBus.post(new UndoEvent());
		} else
			App.eventBus.post(new StatusBarEvent("Can't undo more!"));

	}

	public void redo () {
		if (redoList.size > 0) {
			UndoableAction action = redoList.pop();
			action.execute();
			undoList.add(action);
			App.eventBus.post(new RedoEvent());
		} else
			App.eventBus.post(new StatusBarEvent("Can't redo more!"));

	}

	public void execute (UndoableAction action) {
		action.execute();
		add(action);
	}

	public void add (UndoableAction action) {
		undoList.add(action);
		redoList.clear();
		sceneTab.dirty();
	}

	public int getUndoSize () {
		return undoList.size;
	}

	@Override
	public void onHide () {
		tabActive = false;
	}

	@Override
	public void onShow () {
		tabActive = true;
	}

	private class UndoInputListener extends ModalInputListener {
		@Override
		public boolean keyDown (InputEvent event, int keycode) {
			if (tabActive) {
				if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
					if (keycode == Keys.Z) undo();
					if (keycode == Keys.Y) redo();

					return true;
				}
			}

			return false;
		}
	}
}
