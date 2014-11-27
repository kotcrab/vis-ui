
package pl.kotcrab.vis.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {
	public static TextureAtlas icons;

	public static void load () {
		icons = new TextureAtlas(Gdx.files.internal("icons.atlas"));
	}

	public static void dispose () {
		icons.dispose();
	}
	
	public static Drawable getIcon(String name)
	{
		return new TextureRegionDrawable(icons.findRegion(name));
	}

}
