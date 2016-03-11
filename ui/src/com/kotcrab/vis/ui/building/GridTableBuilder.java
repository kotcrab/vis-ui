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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;

/**
 * Ignores row() calls and builds table with all widgets put into rows of given size. Note that this builder
 * will not center or in any way try to "repair" the last row if too few widgets are given to create a true
 * grid.
 * @author MJ
 */
public class GridTableBuilder extends TableBuilder {
	private final int rowSize;

	public GridTableBuilder (final int rowSize) {
		super();
		this.rowSize = rowSize;
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public GridTableBuilder (final Padding defaultWidgetPadding, final int rowSize) {
		super(defaultWidgetPadding);
		this.rowSize = rowSize;
	}

	public GridTableBuilder (final int rowSize, final int estimatedWidgetsAmount, final int estimatedRowsAmount) {
		super(estimatedWidgetsAmount, estimatedRowsAmount);
		this.rowSize = rowSize;
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public GridTableBuilder (final int rowSize, final int estimatedWidgetsAmount,
							 final int estimatedRowsAmount, final Padding defaultWidgetPadding) {
		super(estimatedWidgetsAmount, estimatedRowsAmount, defaultWidgetPadding);
		this.rowSize = rowSize;
	}

	@Override
	protected void fillTable (final Table table) {
		int widgetsCounter = 0;
		for (final CellWidget<? extends Actor> widget : getWidgets()) {
			widget.buildCell(table, getDefaultWidgetPadding());
			if (++widgetsCounter == rowSize) {
				widgetsCounter -= rowSize;
				table.row();
			}
		}
	}
}
