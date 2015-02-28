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

public interface EditorEntity {
	public abstract float getX ();

	public abstract void setX (float x);

	public abstract float getY ();

	public abstract void setY (float y);

	public abstract void setPosition (float x, float y);

	public boolean isResizeSupported ();

	public abstract float getWidth ();

	public abstract float getHeight ();

	public void render (Batch batch);

	public void setSize (float width, float height);

	public boolean isOriginSupported ();

	public float getOriginX ();

	public float getOriginY ();

	public void setOrigin (float x, float y);

	public boolean isScaleSupported ();

	public float getScaleX ();

	public float getScaleY ();

	public void setScale (float x, float y);

	public boolean isTintSupported ();

	public Color getColor ();

	public void setColor (Color color);

	public boolean isRotationSupported ();

	public float getRotation ();

	public void setRotation (float rotation);

	public boolean isFlipSupported ();

	public boolean isFlipX ();

	public boolean isFlipY ();

	public void setFlip (boolean x, boolean y);

	public abstract Rectangle getBoundingRectangle ();

	public String getId ();

	public void setId (String id);
}
