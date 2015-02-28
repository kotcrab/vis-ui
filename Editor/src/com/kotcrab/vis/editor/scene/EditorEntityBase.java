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

package com.kotcrab.vis.editor.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EditorEntityBase implements EditorEntity {
	private String id;

	@Override
	public abstract float getX ();

	@Override
	public abstract void setX (float x);

	@Override
	public abstract float getY ();

	@Override
	public abstract void setY (float y);

	@Override
	public abstract void setPosition (float x, float y);

	@Override
	public boolean isResizeSupported () {
		return false;
	}

	@Override
	public abstract float getWidth ();

	@Override
	public abstract float getHeight ();

	@Override
	public void render (Batch batch) {

	}

	@Override
	public void setSize (float width, float height) {
	}

	@Override
	public boolean isOriginSupported () {
		return false;
	}

	@Override
	public float getOriginX () {
		return 0;
	}

	@Override
	public float getOriginY () {
		return 0;
	}

	@Override
	public void setOrigin (float x, float y) {
	}

	@Override
	public boolean isScaleSupported () {
		return false;
	}

	@Override
	public float getScaleX () {
		return 1;
	}

	@Override
	public float getScaleY () {
		return 1;
	}

	@Override
	public void setScale (float x, float y) {
	}

	@Override
	public boolean isTintSupported () {
		return false;
	}

	@Override
	public Color getColor () {
		return null;
	}

	@Override
	public void setColor (Color color) {
	}

	@Override
	public boolean isRotationSupported () {
		return false;
	}

	@Override
	public float getRotation () {
		return 0;
	}

	@Override
	public void setRotation (float rotation) {
	}

	@Override
	public boolean isFlipSupported () {
		return false;
	}

	@Override
	public boolean isFlipX () {
		return false;
	}

	@Override
	public boolean isFlipY () {
		return false;
	}

	@Override
	public void setFlip (boolean x, boolean y) {
	}

	@Override
	public abstract Rectangle getBoundingRectangle ();

	@Override
	public String getId () {
		return id;
	}

	@Override
	public void setId (String id) {
		this.id = id;
	}
}
