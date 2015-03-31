/*
 * Copyright 2014-2015 Pawel Pastuszak
 *
 * This file is part of VisEditor.
 *
 * VisEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VisEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VisEditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kotcrab.vis.editor.module.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.scene.EditorObject;

public class RectangularSelection {
	private Array<EditorObject> entities;
	private EntityManipulatorModule entityManipulatorModule;

	private Rectangle currentRect = null;
	private Rectangle rectToDraw = null;

	public RectangularSelection (Array<EditorObject> entities, EntityManipulatorModule entityManipulatorModule) {
		this.entities = entities;
		this.entityManipulatorModule = entityManipulatorModule;
	}

	public void render (ShapeRenderer shapeRenderer) {
		if (rectToDraw != null) {
			Gdx.gl20.glEnable(GL20.GL_BLEND);

			shapeRenderer.setColor(0.11f, 0.63f, 0.89f, 1);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();

			shapeRenderer.setColor(0.05f, 0.33f, 0.49f, 0.3f);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();
		}
	}

	public void findContainedComponents () {
		Array<EditorObject> matchingEntities = new Array<>();

		for (EditorObject entity : entities)
			if (rectToDraw.contains(entity.getBoundingRectangle())) matchingEntities.add(entity);

		entityManipulatorModule.resetSelection();
		matchingEntities.forEach(entityManipulatorModule::selectAppend);
	}

	public boolean touchDown (float x, float y, int button) {
		if (button == Buttons.LEFT) {
			currentRect = new Rectangle(x, y, 0, 0);
			updateDrawableRect();
			return true;
		}

		return false;
	}

	public boolean touchDragged (float x, float y) {
		if (currentRect != null) {
			currentRect.setSize(x - currentRect.x, y - currentRect.y);
			updateDrawableRect();
			return true;
		}

		return false;
	}

	public void touchUp () {
		if (rectToDraw != null) findContainedComponents();

		rectToDraw = null;
		currentRect = null;
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
		if (rectToDraw != null)
			rectToDraw.set(x, y, width, height);
		else
			rectToDraw = new Rectangle(x, y, width, height);

	}
}
