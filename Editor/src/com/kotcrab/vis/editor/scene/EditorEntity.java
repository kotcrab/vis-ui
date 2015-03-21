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

	String getId ();

	void setId (String id);

	float getX ();

	void setX (float x);

	float getY ();

	void setY (float y);

	void setPosition (float x, float y);

	float getWidth ();

	float getHeight ();

	Rectangle getBoundingRectangle ();

	void render (Batch batch);

	default boolean isResizeSupported () {
		return false;
	}

	default void setSize (float width, float height) {

	}

	default boolean isOriginSupported () {
		return false;
	}

	default float getOriginX () {
		return 0;
	}

	default float getOriginY () {
		return 0;
	}

	default void setOrigin (float x, float y) {

	}

	default boolean isScaleSupported () {
		return false;
	}

	default float getScaleX () {
		return 0;
	}

	default float getScaleY () {
		return 0;
	}

	default void setScale (float x, float y) {

	}

	default boolean isTintSupported () {
		return false;
	}

	default Color getColor () {
		return null;
	}

	default void setColor (Color color) {

	}

	default boolean isRotationSupported () {
		return false;
	}

	default float getRotation () {
		return 0;
	}

	default void setRotation (float rotation) {

	}

	default boolean isFlipSupported () {
		return false;
	}

	default boolean isFlipX () {
		return false;
	}

	default boolean isFlipY () {
		return false;
	}

	default void setFlip (boolean x, boolean y) {

	}

	default void afterDeserialize () {

	}

	default void beforeSerialize () {

	}

	default String toPrettyString () {
		if (getId() == null)
			return getClass().getSimpleName() + " X: " + (int)getX() + " Y: " + (int)getY();
		else
			return getClass().getSimpleName() + " ID: " + getId() + " X: " + (int)getX() + " Y: " + (int)getY();
	}
}
