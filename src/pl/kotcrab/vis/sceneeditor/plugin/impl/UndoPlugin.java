/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
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
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.EditorAction;
import pl.kotcrab.vis.sceneeditor.SceneEditorConfig;
import pl.kotcrab.vis.sceneeditor.plugin.PluginAdapter;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IUndo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class UndoPlugin extends PluginAdapter implements IUndo {
	private Array<Array<EditorAction>> undoList;
	private Array<Array<EditorAction>> redoList;

	public UndoPlugin () {
		undoList = new Array<Array<EditorAction>>();
		redoList = new Array<Array<EditorAction>>();
	}

	public void undo () {
		if (undoList.size > 0) {
			Array<EditorAction> actions = undoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			redoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't undo any more!");
	}

	public void redo () {
		if (redoList.size > 0) {
			Array<EditorAction> actions = redoList.pop();

			for (EditorAction action : actions)
				action.switchValues();

			undoList.add(actions);
		} else
			Gdx.app.log(TAG, "Can't redo any more!");
	}

	@Override
	public void addToUndoList (Array<EditorAction> undos) {
		undoList.add(undos);
	}

	@Override
	public boolean keyDown (int keycode) {
		if (Gdx.input.isKeyPressed(SceneEditorConfig.KEY_SPECIAL_ACTIONS)) {
			//in perfect world we would return true after calling undo or redo FIX'ME maybe
			if (keycode == SceneEditorConfig.KEY_SPECIAL_UNDO) undo();
			if (keycode == SceneEditorConfig.KEY_SPECIAL_REDO) redo();
		}

		return false;
	}
}
