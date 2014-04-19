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
	private KeyboardInputMode keyboardInputMode;
	private Array<ObjectRepresentation> selectedObjs;

	private SpriteBatch guiBatch;
	private BitmapFont font;

	private boolean renderFlashingCursor;
	private long startTime;

	public GUI (KeyboardInputMode keyboardInputMode, Array<ObjectRepresentation> selectedObjs) {
		this.keyboardInputMode = keyboardInputMode;
		this.selectedObjs = selectedObjs;

		guiBatch = new SpriteBatch();
		font = new BitmapFont(Gdx.files.internal("pl/kotcrab/vis/arial.fnt"));
		font.setColor(SceneEditorConfig.GUI_TEXT_COLOR);
	}

	public void render (int entityNumber, boolean cameraLocked, boolean dirty, boolean exitingEditMode) {
		int line = 0;

		if (SceneEditorConfig.GUI_DRAW) {
			guiBatch.begin();

			if (SceneEditorConfig.GUI_DRAW_TITLE) drawTextAtLine("VisSceneEditor - Edit Mode - Entities: " + entityNumber, line++);

			if (exitingEditMode) {
				String text = "Unsaved changes, save before exit? (Y/N)";
				if (renderFlashingCursor) text += "_";

				drawTextAtLine(text, line++);
			} else {
				if (cameraLocked)
					drawTextAtLine("Camera is locked.", line++);
				else
					drawTextAtLine("Camera is not locked.", line++);

				if (dirty)
					drawTextAtLine("Unsaved changes.", line++);
				else
					drawTextAtLine("All changes saved.", line++);

				line++;

				if (selectedObjs.size == 1) {
					ObjectRepresentation orep = selectedObjs.first();
					drawTextAtLine("Selected object: " + orep.getIdentifier(), line++);

					if (SceneEditorConfig.GUI_DRAW_OBJECT_INFO) {
						drawTextAtLine(buildSingleObjectInfo(orep), line++);
					}

				} else if (selectedObjs.size > 1) {
					drawTextAtLine("Multiple objects selected: " + selectedObjs.size, line++);
					drawTextAtLine(buildMultipleObjectInfo(), line++);
				}

				if (selectedObjs.size > 0) {
					if (keyboardInputMode.isActive()) {
						String text = "Input new " + keyboardInputMode.getEditTypeText() + ": "
							+ keyboardInputMode.getEditingValueText();

						if (renderFlashingCursor) text += "_";
						
						drawTextAtLine(text, line++);
					}
				}
			}

			guiBatch.end();

			if (TimeUtils.millis() - startTime > 500) {
				renderFlashingCursor = !renderFlashingCursor;
				startTime = TimeUtils.millis();
			}
		}
	}

	private String buildSingleObjectInfo (ObjectRepresentation orep) {
		String info = "X: " + (int)orep.getX() + " Y:" + (int)orep.getY() + " Width: " + (int)orep.getWidth() + " Height: "
			+ (int)orep.getHeight();

		if (orep.isRotatingSupported()) info += " Rotation: " + (int)orep.getRotation();

		return info;
	}

	private String buildMultipleObjectInfo () {

		String info = "X: ";
		if (checkIfAllSelectedObjectHaveSameX())
			info += (int)selectedObjs.first().getX();
		else
			info += "?";

		info += " Y: ";
		if (checkIfAllSelectedObjectHaveSameY())
			info += (int)selectedObjs.first().getY();
		else
			info += "?";

		info += " Width: ";
		if (checkIfAllSelectedObjectHaveSameWidth())
			info += (int)selectedObjs.first().getWidth();
		else
			info += "?";

		info += " Height: ";
		if (checkIfAllSelectedObjectHaveSameHeight())
			info += (int)selectedObjs.first().getHeight();
		else
			info += "?";

		info += " Rotation: ";
		if (checkIfAllSelectedObjectHaveSameRotation())
			info += (int)selectedObjs.first().getRotation();
		else
			info += "?";

		return info;
	}

	private void drawTextAtLine (String text, int line) {
		font.draw(guiBatch, text, 2, Gdx.graphics.getHeight() - 2 - (line * 17));

		if (line % 3 == 0) // is this a libgdx bug? without it cpu usage jumps to 25%, need to be done every 3 text lines...
			guiBatch.flush();
	}

	private boolean checkIfAllSelectedObjectHaveSameX () {
		int value = (int)selectedObjs.first().getX();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getX()) return false;
		}

		return true;
	}

	private boolean checkIfAllSelectedObjectHaveSameY () {
		int value = (int)selectedObjs.first().getY();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getY()) return false;
		}

		return true;
	}

	private boolean checkIfAllSelectedObjectHaveSameWidth () {
		int value = (int)selectedObjs.first().getWidth();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getWidth()) return false;
		}

		return true;
	}

	private boolean checkIfAllSelectedObjectHaveSameHeight () {
		int value = (int)selectedObjs.first().getHeight();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getHeight()) return false;
		}

		return true;
	}

	private boolean checkIfAllSelectedObjectHaveSameRotation () {
		int value = (int)selectedObjs.first().getRotation();

		for (ObjectRepresentation orep : selectedObjs) {
			if (value != orep.getRotation()) return false;
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
