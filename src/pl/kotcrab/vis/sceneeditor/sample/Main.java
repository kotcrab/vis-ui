package pl.kotcrab.vis.sceneeditor.sample;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main
{
	public static void main(String[] args)
	{
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "VisSceneEditor - Sample";
		cfg.width = 800;
		cfg.height = 480;
		
		new LwjglApplication(new Sample(), cfg);
	}
	
}
