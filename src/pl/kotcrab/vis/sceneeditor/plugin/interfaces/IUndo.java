
package pl.kotcrab.vis.sceneeditor.plugin.interfaces;

import pl.kotcrab.vis.sceneeditor.EditorAction;

import com.badlogic.gdx.utils.Array;

public interface IUndo {
	public void addToUndoList (Array<EditorAction> undos);
}
