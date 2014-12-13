
package pl.kotcrab.vis.editor.module;

import com.badlogic.gdx.scenes.scene2d.utils.Align;

import pl.kotcrab.vis.ui.widget.VisWindow;

public class NewSceneDialog extends VisWindow {

	public NewSceneDialog () {
		super("New Scene");

		setTitleAlignment(Align.center);
		centerWindow();
	}

}
