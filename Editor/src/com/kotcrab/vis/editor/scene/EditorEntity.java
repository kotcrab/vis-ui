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
	abstract float getX ();

	abstract void setX (float x);

	abstract float getY ();

	abstract void setY (float y);

	abstract void setPosition (float x, float y);

	boolean isResizeSupported ();

	abstract float getWidth ();

	abstract float getHeight ();

	void render (Batch batch);

	void setSize (float width, float height);

	boolean isOriginSupported ();

	float getOriginX ();

	float getOriginY ();

	void setOrigin (float x, float y);

	boolean isScaleSupported ();

	float getScaleX ();

	float getScaleY ();

	void setScale (float x, float y);

	boolean isTintSupported ();

	Color getColor ();

	void setColor (Color color);

	boolean isRotationSupported ();

	float getRotation ();

	void setRotation (float rotation);

	boolean isFlipSupported ();

	boolean isFlipX ();

	boolean isFlipY ();

	void setFlip (boolean x, boolean y);

	abstract Rectangle getBoundingRectangle ();

	String getId ();

	void setId (String id);

	default void afterDeserialize () {

	}

	default void beforeSerialize () {

	}
}
