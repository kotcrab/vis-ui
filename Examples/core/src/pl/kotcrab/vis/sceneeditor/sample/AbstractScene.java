package pl.kotcrab.vis.sceneeditor.sample;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class AbstractScene
{
	public abstract void resize();
	public abstract void dispose();
	public abstract void render(SpriteBatch batch);
}