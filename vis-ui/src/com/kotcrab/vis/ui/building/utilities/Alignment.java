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

package com.kotcrab.vis.ui.building.utilities;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;

/**
 * LibGDX alignments are simple integers and it's rather easy to make a mistake while using the align methods.
 * This enums wraps all default alignments, allowing to validate if the alignment value is actually correct.
 * @author MJ
 */
public enum Alignment {
	CENTER(Align.center),
	TOP(Align.top),
	BOTTOM(Align.bottom),
	LEFT(Align.left),
	RIGHT(Align.right),
	TOP_LEFT(Align.topLeft),
	TOP_RIGHT(Align.topRight),
	BOTTOM_LEFT(Align.bottomLeft),
	BOTTOM_RIGHT(Align.bottomRight);

	private final int alignment;

	private Alignment (final int alignment) {
		this.alignment = alignment;
	}

	public int getAlignment () {
		return alignment;
	}

	public void apply (final Cell<?> cell) {
		cell.align(alignment);
	}

	/** @return true for TOP, TOP_LEFT and TOP_RIGHT. */
	public boolean isAlignedWithTop () {
		return (alignment & Align.top) != 0;
	}

	/** @return true for BOTTOM, BOTTOM_LEFT and BOTTOM_RIGHT. */
	public boolean isAlignedWithBottom () {
		return (alignment & Align.bottom) != 0;
	}

	/** @return true for LEFT, BOTTOM_LEFT and TOP_LEFT. */
	public boolean isAlignedWithLeft () {
		return (alignment & Align.left) != 0;
	}

	/** @return true for RIGHT, BOTTOM_RIGHT and TOP_RIGHT. */
	public boolean isAlignedWithRight () {
		return (alignment & Align.right) != 0;
	}

	/** @return true for CENTER. */
	public boolean isCentered () {
		return alignment == Align.center;
	}

	/**
	 * @param index ordinal of an enum constant.
	 * @return optional value of enum constant. Will be null for invalid index.
	 */
	public static Alignment getByIndex (final int index) {
		return isIndexValid(index) ? values()[index] : null;
	}

	/**
	 * @param index a valid ordinal of an enum constant.
	 * @return enum constant with the selected index.
	 * @throws ArrayIndexOutOfBoundsException for invalid index.
	 */
	public static Alignment getByValidIndex (final int index) {
		return values()[index];
	}

	/** @return true if the index is connected with an enum constant. */
	public static boolean isIndexValid (final int index) {
		return index >= 0 && index < values().length;
	}

	/** @return true if the index is connected with the last enum constant. */
	public static boolean isIndexLast (final int index) {
		return index == values().length - 1;
	}
}
