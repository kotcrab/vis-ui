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
package pl.kotcrab.vis.sceneeditor.plugin.impl;

import pl.kotcrab.vis.sceneeditor.ObjectRepresentation;
import pl.kotcrab.vis.sceneeditor.plugin.PluginState;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.IObjectManager;
import pl.kotcrab.vis.sceneeditor.plugin.interfaces.Renderable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class RectangularSelectionPlugin extends PluginState implements Renderable {
	private IObjectManager objectManager;

	private Rectangle currentRect = null;
	private Rectangle rectToDraw = null;
	private Rectangle previousRectDrawn = new Rectangle();

	private int drawingPointer = -1;

	public RectangularSelectionPlugin (IObjectManager objectManager) {
		this.objectManager = objectManager;
	}

	public void renderSelf (ShapeRenderer shapeRenderer) {
		if (rectToDraw != null) {
			Gdx.gl20.glEnable(GL20.GL_BLEND);

			shapeRenderer.setColor(Color.RED);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();

			shapeRenderer.setColor(0.7f, 0f, 0f, 0.3f);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();
		}
	}

	public void findContainedComponents () {
		Array<ObjectRepresentation> matchingObjects = new Array<ObjectRepresentation>();

		for (ObjectRepresentation orep : objectManager.getObjectRepresenationList())
			if (rectToDraw.contains(orep.getBoundingRectangle())) matchingObjects.add(orep);

		objectManager.getSelectedObjs().clear();
		objectManager.getSelectedObjs().addAll(matchingObjects); // we can't just swap tables
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		if (button == Buttons.RIGHT) {
			drawingPointer = pointer;
			currentRect = new Rectangle(x, y, 0, 0);
			updateDrawableRect();
			return true;
		}

		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		if (drawingPointer == pointer && !Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			findContainedComponents();
			rectToDraw = null;
			drawingPointer = -1;
			return true;
		}

		return false;
	}

	@Override
	public boolean touchDragged (int x, int y, int pointer) {
		if (drawingPointer == pointer && Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			currentRect.setSize(x - currentRect.x, y - currentRect.y);
			updateDrawableRect();
			return true;
		}

		return false;
	}

	private void updateDrawableRect () {
		float x = currentRect.x;
		float y = currentRect.y;
		float width = currentRect.width;
		float height = currentRect.height;

		// Make the width and height positive, if necessary.
		if (width < 0) {
			width = 0 - width;
			x = x - width + 1;
		}

		if (height < 0) {
			height = 0 - height;
			y = y - height + 1;
		}

		// Update rectToDraw after saving old value.
		if (rectToDraw != null) {
			previousRectDrawn.set(rectToDraw.x, rectToDraw.y, rectToDraw.width, rectToDraw.height);
			rectToDraw.set(x, y, width, height);
		} else {
			rectToDraw = new Rectangle(x, y, width, height);
		}
	}
 }
