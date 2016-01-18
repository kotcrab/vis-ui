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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Wraps a Scene2D widget, allowing to store cell data for delayed Table creation. Note that some filling data
 * (like expanding on X axis or alignment) might be overridden by some TableBuilders (like the
 * CenteredTableBuilder, which tries to keep all widgets centered by setting expansion and alignment of some
 * cells).
 * @author MJ
 */
public class CellWidget<Widget extends Actor> {
	private final static int IGNORED_SIZE = 0;
	/** Contains nulled actor. */
	public final static CellWidget<?> EMPTY = empty();

	private final Widget widget;
	private final Padding padding;
	private final boolean expandX, expandY, fillX, fillY, useSpacing;
	private final Alignment alignment;
	private final int width, height, minWidth, minHeight;

	// Cast is safe - builder will have the same type as the cell widget.
	@SuppressWarnings("unchecked")
	private CellWidget (final CellWidgetBuilder<Widget> cellWidgetBuilder) {
		widget = (Widget) cellWidgetBuilder.widget;
		padding = cellWidgetBuilder.padding;
		expandX = cellWidgetBuilder.expandX;
		expandY = cellWidgetBuilder.expandY;
		fillX = cellWidgetBuilder.fillX;
		fillY = cellWidgetBuilder.fillY;
		useSpacing = cellWidgetBuilder.useSpacing;
		alignment = cellWidgetBuilder.alignment;
		width = cellWidgetBuilder.width;
		height = cellWidgetBuilder.height;
		minWidth = cellWidgetBuilder.minWidth;
		minHeight = cellWidgetBuilder.minHeight;
	}

	/**
	 * @param widget will be wrapped with CellWidget.
	 * @return a new CellWidgetBuilder, allowing to specify the cell's settings.
	 */
	public static <Widget extends Actor> CellWidgetBuilder<Widget> of (final Widget widget) {
		return new CellWidgetBuilder<Widget>(widget);
	}

	/**
	 * @param widget will be used to set initial data of builder, allowing to "modify" a prepared CellWidget.
	 * @return a new CellWidgetBuilder, allowing to respecify the cell's settings.
	 */
	public static <Widget extends Actor> CellWidgetBuilder<Widget> using (final CellWidget<Widget> widget) {
		return new CellWidgetBuilder<Widget>(widget);
	}

	/**
	 * @param widget will be immediately wrapped into a CellWidget with no specific settings.
	 * @return wrapped widget.
	 */
	public static <Widget extends Actor> CellWidget<Widget> wrap (final Widget widget) {
		return of(widget).wrap();
	}

	/**
	 * @param widgets will be converted to CellWidgets without any specific settings.
	 * @return wrapped widgets.
	 */
	public static CellWidget<?>[] wrap (final Actor... widgets) {
		final CellWidget<?>[] wrappedWidgets = new CellWidget<?>[widgets.length];
		for (int index = 0; index < widgets.length; index++) {
			wrappedWidgets[index] = CellWidget.of(widgets[index]).wrap();
		}
		return wrappedWidgets;
	}

	/** @return a new empty, non-null CellWidget with no actor. */
	public static CellWidget<?> empty () {
		return builder().wrap();
	}

	/** @return an empty builder with no widget that can be used as data container. */
	public static CellWidgetBuilder<Actor> builder () {
		return of(null);
	}

	/** @return widget wrapped with the CellWidget object. */
	public Widget getWidget () {
		return widget;
	}

	/**
	 * @param table will contain a cell with the object's widget with specified cell settings.
	 * @return a reference to the built cell.
	 */
	public Cell<?> buildCell (final Table table) {
		return buildCell(table, null);
	}

	/**
	 * @param table will contain a cell with the object's widget with specified cell settings.
	 * @param defaultWidgetPadding will be applied to the cell if padding was not specified. Can be null.
	 * @return a reference to the built cell.
	 */
	public Cell<?> buildCell (final Table table, final Padding defaultWidgetPadding) {
		final Cell<?> cell = table.add(widget);

		applyPadding(cell, defaultWidgetPadding);
		applySizeData(cell);
		applyFillingData(cell);

		return cell;
	}

	private void applyPadding (final Cell<?> cell, final Padding defaultWidgetPadding) {
		final Padding appliedPadding = Nullables.getOrElse(padding, defaultWidgetPadding);
		if (appliedPadding != null) {
			if (useSpacing) {
				appliedPadding.applySpacing(cell);
			} else {
				appliedPadding.applyPadding(cell);
			}
		}
	}

