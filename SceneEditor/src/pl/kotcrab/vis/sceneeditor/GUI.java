/*******************************************************************************
 * Copyright 2014 Pawel Pastuszak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package pl.kotcrab.vis.sceneeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

/**
 * GUI class for rendering VisSceneEditor gui.
 * 
 * @author Pawel Pastuszak
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class GUI {
	private SceneEditor sceneEditor;

	private SpriteBatch guiBatch;
	private BitmapFont font;

	public GUI (SceneEditor sceneEditor) {
		this.sceneEditor = sceneEditor;

		guiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/arial.fnt"));
		font.setColor(SceneEditorConfig.GUI_TEXT_COLOR);
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
				drawTextAtLine("Unsaved changes.", line++);
			else
				drawTextAtLine("All changes saved.", line++);

			line++;

			if (selectedObj != null) {
				SceneEditorSupport sup = sceneEditor.getSupportForClass(selectedObj.getClass());

				drawTextAtLine("Selected object: " + sceneEditor.getIdentifierForObject(selectedObj), line++);

				if (SceneEditorConfig.GUI_DRAW_OBJECT_INFO)
					drawTextAtLine(
						"X: " + (int)sup.getX(selectedObj) + " Y:" + (int)sup.getY(selectedObj) + " Width: "
							+ (int)sup.getWidth(selectedObj) + " Height: " + (int)sup.getHeight(selectedObj) + " Rotation: "
							+ (int)sup.getRotation(selectedObj), line++);
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

	public void resize () {
		guiBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
}
