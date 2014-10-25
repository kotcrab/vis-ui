
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class VisWindow extends Window {
	private Stage parent;

	public VisWindow (Stage parent, String title, Skin skin) {
		super(title, skin);
		this.parent = parent;
	}

	public void setPositionToCenter () {
		if (parent != null) setPosition((parent.getWidth() - getWidth()) / 2, (parent.getHeight() - getHeight()) / 2);
	}

}
