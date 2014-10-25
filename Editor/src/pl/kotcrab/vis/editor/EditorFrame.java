
package pl.kotcrab.vis.editor;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class EditorFrame extends JFrame {
	public EditorFrame () {
		setSize(800, 480);
		setTitle("Vis Editor");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		LwjglAWTCanvas editorCanvas = new LwjglAWTCanvas(new Editor());
		editorCanvas.getCanvas().setSize(800, 480);

		getContentPane().add(editorCanvas.getCanvas(), BorderLayout.CENTER);

		pack();
		setVisible(true);
	}

	@Override
	public void dispose () {
		super.dispose();
		Gdx.app.exit();
	}
}
