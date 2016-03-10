/*
 * Copyright 2014-2016 See AUTHORS file.
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
 */

package com.kotcrab.vis.editor.module.scene.entitymanipulator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.editor.module.scene.system.EntityProxyCache;
import com.kotcrab.vis.editor.proxy.EntityProxy;
import com.kotcrab.vis.editor.scene.EditorScene;

/**
 * Allows to select entities using rectangular selection
 * @author Kotcrab
 */
public class RectangularSelection {
	private static final Color INNER_MODE_BORDER_COLOR = new Color(0.11f, 0.63f, 0.89f, 1);
	private static final Color INNER_MODE_FILL_COLOR = new Color(0.05f, 0.33f, 0.49f, 0.3f);
	private static final Color OVERLAP_MODE_BORDER_COLOR = new Color(0.11f, 0.88f, 0.2f, 1);
	private static final Color OVERLAP_MODE_FILL_COLOR = new Color(0.13f, 0.49f, 0.05f, 0.3f);

	private EditorScene scene;
	private EntityManipulatorModule entityManipulatorModule;

	private EntityProxyCache proxyCache;
	private Rectangle currentRect = null;
	private Rectangle rectToDraw = null;
	private float touchDownPositionX;
	private SelectionMode selectionMode = SelectionMode.Inner;

	public RectangularSelection (EditorScene scene, EntityManipulatorModule entityManipulatorModule, EntityProxyCache proxyCache) {
		this.scene = scene;
		this.entityManipulatorModule = entityManipulatorModule;
		this.proxyCache = proxyCache;
	}

	public void render (ShapeRenderer shapeRenderer) {
		if (rectToDraw != null) {
			Gdx.gl20.glEnable(GL20.GL_BLEND);

			Color border = selectionMode == SelectionMode.Inner ? INNER_MODE_BORDER_COLOR : OVERLAP_MODE_BORDER_COLOR;
			Color fill = selectionMode == SelectionMode.Inner ? INNER_MODE_FILL_COLOR : OVERLAP_MODE_FILL_COLOR;

			shapeRenderer.setColor(border);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();

			shapeRenderer.setColor(fill);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.rect(rectToDraw.getX(), rectToDraw.getY(), rectToDraw.getWidth(), rectToDraw.getHeight());
			shapeRenderer.end();
		}
	}

	public void findContainedComponents () {
		Array<EntityProxy> matchingEntities = new Array<>();

		if (selectionMode == SelectionMode.Inner) {
			for (EntityProxy entity : proxyCache.getCache().values()) {
				if (rectToDraw.contains(entity.getBoundingRectangle()) && entity.getLayerID() == scene.getActiveLayerId())
					matchingEntities.add(entity);
			}
		} else {
			for (EntityProxy entity : proxyCache.getCache().values()) {
				if (rectToDraw.overlaps(entity.getBoundingRectangle()) && entity.getLayerID() == scene.getActiveLayerId())
					matchingEntities.add(entity);
			}
		}

		entityManipulatorModule.softSelectionReset();
		matchingEntities.forEach(entityManipulatorModule::fastSelectAppend);
		entityManipulatorModule.selectedEntitiesChanged();
	}

	public boolean touchDown (float x, float y, int button) {
		if (button == Buttons.LEFT) {
			touchDownPositionX = x;
			currentRect = new Rectangle(x, y, 0, 0);
			updateDrawableRect();
			return true;
		}

		return false;
	}

	public boolean touchDragged (float x, float y) {
		if (currentRect != null) {

			// check gesture direction at X axis to change selection mode
			if (x < touchDownPositionX) {
				selectionMode = SelectionMode.Overlap;
			} else {
				selectionMode = SelectionMode.Inner;
			}

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
			x = x - width;
		}

		if (height < 0) {
			height = 0 - height;
			y = y - height;
		}

		// Update rectToDraw after saving old value.
		if (rectToDraw != null)
			rectToDraw.set(x, y, width, height);
		else
			rectToDraw = new Rectangle(x, y, width, height);

	}

	private enum SelectionMode {
		/** Selects only inner objects */
		Inner,
		/** Selects inner and overlapping objects */
		Overlap
	}
}
