
package pl.kotcrab.vis.editor;

import java.awt.EventQueue;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main (String[] args) {
		if (App.USE_SWING) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run () {
					new EditorFrame().setVisible(true);
				}
			});
		} else
		{
			LwjglApplicationConfiguration c = new LwjglApplicationConfiguration();
			c.width = 1280;
			c.height = 720;
			new LwjglApplication(new Editor(), c);
		}
	}
}
