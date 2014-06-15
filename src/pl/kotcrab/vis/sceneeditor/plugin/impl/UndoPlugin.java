
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
			if (keycode == SceneEditorConfig.KEY_SPECIAL_UNDO) undo();
			if (keycode == SceneEditorConfig.KEY_SPECIAL_REDO) redo();
			return true; // we don't want to trigger diffrent events
		}
		
		return false;
	}
}
