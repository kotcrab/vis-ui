/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.App;
import com.kotcrab.vis.editor.event.StatusBarEvent;

public class UndoModule extends SceneModule {
	private InputModule input;

	private Array<UndoableAction> undoList;
	private Array<UndoableAction> redoList;

	private boolean tabActive;

	@Override
	public void added () {
		undoList = new Array<>();
		redoList = new Array<>();

		input = containter.get(InputModule.class);
		input.addListener(new UndoInputListener());
	}


	public void undo () {
		if (undoList.size > 0) {
			UndoableAction action = undoList.pop();
			action.undo();
			redoList.add(action);
		} else
			App.eventBus.post(new StatusBarEvent("Can't undo more!"));
	}

	public void redo () {
		if (redoList.size > 0) {
			UndoableAction action = redoList.pop();
			action.execute();
			undoList.add(action);
		} else
			App.eventBus.post(new StatusBarEvent("Can't redo more!"));
	}

	public void execute (UndoableAction action) {
		action.execute();
		undoList.add(action);
		redoList.clear();
	}

	@Override
	public void onHide () {
		tabActive = false;
	}

	@Override
	public void onShow () {
		tabActive = true;
	}

	private class UndoInputListener extends InputListener {
		public boolean keyDown (InputEvent event, int keycode) {
			if(tabActive) {
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
