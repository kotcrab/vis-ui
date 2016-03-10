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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.CellWidget.CellWidgetBuilder;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.building.utilities.layouts.ActorLayout;
import com.kotcrab.vis.ui.building.utilities.layouts.TableLayout;

/**
 * Allows to easily build Scene2D tables, without having to worry about different colspans of table's rows.
 * Table built using this helper class will have the same amount of cells in each row. CellWidget class allows
 * to store cell's settings, and thanks to that - even the most complex tables can be built using one of the
 * TableBuilders.
 * @author MJ
 */
public abstract class TableBuilder {
	private final static int DEFAULT_WIDGETS_AMOUNT = 10, DEFAULT_ROWS_AMOUNT = 3;

	private final Array<CellWidget<? extends Actor>> widgets;
	private final IntArray rowSizes;
	// Control variables.
	private int currentRowSize;
	// Settings.
	private final Padding widgetPadding;
	private Padding tablePadding;

	public TableBuilder () {
		this(DEFAULT_WIDGETS_AMOUNT, DEFAULT_ROWS_AMOUNT, Padding.PAD_0);
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public TableBuilder (final Padding defaultWidgetPadding) {
		this(DEFAULT_WIDGETS_AMOUNT, DEFAULT_ROWS_AMOUNT, defaultWidgetPadding);
	}

	public TableBuilder (final int estimatedWidgetsAmount, final int estimatedRowsAmount) {
		this(estimatedWidgetsAmount, estimatedRowsAmount, Padding.PAD_0);
	}

	/** @param defaultWidgetPadding will be applied to all added widgets if no specific padding is given. */
	public TableBuilder (final int estimatedWidgetsAmount, final int estimatedRowsAmount,
						 final Padding defaultWidgetPadding) {
		widgets = new Array<CellWidget<? extends Actor>>(estimatedWidgetsAmount);
		rowSizes = new IntArray(estimatedRowsAmount);
		widgetPadding = defaultWidgetPadding;
	}

	/** @return the greatest common denominator of two values. */
	public static int getGreatestCommonDenominator (final int valueA, final int valueB) {
		return valueB == 0 ? valueA : getGreatestCommonDenominator(valueB, valueA % valueB);
	}

	/** @return lowest common multiple for the given two values. */
	public static int getLowestCommonMultiple (final int valueA, final int valueB) {
		return valueA * (valueB / getGreatestCommonDenominator(valueA, valueB));
	}

	/**
	 * @param values cannot be empty or null.
	 * @return lowest common multiple for the given values.
	 */
	public static int getLowestCommonMultiple (final IntArray values) {
		int lowestCommonMultiple = values.first();
		for (int index = 1; index < values.size; index++) {
			lowestCommonMultiple = getLowestCommonMultiple(lowestCommonMultiple, values.get(index));
		}
		return lowestCommonMultiple;
	}

	/**
	 * @param tablePadding will define the amount of pixels separating widgets from the table's borders. Can be
	 * null - nulled padding will be ignored.
	 */
	public TableBuilder setTablePadding (final Padding tablePadding) {
		this.tablePadding = tablePadding;
		return this;
	}

	/**
	 * @return default widgets' padding. Should be applied to cells that have not specified custom padding
	 * setting.
	 */
	protected Padding getDefaultWidgetPadding () {
		return widgetPadding;
	}

	/** @param widget will be added to the table with current default table's padding. */
	public TableBuilder append (final Actor widget) {
		return append(CellWidget.of(widget).padding(widgetPadding).wrap());
	}

	/** @param widget will be added to the table with custom provided data. */
	public TableBuilder append (final CellWidget<? extends Actor> widget) {
		widgets.add(widget);
		currentRowSize++;

		return this;
	}

	/** @param widgets will be converted into one cell, with widgets appended into one row. */
	public TableBuilder append (final Actor... widgets) {
		return append(TableLayout.HORIZONTAL, widgets);
	}

	/**
	 * @param widgets will be converted into one cell, with widgets appended into one row. Note that these
	 * CellWidgets' settings are local to the merging widget and additional data might have to be
	 * passed. See methods that consume CellWidgetBuilder.
	 */
	public TableBuilder append (final CellWidget<?>... widgets) {
		return append(TableLayout.HORIZONTAL, widgets);
	}

	/**
	 * @param layout will determine how widgets are converted into one cell. See TableLayout for default
	 * implementations.
	 * @param widgets will be converted into one cell using passed layout.
	 */
	public TableBuilder append (final ActorLayout layout, final Actor... widgets) {
		return append(layout.convertToActor(widgets));
	}

	/**
	 * @param layout will determine how widgets are converted into one cell. See TableLayout for default
	 * implementations.
	 * @param widgets will be converted into one cell using passed layout. Note that some (or all) CellWidget
	 * settings might be ignored, depending on the implementation of ActorLayout. Default layouts
	 * use TableBuilders, so they do not ignore (most of) passed data. Also, these CellWidgets'
	 * settings are local to the merging widget and additional data might have to be passed. See
	 * methods that consume CellWidgetBuilder.
	 */
	public TableBuilder append (final ActorLayout layout, final CellWidget<?>... widgets) {
		return append(layout.convertToActor(widgets));
	}

	/**
	 * @param mergedCellSettings its data will be applied to the cell that will contain passed widgets merged
	 * into one actor.
	 * @param widgets will be converted into one cell, with widgets appended into one row.
	 */
	public TableBuilder append (final CellWidgetBuilder<Actor> mergedCellSettings, final Actor... widgets) {
		return append(TableLayout.HORIZONTAL, mergedCellSettings, widgets);
	}

	/**
	 * @param mergedCellSettings its data will be applied to the cell that will contain passed widgets merged
	 * into one actor.
	 * @param widgets will be converted into one cell, with widgets appended into one row.
	 */
	public TableBuilder append (final CellWidgetBuilder<Actor> mergedCellSettings,
								final CellWidget<?>... widgets) {
		return append(TableLayout.HORIZONTAL, mergedCellSettings, widgets);
	}

	/**
	 * @param layout will determine how widgets are converted into one cell. See TableLayout for default
	 * implementations.
	 * @param mergedCellSettings its data will be applied to the cell that will contain passed widgets merged
	 * into one actor.
	 * @param widgets will be converted into one cell using passed layout.
	 */
	public TableBuilder append (final ActorLayout layout, final CellWidgetBuilder<Actor> mergedCellSettings,
								final Actor... widgets) {
		return append(mergedCellSettings.widget(layout.convertToActor(widgets)).wrap());
	}

	/**
	 * @param layout will determine how widgets are converted into one cell. See TableLayout for default
	 * implementations.
	 * @param mergedCellSettings its data will be applied to the cell that will contain passed widgets merged
	 * into one actor.
	 * @param widgets will be converted into one cell using passed layout. Note that some (or all) CellWidget
	 * settings might be ignored, depending on the implementation of ActorLayout. Default layouts
	 * use TableBuilders, so they do not ignore (most of) passed data.
	 */
	public TableBuilder append (final ActorLayout layout, final CellWidgetBuilder<Actor> mergedCellSettings,
								final CellWidget<?>... widgets) {
		return append(mergedCellSettings.widget(layout.convertToActor(widgets)).wrap());
	}

	/**
	 * Appends an empty cell to the table. Equivalent to passing null to append methods or appending
	 * CellWidget.EMPTY/CellWidget.empty().
	 */
	public TableBuilder append () {
		return append(CellWidget.EMPTY);
	}

	/**
	 * Changes the current row, starts another. If no widgets were appended since the last call, row() will be
	 * ignored.
	 */
	public TableBuilder row () {
		if (currentRowSize != 0) {
			rowSizes.add(currentRowSize);
			currentRowSize = 0;
		}
		return this;
	}

	/** @return a new table with the appended widgets, with widgets added depending on the chosen builder type. */
	public Table build () {
		return build(new Table());
	}

	/**
	 * @return passed table with the appended widgets, with widgets added depending on the chosen builder type.
	 * Note that if the passed table is not empty, builder implementations do not have to ensure that
	 * the widgets are actually correctly appended.
	 */
	public <T extends Table> T build (final T table) {
		prepareNewTable(table);
		if (widgets.size == 0) {
			// Table is empty; avoiding unnecessary operations.
			return table;
		} else {
			fillTable(table);
			return prepareBuiltTable(table);
		}
	}

	private Table prepareNewTable (final Table table) {
		validateRowSize();
		if (tablePadding != null) {
			return tablePadding.applyPadding(table);
		}
		return table;
	}

	/**
	 * Should fill the given table with the widgets appended to the builder. Widgets can be accessed with
	 * getWidget(index) and getWidgets() methods. Row sizes are already validated. There is at least one
	 * widget.
	 * @param table is a properly created Scene2D table. Will be packed and return after filling.
	 */
	protected abstract void fillTable (Table table);

	private <T extends Table> T prepareBuiltTable (final T table) {
		table.pack();
		return table;
	}

	/** Will append a new row if any new widgets were passed to make sure that all widgets are honored. */
	private void validateRowSize () {
		if (currentRowSize != 0) {
			row();
		}
	}

	/** @return array with sizes of each row. */
	protected IntArray getRowSizes () {
		return rowSizes;
	}

	/** @return CellWidget with the given index in the widgets array. */
	protected CellWidget<? extends Actor> getWidget (final int index) {
		return widgets.get(index);
	}

	/** @return all CellWidgets appended to the builder. */
	protected Array<CellWidget<? extends Actor>> getWidgets () {
		return widgets;
	}
}
