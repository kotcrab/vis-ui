
package pl.kotcrab.vis.editor;

import java.awt.EventQueue;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

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
			new LwjglApplication(new Editor());
	}
}
