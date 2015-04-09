/*
 * Copyright 2014-2015 See AUTHORS file.
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

package com.kotcrab.vis.editor.module.physicseditor.models;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ShapeModel {
	public enum Type {POLYGON, CIRCLE}

	private final Array<Vector2> vertices = new Array<>();
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