	private void applySizeData (final Cell<?> cell) {
		if (width > IGNORED_SIZE) {
			cell.width(width);
		}
		if (height > IGNORED_SIZE) {
			cell.height(height);
		}
		if (minWidth > IGNORED_SIZE) {
			cell.minWidth(minWidth);
		}
		if (minHeight > IGNORED_SIZE) {
			cell.minHeight(minHeight);
		}
	}

	private void applyFillingData (final Cell<?> cell) {
		if (alignment != null) {
			alignment.apply(cell);
		}
		cell.expand(expandX, expandY);
		cell.fill(fillX, fillY);
	}

	/**
	 * Allows to set the CellWidget's data. All setter methods return this for chaining.
	 * @author MJ
	 */
	public static class CellWidgetBuilder<Widget extends Actor> {
		private Actor widget;
		private Padding padding;
		private boolean expandX, expandY, fillX, fillY, useSpacing;
		private Alignment alignment;
		private int width = IGNORED_SIZE, height = IGNORED_SIZE, minWidth = IGNORED_SIZE,
				minHeight = IGNORED_SIZE;

		private CellWidgetBuilder (final Actor widget) {
			this.widget = widget;
		}

		private CellWidgetBuilder (final CellWidget<Widget> widget) {
			this.widget = widget.widget;
			padding = widget.padding;
			expandX = widget.expandX;
			expandY = widget.expandY;
			fillX = widget.fillX;
			fillY = widget.fillY;
			useSpacing = widget.useSpacing;
			alignment = widget.alignment;
			width = widget.width;
			height = widget.height;
			minWidth = widget.minWidth;
			minHeight = widget.minHeight;
		}

		/** @return widget passed to factory method wrapped with CellWidget with the applied data. */
		public CellWidget<Widget> wrap () {
			return new CellWidget<Widget>(this);
		}

		/** @param widget will replace the original widget wrapped by the builder. */
		public CellWidgetBuilder<Widget> widget (final Widget widget) {
			this.widget = widget;
			return this;
		}

		/** @param padding can be also applied as spacing after calling useSpacing(). */
		public CellWidgetBuilder<Widget> padding (final Padding padding) {
			this.padding = padding;
			return this;
		}

		/**
		 * Forces the given padding object to work as spacing data. Spacing will be applied to the cell
		 * instead of padding.
		 */
		public CellWidgetBuilder<Widget> useSpacing () {
			useSpacing = true;
			return this;
		}

		/** Widget will expand on X axis. */
		public CellWidgetBuilder<Widget> expandX () {
			expandX = true;
			return this;
		}

		/** Widget will expand on Y axis. */
		public CellWidgetBuilder<Widget> expandY () {
			expandY = true;
			return this;
		}

		/** Widget will fill X axis. Often used with expansion or fixed width setting. */
		public CellWidgetBuilder<Widget> fillX () {
			fillX = true;
			return this;
		}

		/** Widget will fill Y axis. Often used with expansion or fixed width setting. */
		public CellWidgetBuilder<Widget> fillY () {
			fillY = true;
			return this;
		}

		/**
		 * @param alignment will be used to align the widget in the cell area it has. May need expansion to
		 * take any effect.
		 */
		public CellWidgetBuilder<Widget> align (final Alignment alignment) {
			this.alignment = alignment;
			return this;
		}

		/** @param width set as min, preffered and max width. Has to be higher than 0 or it will be ignored. */
		public CellWidgetBuilder<Widget> width (final int width) {
			this.width = width;
			return this;
		}

		/** @param height set as min, preffered and max height. Has to be higher than 0 or it will be ignored. */
		public CellWidgetBuilder<Widget> height (final int height) {
			this.height = height;
			return this;
		}

		/**
		 * @param minWidth forces the minimum width of the widget. Note that it overrides width() setting for
		 * the min width. Has to be higher than 0 or it will be ignored.
		 */
		public CellWidgetBuilder<Widget> minWidth (final int minWidth) {
			this.minWidth = minWidth;
			return this;
		}

		/**
		 * @param minHeight forces the minimum height of the widget. Note that it overrides height() setting
		 * for the min height. Has to be higher than 0 or it will be ignored.
		 */
		public CellWidgetBuilder<Widget> minHeight (final int minHeight) {
			this.minHeight = minHeight;
			return this;
		}
	}
}
