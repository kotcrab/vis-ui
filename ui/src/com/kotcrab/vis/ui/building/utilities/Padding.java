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
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * A simple helper class that holds informations about padding on each side of an object. Static methods can
 * be used to quickly set padding or spacing of a cell or a table using this class' object.
 * <p>
 * When padding is set for a table, its cells will be separated from its borders by the given value. When
 * padding is set for a window, additionally to the table's padding effect, top padding will be a draggable
 * area, allowing to move the window.
 * <p>
 * When padding is set for a cell, it will be separated from other cells and table's border by the given
 * value. When spacing is set for a cell, it will be separated by at least as many pixels from other cells as
 * specified. If spacing or padding with the same values is used on every cell in a table, padding will
 * provide twice as big distances between cells, since spacings can overlap.
 * @author MJ
 */
public class Padding {
	/** Common padding sizes. */
	public static final Padding PAD_0 = of(0f), PAD_2 = of(2f), PAD_4 = of(4f), PAD_8 = of(8f);

	private final float top, left, bottom, right;

	/** @param padding will be set as padding for all directions. */
	public Padding (final float padding) {
		this(padding, padding, padding, padding);
	}

	/**
	 * @param horizontal will be set as left and right padding.
	 * @param vertical will be set as top and bottom padding.
	 */
	public Padding (final float horizontal, final float vertical) {
		this(vertical, horizontal, vertical, horizontal);
	}

	/**
	 * @param top top padding value.
	 * @param left left padding value.
	 * @param bottom bottom padding value.
	 * @param right right padding value.
	 */
	public Padding (final float top, final float left, final float bottom, final float right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/** @param padding will be set as padding for all directions. */
	public static Padding of (final float padding) {
		return new Padding(padding, padding, padding, padding);
	}

	/**
	 * @param horizontal will be set as left and right padding.
	 * @param vertical will be set as top and bottom padding.
	 */
	public static Padding of (final float horizontal, final float vertical) {
		return new Padding(vertical, horizontal, vertical, horizontal);
	}

	/**
	 * @param top top padding value.
	 * @param left left padding value.
	 * @param bottom bottom padding value.
	 * @param right right padding value.
	 */
	public static Padding of (final float top, final float left, final float bottom, final float right) {
		return new Padding(top, left, bottom, right);
	}

	/** @return top padding value. */
	public float getTop () {
		return top;
	}

	/** @return left padding value. */
	public float getLeft () {
		return left;
	}

	/** @return bottom padding value. */
	public float getBottom () {
		return bottom;
	}

	/** @return right padding value. */
	public float getRight () {
		return right;
	}

	/**
	 * @param padding will be added to the given padding.
	 * @return new Padding object with summed pad values.
	 */
	public Padding add (final Padding padding) {
		return new Padding(top + padding.getTop(), left + padding.getLeft(), bottom + padding.getBottom(),
				right + padding.getRight());
	}

	/**
	 * @param padding will be subtracted from the given padding.
	 * @return new Padding object with subtracted pad values.
	 */
	public Padding subtract (final Padding padding) {
		return new Padding(top - padding.getTop(), left - padding.getLeft(), bottom - padding.getBottom(),
				right - padding.getRight());
	}

	/** @return new Padding object with reversed pad values. */
	public Padding reverse () {
		return new Padding(-top, -left, -bottom, -right);
	}

	/**
	 * Allows to set Table's padding with the Padding object, which has be done externally, as it's not part
	 * of the standard libGDX API.
	 * @param table will have the padding set according to the this object's data.
	 * @return the given table for chaining.
	 */
	public Table applyPadding (final Table table) {
		table.pad(top, left, bottom, right);
		return table;
	}

	/**
	 * Allows to set Cell's padding with the Padding object, which has be done externally, as it's not part of
	 * the standard libGDX API.
	 * @param cell will have the padding set according to the this object's data.
	 * @return the given cell for chaining.
	 */
	public Cell<?> applyPadding (final Cell<?> cell) {
		cell.pad(top, left, bottom, right);
		return cell;
	}

	/**
	 * Allows to set Cell's spacing with the Padding object, which has be done externally, as it's not part of
	 * the standard libGDX API. Padding holds 4 floats for each direction, so it's compatible with both
	 * padding and spacing settings without any additional changes.
	 * @param cell will have the padding set according to the this object's data.
	 * @return the given cell for chaining.
	 */
	public Cell<?> applySpacing (final Cell<?> cell) {
		cell.space(top, left, bottom, right);
		return cell;
	}

	// Padding utilities:

	/**
	 * Allows to set Table's padding with the Padding object, which has be done externally, as it's not part
	 * of the standard libGDX API.
	 * @param padding contains data of padding sizes.
	 * @param table will have the padding set according to the given data.
	 * @return the given table for chaining.
	 */
	public static Table setPadding (final Padding padding, final Table table) {
		table.pad(padding.getTop(), padding.getLeft(), padding.getBottom(), padding.getRight());
		return table;
	}

	/**
	 * Allows to set Cell's padding with the Padding object, which has be done externally, as it's not part of
	 * the standard libGDX API.
	 * @param padding contains data of padding sizes.
	 * @param cell will have the padding set according to the given data.
	 * @return the given cell for chaining.
	 */
	public static Cell<?> setPadding (final Padding padding, final Cell<?> cell) {
		return cell.pad(padding.getTop(), padding.getLeft(), padding.getBottom(), padding.getRight());
	}

	/**
	 * Allows to set Cell's spacing with the Padding object, which has be done externally, as it's not part of
	 * the standard libGDX API. Padding holds 4 floats for each direction, so it's compatible with both
	 * padding and spacing settings without any additional changes.
	 * @param spacing contains data of spacing sizes.
	 * @param cell will have the padding set according to the given data.
	 * @return the given cell for chaining.
	 */
	public static Cell<?> setSpacing (final Padding spacing, final Cell<?> cell) {
		return cell.space(spacing.getTop(), spacing.getLeft(), spacing.getBottom(), spacing.getRight());
	}

}
