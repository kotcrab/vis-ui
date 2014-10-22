
package pl.kotcrab.vis.editor;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Main {
	public static void main (String[] args) {
// EventQueue.invokeLater(new Runnable() {
//
// @Override
// public void run () {
// new EditorFrame().setVisible(true);
// }
// });
		new LwjglApplication(new Editor());
	}
}
