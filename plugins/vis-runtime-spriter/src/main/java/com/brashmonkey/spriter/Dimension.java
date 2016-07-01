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

package com.brashmonkey.spriter;

/**
 * Represents a dimension in a 2D space.
 * A dimension has a width and a height.
 * @author Trixt0r
 */
public class Dimension {

	public float width, height;

	/**
	 * Creates a new dimension with the given size.
	 * @param width the width of the dimension
	 * @param height the height of the dimension
	 */
	public Dimension (float width, float height) {
		this.set(width, height);
	}

	/**
	 * Creates a new dimension with the given size.
	 * @param size the size
	 */
	public Dimension (Dimension size) {
		this.set(size);
	}

	/**
	 * Sets the size of this dimension to the given size.
	 * @param width the width of the dimension
	 * @param height the height of the dimension
	 */
	public void set (float width, float height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets the size of this dimension to the given size.
	 * @param size the size
	 */
	public void set (Dimension size) {
		this.set(size.width, size.height);
	}

	public String toString () {
		return "[" + width + "x" + height + "]";
	}

}
