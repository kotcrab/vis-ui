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

package com.kotcrab.vis.editor.module.physicseditor.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ShapeModel {
	public enum Type {POLYGON, CIRCLE}

	private final Array<Vector2> vertices = new Array<Vector2>();
	private final Type type;
	private boolean isClosed = false;

	public ShapeModel (Type type) {
		this.type = type;
	}

	public Array<Vector2> getVertices () {
		return vertices;
	}

	public Type getType () {
		return type;
	}

	public void close () {
		isClosed = true;
	}

	public boolean isClosed () {
		return isClosed;
	}
}
