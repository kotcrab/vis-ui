
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuItem {
	public TextButton button;

	public MenuItem (String text, ChangeListener listener) {
		this(text);
		button.addListener(listener);
	}

	public MenuItem (String text) {
		button = new TextButton(text, UI.skin, "menu");
		button.getLabel().setAlignment(Align.left);
	}

	public void addListener (ChangeListener listener) {
		button.addListener(listener);
	}
}
