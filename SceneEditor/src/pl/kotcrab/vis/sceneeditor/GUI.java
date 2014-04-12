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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

/** GUI class for rendering VisSceneEditor gui.
 * 
 * @author Pawel Pastuszak */
class GUI {
	private SceneEditor sceneEditor;
	private KeyboardInputMode keyboardInputMode;
	private Array<ObjectRepresentation> selectedObjs;

	private SpriteBatch guiBatch;
	private BitmapFont font;

	private boolean renderFlashingCursor;
	private long startTime;

	public GUI (SceneEditor sceneEditor, KeyboardInputMode keyboardInputMode, Array<ObjectRepresentation> selectedObjs) {
		this.sceneEditor = sceneEditor;
		this.keyboardInputMode = keyboardInputMode;
		this.selectedObjs = selectedObjs;

		guiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("data/arial.fnt"));
		font.setColor(SceneEditorConfig.GUI_TEXT_COLOR);
	}

	public void render (int entityNumber, boolean cameraLocked, boolean dirty) {
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

			if (selectedObjs.size == 1) {
				for (ObjectRepresentation orep : selectedObjs) {
					drawTextAtLine("Selected object: " + sceneEditor.getIdentifierForObject(orep.obj), line++);

					if (SceneEditorConfig.GUI_DRAW_OBJECT_INFO) {
						drawTextAtLine("X: " + (int)orep.getX() + " Y:" + (int)orep.getY() + " Width: " + (int)orep.getWidth()
							+ " Height: " + (int)orep.getHeight() + " Rotation: " + (int)orep.getRotation(), line++);
					}

				}
			} else if (selectedObjs.size > 1) {
				drawTextAtLine("Multiple objects selected.", line++);

			}

			if (selectedObjs.size > 0) {
				if (keyboardInputMode.isActive()) {
					if (renderFlashingCursor)
						drawTextAtLine(
							"Input new " + keyboardInputMode.getEditTypeText() + ": " + keyboardInputMode.getEditingValueText() + "_",
							line++);
					else
						drawTextAtLine(
							"Input new " + keyboardInputMode.getEditTypeText() + ": " + keyboardInputMode.getEditingValueText(), line++);

					if (TimeUtils.millis() - startTime > 500) {
						renderFlashingCursor = !renderFlashingCursor;
						startTime = TimeUtils.millis();
					}
				}
			}

			guiBatch.end();
		}
	}

	private void drawTextAtLine (String text, int line) {
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));
	}

	private boolean checkIfAllSelectedObjectHaveSameX () {
		int value = (int)selectedObjs.first().getX();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getX()) return false;
		}

		return true;
	}

	public void dispose () {
		guiBatch.dispose();
		font.dispose();
	}

	public void resize () {
		guiBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
}
