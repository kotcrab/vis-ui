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
 * Represents a file in a Spriter SCML file.
 * A file has an {@link #id}, a {@link #name}.
 * A {@link #size} and a {@link #pivot} point, i.e. origin of an image do not have to be set since a file can be a sound file.
 * @author Trixt0r
 */
public class File {

	public final int id;
	public final String name;
	public final Dimension size;
	public final Point pivot;

	File (int id, String name, Dimension size, Point pivot) {
		this.id = id;
		this.name = name;
		this.size = size;
		this.pivot = pivot;
	}

	/**
	 * Returns whether this file is a sprite, i.e. an image which is going to be animated, or not.
	 * @return whether this file is a sprite or not.
	 */
	public boolean isSprite () {
		return pivot != null && size != null;
	}

	public String toString () {
		return getClass().getSimpleName() + "|[id: " + id + ", name: " + name + ", size: " + size + ", pivot: " + pivot;
	}

}
