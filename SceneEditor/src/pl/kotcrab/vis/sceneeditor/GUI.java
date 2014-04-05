
package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GUI {
	private SceneEditor sceneEditor;

	private SpriteBatch guiBatch;
	private BitmapFont font;

	public GUI (SceneEditor sceneEditor) {
		this.sceneEditor = sceneEditor;

		guiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/arial.fnt"));

	}

	public void render (int entityNumber, boolean cameraLocked, boolean dirty, Object selectedObj) {
		int line = 0;

		if (SceneEditorConfig.GUI_DRAW) {
			guiBatch.begin();

			if (SceneEditorConfig.GUI_DRAW_TITLE) drawTextAtLine("VisSceneEditor - Edit Mode - Entities: " + entityNumber, line++);

			if (cameraLocked)
				drawTextAtLine("Camera is locked.", line++);
			else
				drawTextAtLine("Camera is not locked.", line++);

			guiBatch.flush(); // is this a libgdx bug? without it cpu usage jumps to 25%

			if (dirty)
				drawTextAtLine("Unsaved changes. Exit edit mode to save them.", line++);
			else
				drawTextAtLine("All changes saved.", line++);

			line++;

			if (selectedObj != null) {
				SceneEditorSupport sup = sceneEditor.getSupportForClass(selectedObj.getClass());

				drawTextAtLine("Selected object: " + sceneEditor.getIdentifierForObject(selectedObj), line++);

				if (SceneEditorConfig.GUI_DRAW_OBJECT_INFO)
					drawTextAtLine(
						"X: " + (int)sup.getX(selectedObj) + " Y:" + (int)sup.getY(selectedObj) + " Width: "
							+ (int)sup.getWidth(selectedObj) + " Heihgt: " + (int)sup.getHeight(selectedObj), line++);
			}
			guiBatch.end();
		}
	}

	private void drawTextAtLine (String text, int line) {
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));
	}

	public void dispose () {
		guiBatch.dispose();
		font.dispose();		
	}
	
	public void resize()
	{
		guiBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
}
