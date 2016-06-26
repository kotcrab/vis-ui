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

package com.kotcrab.vis.ui.building;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.ui.building.utilities.Padding;

/**
 * Builds a table with the appended widgets, trying to keep them centered. Expands X axis for first and last
 * widget in each row and overrides their alignments to right and left, keeping the widgets centered. Each
 * table's row will have the same colspan. While useful, StandardTableBuilder might be more appropriate for
 * complex tables.
 * @author MJ
 */
public class CenteredTableBuilder extends TableBuilder {
	public CenteredTableBuilder () {
		super();
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public CenteredTableBuilder (final Padding defaultWidgetPadding) {
		super(defaultWidgetPadding);
	}

	public CenteredTableBuilder (final int estimatedWidgetsAmount, final int estimatedRowsAmount) {
		super(estimatedWidgetsAmount, estimatedRowsAmount);
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public CenteredTableBuilder (final int estimatedWidgetsAmount, final int estimatedRowsAmount,
								 final Padding defaultWidgetPadding) {
		super(estimatedWidgetsAmount, estimatedRowsAmount, defaultWidgetPadding);
	}

	@Override
	protected void fillTable (final Table table) {
		final IntArray rowSizes = getRowSizes();
		final int widgetsInRow = getLowestCommonMultiple(rowSizes);

		for (int rowIndex = 0, widgetIndex = 0; rowIndex < rowSizes.size; rowIndex++) {
			final int rowSize = rowSizes.get(rowIndex);
			final int currentWidgetColspan = widgetsInRow / rowSize;
			boolean isFirst = shouldExpand(rowSize);

			for (final int totalWidgetsBeforeRowEnd = widgetIndex + rowSize; widgetIndex < totalWidgetsBeforeRowEnd; widgetIndex++) {
				final Cell<?> cell =
						getWidget(widgetIndex).buildCell(table, getDefaultWidgetPadding()).colspan(
								currentWidgetColspan);
				// Keeping widgets together - expanding X for first and last widget, setting alignments:
				if (isFirst) {
					isFirst = false;
					cell.expandX().right();
				} else if (isLast(widgetIndex, rowSize, totalWidgetsBeforeRowEnd)) {
					cell.expandX().left();
				}
			}
			table.row();
		}
	}

	/**
	 * When table is trying to keep widgets together and widget is not alone in the row (in which case it
	 * should be centered instead), it has to expand on X and be aligned right.
	 * @param rowSize current row size.
	 * @return true if row size is bigger than 1.
	 */
	private boolean shouldExpand (final int rowSize) {
		return rowSize != 1;
	}

	/**
	 * @return true if the widget is last. It is used to determine if the widget has to be left-aligned and
	 * expand on X axis.
	 */
	private boolean isLast (final int widgetIndex, final int rowSize, final int totalWidgetsInRow) {
		return shouldExpand(rowSize) && widgetIndex == totalWidgetsInRow - 1;
	}
}
