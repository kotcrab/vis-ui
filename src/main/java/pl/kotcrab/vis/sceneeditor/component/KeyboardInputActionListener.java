
package pl.kotcrab.vis.sceneeditor.component;

import pl.kotcrab.vis.sceneeditor.EditorAction;

import com.badlogic.gdx.utils.Array;

public interface KeyboardInputActionListener {
	public void editingFinished (Array<EditorAction> actions);
}
