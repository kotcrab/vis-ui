
package pl.kotcrab.vis.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UI {
	public static final boolean DEBUG = false;
	public static Skin skin;

	public static void load () {
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
	}

	public static void dispose () {
		skin.dispose();
	}
}
