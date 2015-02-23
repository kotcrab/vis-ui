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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public abstract class EditorEntity {
	public String id;

	public abstract float getX ();

	public abstract void setX (float x);

	public abstract float getY ();

	public abstract void setY (float y);

	public abstract void setPosition(float x, float y);

	public abstract float getWidth ();

	public abstract void setWidth (float width);

	public abstract float getHeight ();

	public abstract void setSize(float width, float height);

	public abstract void setHeight (float height);

	public abstract float getOriginX ();

	public abstract void setOriginX (float x);

	public abstract float getOriginY ();

	public abstract void setOriginY (float y);

	public abstract void setOrigin(float x, float y);

	public abstract float getScaleX ();

	public abstract void setScaleX (float x);

	public abstract float getScaleY ();

	public abstract void setScaleY (float y);

	public abstract void setScale(float x, float y);

	public abstract Color getColor ();

	public abstract void setColor (Color color);

	public abstract float getRotation ();

	public abstract void setRotation (float rotation);

	public abstract boolean isFlipX ();

	public abstract void setFlipX (boolean x);

	public abstract boolean isFlipY ();

	public abstract void setFlipY (boolean y);

	public abstract void setFlip(boolean x, boolean y);

	public abstract Rectangle getBoundingRectangle ();
}
